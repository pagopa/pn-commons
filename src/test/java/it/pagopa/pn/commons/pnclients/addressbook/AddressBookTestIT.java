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
    void testKnownTaxId() {
        // GIVEN
        String taxId = "CGNNMO80A02H501R";

        // WHEN
        AddressBookEntry ab = client.getAddresses( taxId );

        // THEN
        Assertions.assertEquals( taxId, ab.getTaxId() );
        Assertions.assertNotNull( ab.getDigitalAddresses() );
        Assertions.assertNotNull( ab.getDigitalAddresses().getPlatform() );
        Assertions.assertNotNull( ab.getDigitalAddresses().getGeneral() );

        Assertions.assertTrue( StringUtils.isNotBlank( ab.getDigitalAddresses().getPlatform().getAddress() ));
        Assertions.assertTrue( StringUtils.isNotBlank( ab.getDigitalAddresses().getGeneral().getAddress() ));
    }

    @Test
    void testUnknownTaxId() {
        // GIVEN
        String taxId = "IsNotATaxId";

        // WHEN
        AddressBookEntry ab = client.getAddresses( taxId );

        // THEN
        Assertions.assertNotNull( ab.getDigitalAddresses() );
        Assertions.assertNull( ab.getDigitalAddresses().getPlatform() );
        Assertions.assertNull( ab.getDigitalAddresses().getGeneral() );
    }

}
