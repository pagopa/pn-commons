package it.pagopa.pn.commons.utils.dynamodb;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Statistic;
import it.pagopa.pn.commons.pnclients.RestTemplateFactory;
import it.pagopa.pn.commons.utils.cloudwatch.CloudWatchMetricHandler;
import it.pagopa.pn.commons.utils.metrics.SpringAnalyzer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.boot.actuate.metrics.MetricsEndpoint.MetricResponse;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.util.*;

import static org.mockito.Mockito.*;

public class SpringAnalyzerTest {

    @Mock
    private CloudWatchMetricHandler cloudWatchMetricHandler;
    @Mock
    private MetricsEndpoint metricsEndpoint;
    @InjectMocks
    private SpringAnalyzer springAnalyzer;

    @BeforeEach
    public void init(){
        this.springAnalyzer = new SpringAnalyzer(cloudWatchMetricHandler, metricsEndpoint);
    }
    @Test
    public void testScheduledSendMetrics() {
        String metricName = "customMetric";

        metricsEndpoint.metric(metricName, new ArrayList<>());
        Assertions.assertDoesNotThrow(() -> springAnalyzer.scheduledSendMetrics());
    }
}