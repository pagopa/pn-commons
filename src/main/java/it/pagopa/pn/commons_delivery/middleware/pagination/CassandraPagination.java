package it.pagopa.pn.commons_delivery.middleware.pagination;

import it.pagopa.pn.commons.abstractions.impl.AbstractCassandraKeyValueStore;
import it.pagopa.pn.commons_delivery.model.notification.cassandra.PaperNotificationFailedEntity;
import it.pagopa.pn.commons_delivery.model.notification.cassandra.PaperNotificationFailedEntityId;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.query.CassandraPageRequest;
import org.springframework.data.cassandra.core.query.Criteria;
import org.springframework.data.cassandra.core.query.Query;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.util.List;

//TODO Da eliminare. creato l'unico scopo di avere un esempio di paginazione in Cassandra. Viene utilizzato solo a livello di test
@Component
public class CassandraPagination extends AbstractCassandraKeyValueStore<PaperNotificationFailedEntityId, PaperNotificationFailedEntity> {
    public static final String RECIPIENT_ID_COL = "recipientid";
    private final CassandraOperations cassandraTemplate;

    public CassandraPagination(CassandraOperations cassandraTemplate) {
        super(cassandraTemplate, PaperNotificationFailedEntity.class);
        this.cassandraTemplate = cassandraTemplate;
    }

    public List<PaperNotificationFailedEntity> paginationCassandra(String recipientId, int size, int searchedPage) {

        ByteBuffer previousPagingState = null; //Non viene utilizato, ma contiene il paging precedente.
        ByteBuffer nextPagingState = null;

        PageRequest pageRequest = PageRequest.of(0, size);
        Slice<PaperNotificationFailedEntity> slice;

        int page = 0;

        do {

            Pageable pageable = CassandraPageRequest.of(pageRequest, nextPagingState);

            Query query = queryByRecipientId(recipientId);
            Query queryWithPagination = query.pageRequest(pageable);

            slice = cassandraTemplate.slice(queryWithPagination, PaperNotificationFailedEntity.class);

            if (page == searchedPage) {
                return slice.getContent();
            } else {
                previousPagingState = nextPagingState;
                nextPagingState = ((CassandraPageRequest) slice.getPageable()).getPagingState();
            }

            page++;
        } while (!slice.isLast());

        return null;
    }

    private Query queryByRecipientId(String recipientId) {
        return Query.query(Criteria.where(RECIPIENT_ID_COL).is(recipientId));
    }

}
