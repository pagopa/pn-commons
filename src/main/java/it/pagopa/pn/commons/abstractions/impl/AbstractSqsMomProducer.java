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
import static it.pagopa.pn.api.dto.events.StandardEventHeader.*;
import it.pagopa.pn.api.dto.events.StandardEventHeader;
        
import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;

public abstract class AbstractSqsMomProducer<T extends GenericEvent> implements MomProducer<T> {

    private final SqsClient sqsClient;
    private final ObjectWriter objectWriter;
    private final String queueUrl;

    protected AbstractSqsMomProducer(SqsClient sqsClient, String topic, ObjectMapper objectMapper, Class<T> msgClass) {
        this.sqsClient = sqsClient;
        this.objectWriter = objectMapper.writerFor(msgClass);

        this.queueUrl = getQueueUrl(sqsClient, topic);
    }

    private String getQueueUrl(SqsClient sqsClient, String topic) {
        return sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(topic).build()).queueUrl();
    }

    @Override
    public void push(List<T> msges) {
        
        sqsClient.sendMessageBatch(SendMessageBatchRequest.builder()
                .queueUrl(this.queueUrl)
                .entries(msges.stream()
                        .map(msg -> SendMessageBatchRequestEntry.builder()
                        .messageBody(toJson(msg))
                        .id(msg.getHeader().getEventId())
                        .messageAttributes(getSqSHeader(msg.getHeader()))
                        .build()
                        )
                        .collect(Collectors.toList()))
                .build());

    }

    private String toJson(T msg) {
        try {
            return objectWriter.writeValueAsString(msg);
        } catch (JsonProcessingException exc) {
            throw new IllegalStateException(exc);
        }
    }

    private Map<String, MessageAttributeValue> getSqSHeader(StandardEventHeader header) {
               
        Map<String, MessageAttributeValue> map = new HashMap<>();
        
        map.put(PN_EVENT_HEADER_IUN, 
                MessageAttributeValue.builder().stringValue(header.getIun()).build());
        map.put(PN_EVENT_HEADER_EVENT_ID, 
                MessageAttributeValue.builder().stringValue(header.getEventId()).build());
        map.put(PN_EVENT_HEADER_EVENT_TYPE, 
                MessageAttributeValue.builder().stringValue(header.getEventType()).build());
        map.put(PN_EVENT_HEADER_CREATED_AT, 
                MessageAttributeValue.builder().stringValue(header.getCreatedAt().toString()).build());
        map.put(PN_EVENT_HEADER_PUBLISHER, 
                MessageAttributeValue.builder().stringValue(header.getPublisher()).build());
       
        return map;

    }
}
