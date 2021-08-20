package it.pagopa.pn.commons.abstractions.impl;

import it.pagopa.pn.api.dto.events.PnDeliveryNewNotificationEvent;
import it.pagopa.pn.api.dto.events.StandardEventHeader;
import it.pagopa.pn.commons.abstractions.MomConsumer;
import it.pagopa.pn.commons.abstractions.MomProducer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KafkaProducerConsumerTestIT {

	static final String TOPIC_NAME = "KafkaConsumerTest_testTopic";
	static final String GROUP_NAME = "KafkaConsumerTest_grp1";
	public static final String IUN = "IUN_001";
	public static final String PA_ID = "paId";

	@Autowired
	private MomProducer<PnDeliveryNewNotificationEvent> producer;

	@Autowired
	private MomConsumer<PnDeliveryNewNotificationEvent> consumer;


	@BeforeAll
	void init() throws InterruptedException {
		Thread.sleep( 1000 );

		// - Necessario per inizializzazione dei topic in caso di test locali
		consumer.poll( Duration.ofSeconds(1) );
	}

	@Test
	void successPushAndPullToKafka() throws InterruptedException {

		// - Given
		PnDeliveryNewNotificationEvent bean = PnDeliveryNewNotificationEvent.builder()
				.header( StandardEventHeader.builder()
						.iun( IUN )
						.build()
				)
				.payload(PnDeliveryNewNotificationEvent.Payload.builder()
						.paId( PA_ID )
						.build()
				)
				.build();

		// - When
		producer.push( bean );
		List<PnDeliveryNewNotificationEvent> receivedBeans = consumer.poll( Duration.ofSeconds(10) );

		// - Then
		Assertions.assertTrue( receivedBeans.size() > 0, "Ricevuto almeno un messaggio");
		PnDeliveryNewNotificationEvent lastReceived = receivedBeans.get( receivedBeans.size() - 1 );
		Assertions.assertEquals( bean.getHeader().getEventId(), lastReceived.getHeader().getEventId(), "Sended and received messages differs");
	}

}
