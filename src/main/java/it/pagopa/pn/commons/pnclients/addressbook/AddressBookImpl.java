package it.pagopa.pn.commons.pnclients.addressbook;

import it.pagopa.pn.api.dto.addressbook.AddressBookEntry;
import it.pagopa.pn.api.dto.addressbook.DigitalAddresses;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AddressBookImpl implements AddressBook {

    private final RestTemplate restTemplate;
    private final MicroserviceClientsConfigs configs;

    public AddressBookImpl( MicroserviceClientsConfigs configs) {
        this.configs = configs;
        restTemplate = new RestTemplate();
    }

    public AddressBookEntry getAddresses(String taxId ) {
        String addressBookGetUrl = configs.getAddressBookBaseUrl() + "/" + taxId;

        ResponseEntity<AddressBookEntry> response;
        response = this.restTemplate.getForEntity( addressBookGetUrl, AddressBookEntry.class );

        if( response.getStatusCode().isError() ) {
            // FIXME exception handling
            throw new IllegalStateException("Error calling url " + addressBookGetUrl + " status "+ response.getStatusCodeValue() );
        }

        AddressBookEntry responseBody = response.getBody();
        if( responseBody == null ) {
            responseBody = new AddressBookEntry();
        }

        if( responseBody.getDigitalAddresses() == null ) {
            responseBody = responseBody.toBuilder()
                    .digitalAddresses( new DigitalAddresses() )
                    .build();
        }

        return responseBody;
    }
}
