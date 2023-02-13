package it.pagopa.pn.commons.configs;

import it.pagopa.pn.commons.abstractions.ParameterConsumer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

class TaxIdInWhiteListParameterConsumerTest {

    private static final String TAX_ID_IN_WHITE_LIST = "EEEEEEEEEEE";

    @Mock
    private ParameterConsumer parameterConsumer;

    private TaxIdInWhiteListParameterConsumer taxIdInWhiteListParameterConsumer;


    @BeforeEach
    void setup() {
        taxIdInWhiteListParameterConsumer = new TaxIdInWhiteListParameterConsumer( parameterConsumer );
    }

    @ExtendWith(MockitoExtension.class)
    @Test
    void getTaxIdIsInWhiteList() {

        TaxIdInWhiteListParameterConsumer.TaxIdInWhiteList[] taxIdInWhiteLists = new TaxIdInWhiteListParameterConsumer.TaxIdInWhiteList[2];
        taxIdInWhiteLists[0] = new TaxIdInWhiteListParameterConsumer.TaxIdInWhiteList( TAX_ID_IN_WHITE_LIST );

        Mockito.when( parameterConsumer.getParameterValue( Mockito.anyString(), Mockito.any() ) )
                .thenReturn(Optional.of(taxIdInWhiteLists));

        Boolean inWhiteList = taxIdInWhiteListParameterConsumer.isInWhiteList(TAX_ID_IN_WHITE_LIST);
        Assertions.assertTrue( inWhiteList );
    }

    @ExtendWith(MockitoExtension.class)
    @Test
    void getTaxIdNotInWhiteList() {

        Mockito.when( parameterConsumer.getParameterValue( Mockito.anyString(), Mockito.any() ) )
                .thenReturn(Optional.empty());

        Boolean inWhiteList = taxIdInWhiteListParameterConsumer.isInWhiteList(TAX_ID_IN_WHITE_LIST);
        Assertions.assertFalse( inWhiteList );
    }
}
