package it.pagopa.pn.commons.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class ClockConfig {

    @Bean
    public Clock defaultClock() {
        return Clock.systemUTC();
    }
}
