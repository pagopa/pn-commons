package it.pagopa.pn.commons.utils;

import it.pagopa.pn.commons.utils.metrics.cloudwatch.CloudWatchMetricHandler;
import it.pagopa.pn.commons.utils.metrics.SpringAnalyzer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.util.logging.Level;

@ExtendWith(OutputCaptureExtension.class)
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

    @Test
    void testInit(CapturedOutput output) {
        this.springAnalyzer.init();
        Assertions.assertTrue(output.getOut().contains("Metric Instance for SpringAnalyzer Activation: null-null"));
    }


    static class Analyzer extends SpringAnalyzer{

        public Analyzer(CloudWatchMetricHandler cloudWatchMetricHandler, MetricsEndpoint metricsEndpoint) {
            super(cloudWatchMetricHandler, metricsEndpoint);
            this.getMetrics().add("customMetric");
            this.getMetrics().add("");
        }
    }
}