package com.crossover.techtrial.converter;

import com.crossover.techtrial.dto.DailyElectricity;
import com.crossover.techtrial.model.HourlyElectricity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.convert.converter.Converter;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.stream.Collectors;

public class DailyElectricitiesConverter implements Converter<List<HourlyElectricity>, List<DailyElectricity>> {

    @Override
    public List<DailyElectricity> convert(final List<HourlyElectricity> hourlyElectricities) {
        Map<LocalDate, LongSummaryStatistics> dailyElectricityStatistics = aggregateToDailyElectricities(hourlyElectricities);
        List<DailyElectricity> dailyElectricityForPanel = dailyElectricityStatistics.entrySet().stream()
            .map(entry -> createDailyElectricity(entry))
            .sorted(Comparator.comparing(DailyElectricity::getDate))
            .collect(Collectors.toList());
        return dailyElectricityForPanel;
    }


    private DailyElectricity createDailyElectricity(Map.Entry<LocalDate, LongSummaryStatistics> entry) {
        LongSummaryStatistics stat = entry.getValue();
        return DailyElectricity.builder()
            .date(entry.getKey())
            .min(stat.getMin())
            .max(stat.getMax())
            .average(stat.getAverage())
            .sum(stat.getSum())
            .build();
    }


    private Map<LocalDate, LongSummaryStatistics> aggregateToDailyElectricities(List<HourlyElectricity> hourlyElectricities) {
        return hourlyElectricities.stream()
            .map(metric -> new DateValuePair(metric.getReadingAt().toLocalDate(), metric.getGeneratedElectricity()))
            .collect(Collectors.groupingBy(DateValuePair::getDate, Collectors.summarizingLong(DateValuePair::getValue)));
    }

    @Getter
    @AllArgsConstructor
    private class DateValuePair {
        private LocalDate date;
        private Long value;
    }
}
