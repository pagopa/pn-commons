package it.pagopa.pn.commons.pnclients.addressbook;

import it.pagopa.pn.api.dto.addressbook.AddressBookEntry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:/application-test.properties")
@EnableConfigurationProperties(value = MicroserviceClientsConfigs.class)
public class AddressBookTestIT {

    private AddressBook client;

    @Autowired
    private MicroserviceClientsConfigs cfg;

    @BeforeEach
    void prepareClient() {
        client = new AddressBookImpl( cfg );
    }
    
    @Test
    void successWithPlatformAndGenralDigitalAddresses() {
        // GIVEN
        String taxId = "CGNNMO80A02H501R";

        // WHEN
        Optional<AddressBookEntry> response = client.getAddresses( taxId );

        // THEN
        Assertions.assertTrue( response.isPresent() );
        AddressBookEntry ab = response.get();

        Assertions.assertEquals( taxId, ab.getTaxId() );
        Assertions.assertNotNull( ab.getDigitalAddresses() );
        Assertions.assertNotNull( ab.getDigitalAddresses().getPlatform() );
        Assertions.assertNotNull( ab.getDigitalAddresses().getGeneral() );

        Assertions.assertTrue( StringUtils.isNotBlank( ab.getDigitalAddresses().getPlatform().getAddress() ));
        Assertions.assertTrue( StringUtils.isNotBlank( ab.getDigitalAddresses().getGeneral().getAddress() ));
    }

    @Test
    void entryWithoutAddresses() {
        // GIVEN
        String taxId = "IsNotATaxId";

        // WHEN
        Optional<AddressBookEntry> response = client.getAddresses( taxId );

        // THEN
        Assertions.assertTrue( response.isEmpty() );
    }

}
