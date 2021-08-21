package it.pagopa.pn.commons.abstractions.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import it.pagopa.pn.api.dto.events.GenericEvent;
import it.pagopa.pn.commons.abstractions.MomProducer;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequestEntry;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractSqsMomProducer<T extends GenericEvent> implements MomProducer<T> {

    private final SqsClient sqsClient;
    private final ObjectWriter objectWriter;
    private final String queueUrl;

    protected AbstractSqsMomProducer(SqsClient sqsClient, String topic, ObjectMapper objectMapper, Class<T> msgClass) {
        this.sqsClient = sqsClient;
        this.objectWriter = objectMapper.writerFor( msgClass );

        this.queueUrl = getQueueUrl(sqsClient, topic);
    }

    private String getQueueUrl(SqsClient sqsClient, String topic) {
        return sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(topic).build()).queueUrl();
    }


    @Override
    public void push( List<T> msges) {

        // FIXME: gestire gli header
        sqsClient.sendMessageBatch( SendMessageBatchRequest.builder()
                .queueUrl( this.queueUrl )
                .entries( msges.stream()
                        .map( msg -> SendMessageBatchRequestEntry.builder()
                                .messageBody( toJson( msg ) )
                                .id( msg.getHeader().getEventId() )
                                .build()
                        )
                        .collect(Collectors.toList()))
                .build());

    }

    private String toJson( T msg ) {
        try {
            return objectWriter.writeValueAsString( msg );
        } catch (JsonProcessingException exc) {
            throw new IllegalStateException( exc );
        }
    }

}
