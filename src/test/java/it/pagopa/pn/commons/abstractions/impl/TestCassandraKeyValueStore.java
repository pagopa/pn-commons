package it.pagopa.pn.commons.abstractions.impl;

import it.pagopa.pn.commons.abstractions.impl.AbstractCassandraKeyValueStore;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Component;

@Component
public class TestCassandraKeyValueStore
        extends AbstractCassandraKeyValueStore<String, CassandraKeyValueStoreTestIT.CassandraKeyValueStoreTestITTestBean> {

    public TestCassandraKeyValueStore(CassandraOperations cassandraTemplate) {
        super(cassandraTemplate, CassandraKeyValueStoreTestIT.CassandraKeyValueStoreTestITTestBean.class);
    }
}
