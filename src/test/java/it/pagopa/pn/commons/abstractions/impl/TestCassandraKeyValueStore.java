package it.pagopa.pn.commons.abstractions.impl;

import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Component;

@Component
public class TestCassandraKeyValueStore
        extends AbstractCassandraKeyValueStore<String, CassandraKeyValueStoreTestIT.CassandraKeyValueStoreTestITTestBean> {

    public TestCassandraKeyValueStore(CassandraOperations cassandraTemplate) {
        super(cassandraTemplate, CassandraKeyValueStoreTestIT.CassandraKeyValueStoreTestITTestBean.class);
    }
}
