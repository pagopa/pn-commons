package it.pagopa.pn.commons.utils.metrics.cloudwatch;

import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;
import software.amazon.awssdk.services.cloudwatch.model.*;

import jakarta.annotation.PostConstruct;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;

@RequiredArgsConstructor
@CustomLog
public class CloudWatchMetricHandler {

    private final CloudWatchAsyncClient cloudWatchAsyncClient;

    @PostConstruct
    public void init() {
        log.info("Cloudwatch metric handler activated");
    }

    public Mono<PutMetricDataResponse> sendMetric(String namespace, Dimension dimension, String metricName, double value) {

        log.trace("Sending information to namespace=[{}] metricname=[{}]", namespace, metricName);

        PutMetricDataRequest metricDataRequest = createMetricDataRequest(metricName, dimension, namespace, value);

        return Mono.fromFuture(cloudWatchAsyncClient.putMetricData(metricDataRequest));

    }

    public Mono<PutMetricDataResponse> sendMetricCollection(String namespace, Collection<MetricDatum> metricDatumCollection) {

        log.trace("Sending collection to namespace=[{}]", namespace);

        PutMetricDataRequest metricDataRequest = createCollectionDataRequest(namespace, metricDatumCollection);

        return Mono.fromFuture(cloudWatchAsyncClient.putMetricData(metricDataRequest));

    }

    private PutMetricDataRequest createMetricDataRequest(String metricName, Dimension dimension, String namespace, double value){
        MetricDatum metricDatum = MetricDatum.builder()
                .metricName(metricName)
                .value(value)
                .dimensions(dimension)
                .unit(StandardUnit.COUNT)
                .timestamp(Instant.now())
                .build();

        return PutMetricDataRequest.builder()
                .namespace(namespace)
                .metricData(Collections.singletonList(metricDatum))
                .build();
    }

    private PutMetricDataRequest createCollectionDataRequest(String namespace, Collection<MetricDatum> metricDatumCollection){
        return PutMetricDataRequest.builder()
                .namespace(namespace)
                .metricData(metricDatumCollection)
                .build();
    }
}