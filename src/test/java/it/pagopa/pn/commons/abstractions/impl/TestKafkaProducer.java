package it.pagopa.pn.commons.abstractions.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.api.dto.events.NewNotificationEvent;
import it.pagopa.pn.commons.abstractions.impl.AbstractKafkaMomProducer;
import it.pagopa.pn.commons.abstractions.impl.KafkaProducerConsumerTestIT;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TestKafkaProducer extends AbstractKafkaMomProducer<NewNotificationEvent> {

    public TestKafkaProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        super(kafkaTemplate, KafkaProducerConsumerTestIT.TOPIC_NAME, objectMapper, NewNotificationEvent.class);
    }
}
