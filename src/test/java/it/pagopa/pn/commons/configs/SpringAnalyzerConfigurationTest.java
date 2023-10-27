package it.pagopa.pn.commons.configs;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import it.pagopa.pn.commons.abstractions.ParameterConsumer;
import it.pagopa.pn.commons.utils.ValidateUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;


class SpringAnalyzerConfigurationTest {


    @Test
    void createSimpleMeterRegistry() {
        SimpleMeterRegistry simpleMeterRegistry = new SpringAnalyzerConfiguration().simpleMeterRegistry();
        Assertions.assertNotNull( simpleMeterRegistry );
    }

    @Test
    void createMetricsEndpoint() {
        MetricsEndpoint metricsEndpoint =  new SpringAnalyzerConfiguration().metricsEndpoint(new SimpleMeterRegistry());
        Assertions.assertNotNull( metricsEndpoint );
    }

}
