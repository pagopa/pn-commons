package it.pagopa.pn.commons.utils.metrics;

import it.pagopa.pn.commons.utils.cloudwatch.CloudWatchMetricHandler;
import lombok.CustomLog;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.boot.actuate.metrics.MetricsEndpoint.MetricResponse;
import org.springframework.scheduling.annotation.Scheduled;
import software.amazon.awssdk.services.cloudwatch.model.Dimension;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@CustomLog
public class SpringAnalyzer {
    private final CloudWatchMetricHandler cloudWatchMetricHandler;
    private final MetricsEndpoint metricEndpoint;
    @Value("${spring.application.name}")
    private String applicationName;
    @Value("${pn.ecs.task.id}")
    private String taskId;
    @Value("${pn.analyzer.params}")
    protected List<String> metrics;

    protected List<String> getMetrics() {
        return this.metrics;
    }

    public SpringAnalyzer(CloudWatchMetricHandler cloudWatchMetricHandler, MetricsEndpoint metricsEndpoint) {
        this.metricEndpoint = metricsEndpoint;
        this.cloudWatchMetricHandler = cloudWatchMetricHandler;
        Runtime.getRuntime().addShutdownHook(new ShutdownHook(this));
    }

    @PostConstruct
    public void init() {
        log.info("Metric Instance for SpringAnalyzer Activation: {}", applicationName + "-" + taskId);
    }

    @Scheduled(cron = "${pn.analyzer.cloudwatch-metric-cron}")
    private void scheduledSendMetrics() throws IOException, InterruptedException {
        metrics.forEach(this::createMetricAndSendCloudwatch);
    }

    private void createMetricAndSendCloudwatch(String metricName) {
        List<String> tag = new ArrayList<>();
        String namespace = "SpringAnalyzer" + "-" + applicationName;
        MetricResponse response = this.metricEndpoint.metric(metricName, tag);
        Dimension dimension = Dimension.builder()
                .name("ApplicationName_TaskId")
                .value(applicationName + "_" + taskId)
                .build();
        dimension = customizedDimension(dimension, metricName);
        log.trace("Sending Cloudwatch information {}= {}", metricName, response.getMeasurements().get(0).getValue());
        cloudWatchMetricHandler.sendMetric(namespace, dimension, metricName, response.getMeasurements().get(0).getValue()).
                subscribe(putMetricDataResponse -> {
                            metricSuccessfullSendListener(metricName);
                            log.trace("[{}] PutMetricDataResponse: {}", namespace, putMetricDataResponse);
                        },
                throwable -> log.warn(String.format("[%s] Error sending metric", namespace), throwable));
    }

    protected Dimension customizedDimension(Dimension dimension, String metricName){
        return dimension;
    }

    protected void metricSuccessfullSendListener(String metricName){}

    static class ShutdownHook extends Thread {
        final private SpringAnalyzer analyzer;

        public ShutdownHook(SpringAnalyzer analyzer) {
            this.analyzer = analyzer;
        }
        @Override
        public void run() {
            try {
                log.trace("Closing application. Sending last metrics.");
                this.analyzer.scheduledSendMetrics();
            } catch (IOException | InterruptedException e) {
                log.trace("Error sending last metrics");
                throw new RuntimeException(e);
            }
        }
    }
}