package com.crossover.techtrial.bootstrap;

import com.crossover.techtrial.converter.DailyElectricitiesConverter;
import com.crossover.techtrial.repository.HourlyElectricityRepository;
import com.crossover.techtrial.repository.PanelRepository;
import com.crossover.techtrial.service.HourlyElectricityService;
import com.crossover.techtrial.service.HourlyElectricityServiceImpl;
import com.crossover.techtrial.service.PanelService;
import com.crossover.techtrial.service.PanelServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class AppConfiguration {


    @Bean
    PanelService panelService(final PanelRepository panelRepository) {
        return new PanelServiceImpl(panelRepository);
    }

    @Bean
    HourlyElectricityService hourlyElectricityService(final HourlyElectricityRepository hourlyElectricityRepository) {
        return new HourlyElectricityServiceImpl(hourlyElectricityRepository);
    }

    @Bean
    DailyElectricitiesConverter dailyElectricitiesConverter() {
        return new DailyElectricitiesConverter();
    }
}
