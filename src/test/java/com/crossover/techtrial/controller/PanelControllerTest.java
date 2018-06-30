package com.crossover.techtrial.controller;

import com.crossover.techtrial.model.HourlyElectricity;
import com.crossover.techtrial.model.Panel;
import com.crossover.techtrial.service.HourlyElectricityService;
import com.crossover.techtrial.service.PanelService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
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

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest
@EnableSpringDataWebSupport
public class PanelControllerTest {

  private static final String ANY_SERIAL = "1234567890123456";
  private static final String HOURLY_ELECTRICITY_URL = "/api/panels/{panel-serial}/hourly";
  private static final String FIRST_ITEM_QUERY_STRING = "?page=0&size=1";
  private static final Long ANY_PANEL_ID = 42L;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private PanelService panelServiceMock;

  @MockBean
  private HourlyElectricityService hourlyElectricityServiceMock;

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
    given(hourlyElectricityServiceMock.save(anyHourlyElectricity))
        .willReturn(expectedHourlyElectricity);
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
    given(panelServiceMock.findBySerial(ANY_SERIAL))
        .willReturn(makeAnyPanel());
    given(hourlyElectricityServiceMock.getAllHourlyElectricityByPanelId(eq(ANY_PANEL_ID), any(Pageable.class)))
        .willReturn(expectedHourlyElectricitiesPage);

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
    given(panelServiceMock.findBySerial(anyString()))
        .willThrow(RuntimeException.class);
    RequestBuilder getHourlyElectricityRequest = get(HOURLY_ELECTRICITY_URL, ANY_SERIAL);

    mockMvc.perform(getHourlyElectricityRequest)
        .andExpect(jsonPath("$.message", is("Unable to process this request.")))
        .andExpect(status().isBadRequest());
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
    Panel panel = new Panel();
    panel.setId(ANY_PANEL_ID);
    panel.setSerial(ANY_SERIAL);
    panel.setBrand("anyBrand");
    panel.setLatitude(12.123456);
    panel.setLongitude(23.234567);
    return panel;
  }

  private HourlyElectricity makeAnyHourlyElectricity() {
    HourlyElectricity data = new HourlyElectricity();
    data.setPanel(makeAnyPanel());
    data.setGeneratedElectricity(123L);
    data.setReadingAt(LocalDateTime.now());
    return data;
  }
}
