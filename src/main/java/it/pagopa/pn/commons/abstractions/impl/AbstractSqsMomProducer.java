package it.pagopa.pn.commons.abstractions.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import it.pagopa.pn.api.dto.events.GenericEvent;
import it.pagopa.pn.commons.abstractions.MomProducer;
import it.pagopa.pn.commons.exceptions.PnInternalException;
import org.jetbrains.annotations.NotNull;
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
        Class<?> payloadClass = getPayloadClass(msgClass);
        this.objectWriter = objectMapper.writerFor(payloadClass);

        this.queueUrl = getQueueUrl(sqsClient, topic);
    }

    @NotNull
    private Class<?> getPayloadClass(Class<T> msgClass) {
        try {
            return msgClass.getDeclaredField("payload").getType();
        } catch (NoSuchFieldException exc) {
            throw new PnInternalException( "Preparing sqs producer " + this.getClass(), exc );
        }
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
                            .messageBody(toJson(msg.getPayload()))
                            .id(msg.getHeader().getEventId())
                            .messageAttributes(getSqSHeader(msg.getHeader()))
                            .build()
                        )
                        .collect(Collectors.toList()))
                .build());

    }

    private String toJson(Object obj) {
        try {
            return objectWriter.writeValueAsString(obj);
        } catch (JsonProcessingException exc) {
            throw new IllegalStateException(exc);
        }
    }

    private Map<String, MessageAttributeValue> getSqSHeader(StandardEventHeader header) {
               
        Map<String, MessageAttributeValue> map = new HashMap<>();
        
        map.put(PN_EVENT_HEADER_IUN, newStringAttributeValue(header.getIun()));
        map.put(PN_EVENT_HEADER_EVENT_ID, newStringAttributeValue(header.getEventId()));
        map.put(PN_EVENT_HEADER_EVENT_TYPE, newStringAttributeValue(header.getEventType()));
        map.put(PN_EVENT_HEADER_CREATED_AT, newStringAttributeValue(header.getCreatedAt().toString()));
        map.put(PN_EVENT_HEADER_PUBLISHER, newStringAttributeValue(header.getPublisher()));
       
        return map;
    }

    private MessageAttributeValue newStringAttributeValue(String value) {
        return MessageAttributeValue.builder()
                .dataType("String")
                .stringValue(value)
                .build();
    }
}
