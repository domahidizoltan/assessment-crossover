package com.crossover.techtrial.controller;

import com.crossover.techtrial.converter.DailyElectricitiesConverter;
import com.crossover.techtrial.dto.DailyElectricity;
import com.crossover.techtrial.model.HourlyElectricity;
import com.crossover.techtrial.model.Panel;
import com.crossover.techtrial.service.HourlyElectricityService;
import com.crossover.techtrial.service.PanelService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * PanelControllerTest class will test all APIs in PanelController.java.
 * @author Crossover
 *
 */

@RunWith(SpringRunner.class)
@WebMvcTest
@EnableSpringDataWebSupport
public class PanelControllerTest {

  private static final String ANY_SERIAL = "1234567890123456";
  private static final String HOURLY_ELECTRICITY_URL = "/api/panels/{panel-serial}/hourly";
  private static final String DAILY_ELECTRICITY_URL = "/api/panels/{panel-serial}/daily";
  private static final String FIRST_ITEM_QUERY_STRING = "?page=0&size=1";
  private static final Long ANY_PANEL_ID = 42L;
  private static final LocalDateTime ANY_CREATED_AT = LocalDateTime.parse("2012-01-15T00:00:00");

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private PanelService panelServiceMock;

  @MockBean
  private HourlyElectricityService hourlyElectricityServiceMock;

  @MockBean
  private DailyElectricitiesConverter dailyElectricitiesConverterMock;

  @Before
  public void setUp() {
    Mockito.reset(panelServiceMock, hourlyElectricityServiceMock);
  }

  @Test
  public void shouldRegisterPanel() throws Exception {
    Panel anyPanel = makeAnyPanel();
    RequestBuilder registerPanelRequest = post("/api/register")
        .content(asJson(anyPanel))
        .contentType(MediaType.APPLICATION_JSON);

    mockMvc.perform(registerPanelRequest)
        .andExpect(status().isAccepted());

    verify(panelServiceMock, times(1)).register(anyPanel);
  }

  @Test
  public void shouldSaveHourlyElectricity() throws Exception {
    HourlyElectricity anyHourlyElectricity = makeAnyHourlyElectricity();
    HourlyElectricity expectedHourlyElectricity = makeAnyHourlyElectricity();
    expectedHourlyElectricity.setId(1L);
    willReturn(makeAnyPanel()).given(panelServiceMock).findBySerial(ANY_SERIAL);
    willReturn(expectedHourlyElectricity).given(hourlyElectricityServiceMock).save(anyHourlyElectricity);
    RequestBuilder saveHourlyElectricityRequest = post(HOURLY_ELECTRICITY_URL, ANY_SERIAL)
        .content(asJson(anyHourlyElectricity))
        .contentType(MediaType.APPLICATION_JSON);

    mockMvc.perform(saveHourlyElectricityRequest)
        .andExpect(content().string(asJson(expectedHourlyElectricity)))
        .andExpect(status().isOk());
  }

  @Test
  public void shouldGetHourlyElectricity() throws Exception {
    HourlyElectricity anyHourlyElectricity = makeAnyHourlyElectricity();
    Page<HourlyElectricity> expectedHourlyElectricitiesPage = makeHourlyElectricitiesPage(anyHourlyElectricity);
    Pageable firstPage = PageRequest.of(0, 1);
    willReturn(makeAnyPanel()).given(panelServiceMock).findBySerial(ANY_SERIAL);
    willReturn(expectedHourlyElectricitiesPage).given(hourlyElectricityServiceMock)
        .getAllHourlyElectricityByPanelId(ANY_PANEL_ID, firstPage);

    RequestBuilder getHourlyElectricityRequest = get(HOURLY_ELECTRICITY_URL + FIRST_ITEM_QUERY_STRING, ANY_SERIAL);

    mockMvc.perform(getHourlyElectricityRequest)
        .andExpect(content().string(asJson(expectedHourlyElectricitiesPage)))
        .andExpect(status().isOk());
  }

  @Test
  public void shouldReturnNotFoundWhenHourlyElectricityNotExists() throws Exception {
    RequestBuilder getHourlyElectricityRequest = get(HOURLY_ELECTRICITY_URL, "non-existing-serial");

    mockMvc.perform(getHourlyElectricityRequest)
        .andExpect(status().isNotFound());
  }

  @Test
  public void requestShouldReturnGeneralErrorMessageOnException() throws Exception {
    willThrow(RuntimeException.class).given(panelServiceMock).findBySerial(anyString());
    RequestBuilder getHourlyElectricityRequest = get(HOURLY_ELECTRICITY_URL, ANY_SERIAL);

    mockMvc.perform(getHourlyElectricityRequest)
        .andExpect(jsonPath("$.message", is("Unable to process this request.")))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void shouldReturnNotFoundWhenPanelNotExistsForDailyElectricityData() throws Exception {
    RequestBuilder getDailyElectricityRequest = get(DAILY_ELECTRICITY_URL, ANY_SERIAL);

    mockMvc.perform(getDailyElectricityRequest)
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldReturnListOfDailyElectricitiesUntilYesterday() throws Exception {
    List<HourlyElectricity> hourlyElectricities = Arrays.asList(
        makeHourlyElectricity(100L, "2012-02-03T00:00:00"),
        makeHourlyElectricity(200L, "2012-02-03T08:42:09")
    );
    List<DailyElectricity> expectedDailyElectricities = Arrays.asList(
        makeDailyElectricity("2012-02-03", 100L, 150D, 200L, 300L)
    );

    willReturn(makeAnyPanel()).given(panelServiceMock).findBySerial(ANY_SERIAL);
    willReturn(hourlyElectricities).given(hourlyElectricityServiceMock)
        .getHourlyElectricityByPanelIdBetweenDates(ANY_PANEL_ID, ANY_CREATED_AT, getEndOfYesterdayDate());
    willReturn(expectedDailyElectricities).given(dailyElectricitiesConverterMock)
        .convert(hourlyElectricities);
    RequestBuilder getDailyElectricityRequest = get(DAILY_ELECTRICITY_URL, ANY_SERIAL);

    mockMvc.perform(getDailyElectricityRequest)
        .andExpect(content().string(asJson(expectedDailyElectricities)))
        .andExpect(status().isOk());
  }

  private LocalDateTime getEndOfYesterdayDate() {
    return LocalDateTime.now()
        .withHour(0)
        .withMinute(0)
        .withSecond(0)
        .withNano(0)
        .minusNanos(1);
  }

  private Page<HourlyElectricity> makeHourlyElectricitiesPage(HourlyElectricity anyHourlyElectricity) {
    List<HourlyElectricity> hourlyElectricities = Arrays.asList(anyHourlyElectricity);
    Pageable firstItemPage = PageRequest.of(0, 1);
    return new PageImpl<>(hourlyElectricities, firstItemPage, 1);
  }

  private String asJson(Object object) throws JsonProcessingException {
    return objectMapper.writeValueAsString(object);
  }

  private Panel makeAnyPanel() {
    return Panel.builder()
        .id(ANY_PANEL_ID)
        .serial(ANY_SERIAL)
        .brand("anyBrand")
        .latitude(12.123456)
        .longitude(23.234567)
        .createdAt(ANY_CREATED_AT)
        .build();
  }

  private HourlyElectricity makeAnyHourlyElectricity() {
    return HourlyElectricity.builder()
        .panel(makeAnyPanel())
        .generatedElectricity(123L)
        .readingAt(LocalDateTime.now())
        .build();
  }

  private HourlyElectricity makeHourlyElectricity(Long electricity, String dateString) {
    return HourlyElectricity.builder()
        .generatedElectricity(electricity)
        .readingAt(LocalDateTime.parse(dateString))
        .build();
  }

  private DailyElectricity makeDailyElectricity(String dateString, Long min, Double avg, Long max, Long sum) {
    return DailyElectricity.builder()
        .date(LocalDate.parse(dateString))
        .min(min)
        .average(avg)
        .max(max)
        .sum(sum)
        .build();
  }

}

