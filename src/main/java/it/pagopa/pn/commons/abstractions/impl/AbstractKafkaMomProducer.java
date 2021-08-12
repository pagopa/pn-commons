package it.pagopa.pn.commons.abstractions.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import it.pagopa.pn.api.dto.events.GenericEvent;
import it.pagopa.pn.commons.abstractions.MomProducer;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;

public abstract class AbstractKafkaMomProducer<T extends GenericEvent> implements MomProducer<T> {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectWriter objectWriter;
    private final String topic;

    protected AbstractKafkaMomProducer(KafkaTemplate<String, String> kafkaTemplate, String topic, ObjectMapper objectMapper, Class<T> msgClass) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
        this.objectWriter = objectMapper.writerFor( msgClass );
    }

    @Override
    public void push( List<T> msges) {
        for( T msg: msges ) {
            try {
                String jsonMessage = objectWriter.writeValueAsString( msg );
                kafkaTemplate.send( topic, jsonMessage );

            } catch (JsonProcessingException exc) {
                throw new IllegalStateException( exc );
            }
        }
    }
}
