package it.pagopa.pn.commons.configs;

import it.pagopa.pn.commons.abstractions.impl.AbstractCachedSsmParameterConsumer;
import it.pagopa.pn.commons.utils.ValidateUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;


@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = {"pn.env.runtime=DEVELOPMENT"})
@SpringBootTest
class TaxIdInWhiteListParameterConsumerTest {

    private static final String TAX_ID_IN_WHITE_LIST = "EEEEEEEEEEE";
    private static final String INVALID_TAX_ID_IN_WHITE_LIST = "AAAAAAAAA";

    @MockBean
    ValidateUtils validateUtils;

    @MockBean
    private AbstractCachedSsmParameterConsumer abstractCachedSsmParameterConsumer;

    @Autowired
    private TaxIdInWhiteListParameterConsumer taxIdInWhiteListParameterConsumer;

    @ExtendWith(MockitoExtension.class)
    @Test
    void getTaxIdIsInWhiteList() {

        TaxIdInWhiteListParameterConsumer.TaxIdInWhiteList[] taxIdInWhiteLists = new TaxIdInWhiteListParameterConsumer.TaxIdInWhiteList[2];
        taxIdInWhiteLists[0] = new TaxIdInWhiteListParameterConsumer.TaxIdInWhiteList( TAX_ID_IN_WHITE_LIST, true );
        taxIdInWhiteLists[1] = new TaxIdInWhiteListParameterConsumer.TaxIdInWhiteList( INVALID_TAX_ID_IN_WHITE_LIST, false );

        Mockito.when( abstractCachedSsmParameterConsumer.getParameterValue( Mockito.anyString(), Mockito.any() ) )
                .thenReturn(Optional.of(taxIdInWhiteLists));

        Boolean inWhiteList = taxIdInWhiteListParameterConsumer.isInWhiteList(TAX_ID_IN_WHITE_LIST);
        Assertions.assertTrue( inWhiteList );
        Boolean invalidTaxIdInWhiteList = taxIdInWhiteListParameterConsumer.isInWhiteList(INVALID_TAX_ID_IN_WHITE_LIST);
        Assertions.assertFalse( invalidTaxIdInWhiteList );
    }

    @ExtendWith(MockitoExtension.class)
    @Test
    void getTaxIdNotInWhiteList() {

        Mockito.when( abstractCachedSsmParameterConsumer.getParameterValue( Mockito.anyString(), Mockito.any() ) )
                .thenReturn(Optional.empty());

        Boolean inWhiteList = taxIdInWhiteListParameterConsumer.isInWhiteList(TAX_ID_IN_WHITE_LIST);
        Assertions.assertFalse( inWhiteList );
    }
}
