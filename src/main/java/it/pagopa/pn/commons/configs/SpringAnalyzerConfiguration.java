package it.pagopa.pn.commons.configs;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import it.pagopa.pn.commons.abstractions.ParameterConsumer;
import it.pagopa.pn.commons.utils.ValidateUtils;
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
