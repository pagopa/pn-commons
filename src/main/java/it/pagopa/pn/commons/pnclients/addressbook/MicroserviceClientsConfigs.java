package it.pagopa.pn.commons.pnclients.addressbook;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("pn.clients")
public class MicroserviceClientsConfigs {

    private String addressBookBaseUrl;

}
