package it.pagopa.pn.commons.configs;

import it.pagopa.pn.commons.abstractions.impl.AbstractCachedSsmParameterConsumer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

class IsMVPParameterConsumerTest {

    private static final String PA_TAX_ID_MVP = "01199250158";
    private static final String PA_TAX_ID_NO_MVP = "02438750586";

    @Mock
    private AbstractCachedSsmParameterConsumer abstractCachedSsmParameterConsumer;

    private IsMVPParameterConsumer isMVPParameterConsumer;

    @BeforeEach
    void setup() {
        this.isMVPParameterConsumer = new IsMVPParameterConsumer( abstractCachedSsmParameterConsumer );
    }

    @ExtendWith(MockitoExtension.class)
    @Test
    void getPaIsMVPConfig() {

        IsMVPParameterConsumer.PaTaxIdIsMVP[] paTaxIdIsMVPs = new IsMVPParameterConsumer.PaTaxIdIsMVP[2];
        paTaxIdIsMVPs[0] = new IsMVPParameterConsumer.PaTaxIdIsMVP(PA_TAX_ID_MVP, true );
        paTaxIdIsMVPs[1] = new IsMVPParameterConsumer.PaTaxIdIsMVP( PA_TAX_ID_NO_MVP, false );

        Mockito.when( abstractCachedSsmParameterConsumer.getParameterValue( Mockito.anyString(), Mockito.any() ) ).thenReturn(Optional.of(paTaxIdIsMVPs));

        Boolean resultTrue = isMVPParameterConsumer.isMvp(PA_TAX_ID_MVP);
        Assertions.assertTrue( resultTrue );
        Boolean resultFalse = isMVPParameterConsumer.isMvp( PA_TAX_ID_NO_MVP );
        Assertions.assertFalse( resultFalse );
        Boolean resultNotFound = isMVPParameterConsumer.isMvp( "" );
        Assertions.assertFalse( resultNotFound );
    }

}
