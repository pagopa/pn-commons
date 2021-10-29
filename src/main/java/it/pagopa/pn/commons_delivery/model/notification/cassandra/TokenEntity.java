package it.pagopa.pn.commons_delivery.model.notification.cassandra;


import lombok.Builder;
import lombok.Getter;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("direct_access_tokens")
@Getter
@Builder
public class TokenEntity {

    @PrimaryKey
    private String token;

    private String iun;

    private String taxId;

}
