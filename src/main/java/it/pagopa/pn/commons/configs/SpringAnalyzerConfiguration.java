package it.pagopa.pn.commons.configs;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.context.annotation.Bean;

public class SpringAnalyzerConfiguration {

    @Bean
    public SimpleMeterRegistry simpleMeterRegistry() {
        return new SimpleMeterRegistry();
    }

    @Bean
    public MetricsEndpoint metricsEndpoint(SimpleMeterRegistry simpleMeterRegistry) {
        return new MetricsEndpoint(simpleMeterRegistry);
    }

}
