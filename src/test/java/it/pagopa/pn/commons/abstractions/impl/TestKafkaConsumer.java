package it.pagopa.pn.commons.abstractions.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.api.dto.events.PnDeliveryNewNotificationEvent;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.stereotype.Component;

@Component
public class TestKafkaConsumer extends AbstractKafkaMomConsumer<PnDeliveryNewNotificationEvent> {

    public TestKafkaConsumer(KafkaListenerContainerFactory listenerContainerFactory, ObjectMapper objectMapper) {
        super(
                listenerContainerFactory,
                KafkaProducerConsumerTestIT.TOPIC_NAME,
                KafkaProducerConsumerTestIT.GROUP_NAME,
                objectMapper,
                PnDeliveryNewNotificationEvent.class
            );
    }
}
