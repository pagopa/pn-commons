package it.pagopa.pn.commons.utils;

import it.pagopa.pn.commons.utils.cloudwatch.CloudWatchMetricHandler;
import it.pagopa.pn.commons.utils.metrics.SpringAnalyzer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;

class SpringAnalyzerTest {

    @Mock
    private CloudWatchMetricHandler cloudWatchMetricHandler;
    @Mock
    private MetricsEndpoint metricsEndpoint;
    @InjectMocks
    private SpringAnalyzer springAnalyzer;

    private Analyzer analyzer;

    @BeforeEach
    public void init(){
        MockitoAnnotations.openMocks(this);
        this.springAnalyzer = new SpringAnalyzer(cloudWatchMetricHandler, metricsEndpoint);
        this.analyzer = new Analyzer(cloudWatchMetricHandler, metricsEndpoint);
    }

    @Test
    void testScheduledSendMetrics() {
        Assertions.assertDoesNotThrow(() -> analyzer.scheduledSendMetrics());
    }

    static class Analyzer extends SpringAnalyzer{

        public Analyzer(CloudWatchMetricHandler cloudWatchMetricHandler, MetricsEndpoint metricsEndpoint) {
            super(cloudWatchMetricHandler, metricsEndpoint);
            this.getMetrics().add("customMetric");
        }
    }
}