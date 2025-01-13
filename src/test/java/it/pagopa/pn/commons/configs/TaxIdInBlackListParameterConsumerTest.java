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

class TaxIdInBlackListParameterConsumerTest {

    private static final String TAX_ID_IN_WHITE_LIST = "EEEEEEEEEEE";

    @Mock
    private ParameterConsumer parameterConsumer;

    private TaxIdInBlackListParameterConsumer taxIdInBlackListParameterConsumer;


    @BeforeEach
    void setup() {
        taxIdInBlackListParameterConsumer = new TaxIdInBlackListParameterConsumer( parameterConsumer );
    }

    @ExtendWith(MockitoExtension.class)
    @Test
    void getTaxIdIsInBlackList() {

        TaxIdInBlackListParameterConsumer.TaxIdInBlackList[] taxIdInBlackLists = new TaxIdInBlackListParameterConsumer.TaxIdInBlackList[2];
        taxIdInBlackLists[0] = new TaxIdInBlackListParameterConsumer.TaxIdInBlackList( TAX_ID_IN_WHITE_LIST );

        Mockito.when( parameterConsumer.getParameterValue( Mockito.anyString(), Mockito.any() ) )
                .thenReturn(Optional.of(taxIdInBlackLists));

        Boolean inBlackList = taxIdInBlackListParameterConsumer.isInBlackList(TAX_ID_IN_WHITE_LIST);
        Assertions.assertTrue( inBlackList );
    }

    @ExtendWith(MockitoExtension.class)
    @Test
    void getTaxIdNotInBlackList() {

        Mockito.when( parameterConsumer.getParameterValue( Mockito.anyString(), Mockito.any() ) )
                .thenReturn(Optional.empty());

        Boolean inBlackList = taxIdInBlackListParameterConsumer.isInBlackList(TAX_ID_IN_WHITE_LIST);
        Assertions.assertFalse( inBlackList );
    }
}
