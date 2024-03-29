package it.pagopa.pn.commons.utils.metrics;

import it.pagopa.pn.commons.utils.metrics.cloudwatch.CloudWatchMetricHandler;
import lombok.CustomLog;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.scheduling.annotation.Scheduled;
import software.amazon.awssdk.services.cloudwatch.model.Dimension;
import software.amazon.awssdk.services.cloudwatch.model.MetricDatum;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@CustomLog
public class SpringAnalyzer {
    private final CloudWatchMetricHandler cloudWatchMetricHandler;
    private final MetricsEndpoint metricEndpoint;
    @Value("${spring.application.name}")
    private String applicationName;
    @Value("#{'${pn.ecs.uri}'.split('[/]')[4].split('[-]')[0]}")
    private String taskId;
    @Value("#{'${pn.analyzer.params}'.split(',')}")
    protected List<String> metrics;

    protected List<String> getMetrics() {
        return this.metrics;
    }

    public SpringAnalyzer(CloudWatchMetricHandler cloudWatchMetricHandler, MetricsEndpoint metricsEndpoint) {
        this.metricEndpoint = metricsEndpoint;
        this.cloudWatchMetricHandler = cloudWatchMetricHandler;
        Runtime.getRuntime().addShutdownHook(new ShutdownHook(this));
        if(this.metrics == null) {
            this.metrics = new ArrayList<>();
        }
    }

    @PostConstruct
    public void init() {
        log.info("Metric Instance for SpringAnalyzer Activation: {}", applicationName + "-" + taskId);
    }

    @Scheduled(cron = "${pn.analyzer.cloudwatch-metric-cron}")
    public void scheduledSendMetrics() {
        String namespace = "SpringAnalyzer" + "-" + applicationName;
        Collection<MetricDatum> metricDatumCollection = new ArrayList<>();
        metrics.forEach(value -> {
            if(!value.equals("")) {
                MetricDatum metricDatum = this.createMetricCloudwatch(value);
                if(metricDatum != null) {
                    metricDatumCollection.add(metricDatum);
                }
            }
        });
        if(!metricDatumCollection.isEmpty()) {
            cloudWatchMetricHandler.sendMetricCollection(namespace, metricDatumCollection).
                    subscribe(putMetricDataResponse -> {
                                metricDatumCollection.forEach(metricDatum -> {
                                    metricSuccessfullSendListener(metricDatum.metricName());
                                });
                                log.trace("[{}] PutMetricDataResponse: {}", namespace, putMetricDataResponse.toString());
                            },
                            throwable -> log.warn(String.format("[%s] Error sending metrics", namespace), throwable));
        }
        else {
            log.trace("No metrics to send");
        }
    }


    private MetricDatum createMetricCloudwatch(String metricName) {
        List<String> tag = new ArrayList<>();
        String namespace = "SpringAnalyzer" + "-" + applicationName;
        MetricsEndpoint.MetricResponse response = this.metricEndpoint.metric(metricName, tag);
        if (response == null) {
            log.warn(String.format("[%s] Metric not available", namespace));
        } else {
            Dimension dimension = Dimension.builder()
                    .name("ApplicationName_TaskId")
                    .value(applicationName + "_" + taskId)
                    .build();
            dimension = customizedDimension(dimension, metricName);
            if (!response.getMeasurements().isEmpty()) {
                log.trace("Sending Cloudwatch information {}= {}", metricName, response.getMeasurements().get(0).getValue());
                return  MetricDatum.builder()
                        .metricName(metricName)
                        .value(response.getMeasurements().get(0).getValue())
                        .dimensions(dimension)
                        .build();
            }
            else {
                log.trace("Measurement {} not available", metricName);
            }
        }
        return null;
    }

    protected Dimension customizedDimension(Dimension dimension, String metricName){
        //"metricName" used to customize dimension
        return dimension;
    }

    protected void metricSuccessfullSendListener(String metricName){
        //Used to reset metric in successfull listener
    }

    static class ShutdownHook extends Thread {
        private final SpringAnalyzer analyzer;

        public ShutdownHook(SpringAnalyzer analyzer) {
            this.analyzer = analyzer;
        }
        @Override
        public void run() {
            log.trace("Closing application. Sending last metrics.");
            this.analyzer.scheduledSendMetrics();
        }
    }
}
