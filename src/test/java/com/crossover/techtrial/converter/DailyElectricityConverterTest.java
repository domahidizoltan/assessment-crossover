package com.crossover.techtrial.converter;

import com.crossover.techtrial.dto.DailyElectricity;
import com.crossover.techtrial.model.HourlyElectricity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
public class DailyElectricityConverterTest {

    private DailyElectricitiesConverter underTest;

    @Before
    public void setUp() {
        underTest = new DailyElectricitiesConverter();
    }

    @Test
    public void shouldReturnEmptyListWhenHourlyElectricityDataIsEmpty() {
        List<DailyElectricity> actualDailyElectricities = underTest.convert(new ArrayList<>());
        assertTrue(actualDailyElectricities.isEmpty());
    }

    @Test
    public void shouldConvertListToDailyElectricities() {
        List<HourlyElectricity> hourlyElectricities = Arrays.asList(
            makeHourlyElectricity(100L, "2012-02-03T00:00:00"),
            makeHourlyElectricity(200L, "2012-02-03T08:42:09"),
            makeHourlyElectricity(200L, "2012-02-03T23:59:59"),
            makeHourlyElectricity(12L, "2014-11-17T13:12:35"),
            makeHourlyElectricity(1000L, "2018-06-30T02:45:12"),
            makeHourlyElectricity(1200L, "2018-06-30T22:22:22")
        );
        List<DailyElectricity> expectedDailyElectricities = Arrays.asList(
            makeDailyElectricity("2012-02-03", 100L, 166.66666666666666D, 200L, 500L),
            makeDailyElectricity("2014-11-17", 12L, 12D, 12L, 12L),
            makeDailyElectricity("2018-06-30", 1000L, 1100D, 1200L, 2200L)
        );

        List<DailyElectricity> actualDailyElectricities = underTest.convert(hourlyElectricities);
        assertEquals(actualDailyElectricities, expectedDailyElectricities);
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
