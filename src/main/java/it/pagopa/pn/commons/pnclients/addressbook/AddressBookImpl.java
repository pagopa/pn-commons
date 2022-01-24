package it.pagopa.pn.commons.pnclients.addressbook;

import it.pagopa.pn.api.dto.addressbook.AddressBookEntry;
import it.pagopa.pn.commons.exceptions.PnInternalException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

//@ConditionalOnProperty(name = "pn.legacy", havingValue = "true")
@Deprecated
@Component
public class AddressBookImpl implements AddressBook {

    private final RestTemplate restTemplate;
    private final MicroserviceClientsConfigs configs;

    public AddressBookImpl(MicroserviceClientsConfigs configs) {
        this.configs = configs;
        restTemplate = new RestTemplate();
    }

    public Optional<AddressBookEntry> getAddresses(String taxId) {
        String addressBookGetUrl = configs.getAddressBookBaseUrl() + "/" + taxId;

        ResponseEntity<AddressBookEntry> response;
        response = this.restTemplate.getForEntity(addressBookGetUrl, AddressBookEntry.class);

        if (response.getStatusCode().isError()) {
            throw new PnInternalException("Error calling url " + addressBookGetUrl + " status " + response.getStatusCodeValue());
        }

        AddressBookEntry responseBody = response.getBody();

        // - Less null checking for callers

        if (responseBody != null && responseBody.checkAllNull()) {
            responseBody = null;
        }

        return Optional.ofNullable(responseBody);
    }

}
