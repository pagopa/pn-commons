package it.pagopa.pn.commons.abstractions.impl;

import it.pagopa.pn.commons.abstractions.IdConflictException;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Random;

@SpringBootTest()
@ActiveProfiles("test")
class CassandraKeyValueStoreTestIT {

	public static final String BEAN_ID = CassandraKeyValueStoreTestIT.class.getSimpleName() + "_ID1";

	@Autowired
	private TestCassandraKeyValueStore kvStore;

	@Test
	void successPutAndGetToCassandra() {

		kvStore.delete( BEAN_ID );

		// - Given
		CassandraKeyValueStoreTestITTestBean bean = CassandraKeyValueStoreTestITTestBean.builder()
				.name("Donald")
				.surname("Duck")
				.age( randomAge() )
				.id( BEAN_ID )
				.bornDate( Instant.now().truncatedTo(ChronoUnit.MILLIS))
				.build();

		// - When
		kvStore.put( bean );
		Optional<CassandraKeyValueStoreTestITTestBean> receivedBean = kvStore.get( bean.getId() );

		// - Then
		Assertions.assertTrue( receivedBean.isPresent(), "Saved bean not found");
		Assertions.assertEquals( bean, receivedBean.get(), "Saved and loaded messages differs");

		Assertions.assertThrows( IdConflictException.class, () -> {
			CassandraKeyValueStoreTestITTestBean duplicatedBean = CassandraKeyValueStoreTestITTestBean.builder()
					.name("Donald Modificato")
					.surname("Duck")
					.age( randomAge() )
					.id( BEAN_ID )
					.bornDate( Instant.now().truncatedTo(ChronoUnit.MILLIS))
					.build();
			kvStore.putIfAbsent( duplicatedBean );
		});
	}

	private Integer randomAge() {
		return new Random().nextInt( 100);
	}


	@Data
	@Table
	@Builder( toBuilder = true)
	@EqualsAndHashCode()
	public static class CassandraKeyValueStoreTestITTestBean {

		@Id
		private String id;

		private String name;
		private String surname;
		private Integer age;

		private Instant bornDate;

	}

}
