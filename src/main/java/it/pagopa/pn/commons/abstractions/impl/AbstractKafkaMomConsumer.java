package it.pagopa.pn.commons.abstractions.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import it.pagopa.pn.commons.abstractions.MomConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.MessageListenerContainer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class AbstractKafkaMomConsumer<T> implements MomConsumer<T>, AutoCloseable {

    private final MessageListenerContainer listenerContainer;
    private final BlockingQueue<T> receivedMessages;
    private final ObjectReader objectReader;

    public AbstractKafkaMomConsumer(
            KafkaListenerContainerFactory<MessageListenerContainer> listenerContainerFactory,
            String topic,
            String groupId,
            ObjectMapper objectMapper,
            Class<T> msgClass
    ) {
        this.objectReader = objectMapper.readerFor( msgClass );

        this.receivedMessages = new LinkedBlockingQueue<>();

        this.listenerContainer = newMessageListenerContainer(
                topic,
                groupId,
                listenerContainerFactory,
                receivedMessages
            );
    }

    @Override
    public List<T> poll( Duration maxPollTime) {
        List<T> result = new ArrayList<>();

        long remainingMillis = maxPollTime.toMillis();
        while( remainingMillis > 0 ) {
            long start = System.currentTimeMillis();

            T msg = pollOne( remainingMillis );
            if( msg != null ) { // quando scade il tempo il messaggio è null
                result.add( msg );
            }


            long usedTime = System.currentTimeMillis() - start;
            remainingMillis = remainingMillis - usedTime;
        }
        return result;
    }

    private T pollOne(long remainingMillis) {
        T msg;
        try {
            msg = receivedMessages.poll(remainingMillis, TimeUnit.MILLISECONDS );
        } catch (InterruptedException exc) {
            throw new IllegalStateException( exc );
        }
        return msg;
    }


    private MessageListenerContainer newMessageListenerContainer(
            String topic,
            String groupId,
            KafkaListenerContainerFactory<MessageListenerContainer> listenerContainerFactory,
            BlockingQueue<T> messageBuffer
        ) {
        MessageListenerContainer listenerContainer = listenerContainerFactory.createContainer(topic);
        ContainerProperties containerProperties = listenerContainer.getContainerProperties();

        containerProperties.setGroupId(groupId);
        containerProperties.setMessageListener( new MessageListenerImpl<T>( messageBuffer, objectReader ) );
        listenerContainer.start();
        return listenerContainer;
    }

    @Override
    public void close() throws Exception {
        this.listenerContainer.stop();
    }

    private static class MessageListenerImpl<T> implements MessageListener<String, String> {

        private final BlockingQueue<T> receivedMessages;
        private final ObjectReader objectReader;

        public MessageListenerImpl(BlockingQueue<T> receivedMessages, ObjectReader objectReader) {
            this.receivedMessages = receivedMessages;
            this.objectReader = objectReader;
        }

        @Override
        public void onMessage(ConsumerRecord<String, String> record) {
            try {

                String jsonMsg = record.value();
                T msg = objectReader.readValue( jsonMsg );
                this.receivedMessages.add( msg );

            } catch (JsonProcessingException exc ) {
                throw new IllegalStateException( exc );
            }
        }

    }
}
