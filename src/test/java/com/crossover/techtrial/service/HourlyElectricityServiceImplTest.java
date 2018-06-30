package com.crossover.techtrial.service;


import com.crossover.techtrial.model.HourlyElectricity;
import com.crossover.techtrial.model.Panel;
import com.crossover.techtrial.repository.HourlyElectricityRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
public class HourlyElectricityServiceImplTest {

    private static final Long ANY_PANEL_ID = 42L;
    private static final LocalDateTime ANY_CREATED_AT = LocalDateTime.parse("1970-01-01T00:00:00");
    private static final LocalDateTime NOW = LocalDateTime.now();

    private HourlyElectricity anyHourlyElectricity;

    @MockBean
    private HourlyElectricityRepository hourlyElectricityRepositoryMock;

    private HourlyElectricityService underTest;

    @Before
    public void setUp() {
        underTest = new HourlyElectricityServiceImpl(hourlyElectricityRepositoryMock);
        anyHourlyElectricity = makeAnyHourlyElectricity();
    }

    @Test
    public void shouldSaveHourlyElectricity() {
        HourlyElectricity expectedHourlyElectricity = makeAnyHourlyElectricity();
        expectedHourlyElectricity.setId(1L);
        given(hourlyElectricityRepositoryMock.save(anyHourlyElectricity)).willReturn(expectedHourlyElectricity);

        HourlyElectricity actualHourlyElectricity = underTest.save(anyHourlyElectricity);

        assertEquals(expectedHourlyElectricity, actualHourlyElectricity);
    }

    @Test
    public void shouldGetAllHourlyElectricityByPanelId() {
        PageRequest pageable = PageRequest.of(1, 2);
        List<HourlyElectricity> hourlyElectricities = Arrays.asList(anyHourlyElectricity);
        Page<HourlyElectricity> expectedHourlyElectricityPage = new PageImpl(hourlyElectricities, pageable, 2);
        given(hourlyElectricityRepositoryMock.findAllByPanelIdOrderByReadingAtDesc(ANY_PANEL_ID, pageable))
            .willReturn(expectedHourlyElectricityPage);

        Page<HourlyElectricity> actualHourlyElectricityPage = underTest.getAllHourlyElectricityByPanelId(ANY_PANEL_ID, pageable);

        assertEquals(expectedHourlyElectricityPage, actualHourlyElectricityPage);
    }

    @Test
    public void shouldGetHourlyElectricityByPanelIdBetweenDates() {
        List<HourlyElectricity> expectedHourlyElectricities = Arrays.asList(makeAnyHourlyElectricity());
        given(hourlyElectricityRepositoryMock.findAllByPanelIdAndReadingAtBetweenOrderByReadingAtDesc(ANY_PANEL_ID, ANY_CREATED_AT, NOW))
            .willReturn(expectedHourlyElectricities);

        List<HourlyElectricity> actualHourlyElectricities = underTest.getHourlyElectricityByPanelIdBetweenDates(ANY_PANEL_ID, ANY_CREATED_AT, NOW);

        assertEquals(expectedHourlyElectricities, actualHourlyElectricities);
    }

    private HourlyElectricity makeAnyHourlyElectricity() {
        return HourlyElectricity.builder()
            .panel(makeAnyPanel())
            .generatedElectricity(123L)
            .readingAt(LocalDateTime.now())
            .build();
    }

    private Panel makeAnyPanel() {
        return Panel.builder()
            .id(ANY_PANEL_ID)
            .serial("1234567890123456")
            .createdAt(ANY_CREATED_AT)
            .build();
    }
}
