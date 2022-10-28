package it.pagopa.pn.commons.configs;

import it.pagopa.pn.commons.abstractions.impl.AbstractCachedSsmParameterConsumer;
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
@TestPropertySource(properties = { "pn.commons.features.is-mvp-default-value=true",
        "pn.env.runtime=DEVELOPMENT"})
@SpringBootTest
class MVPParameterConsumerTest {

    private static final String PA_TAX_ID_MVP = "01199250158";
    private static final String PA_TAX_ID_NO_MVP = "02438750586";
    private static final Boolean DEFAULT_VALUE_PA_IS_MVP = true;

    @MockBean
    private AbstractCachedSsmParameterConsumer abstractCachedSsmParameterConsumer;

    @Autowired
    private MVPParameterConsumerTestActivator isMVPParameterConsumer;

    @ExtendWith(MockitoExtension.class)
    @Test
    void getPaIsMVPConfig() {

        MVPParameterConsumer.PaTaxIdIsMVP[] paTaxIdIsMVPs = new MVPParameterConsumer.PaTaxIdIsMVP[2];
        paTaxIdIsMVPs[0] = new MVPParameterConsumer.PaTaxIdIsMVP(PA_TAX_ID_MVP, true );
        paTaxIdIsMVPs[1] = new MVPParameterConsumer.PaTaxIdIsMVP( PA_TAX_ID_NO_MVP, false );

        Mockito.when( abstractCachedSsmParameterConsumer.getParameterValue( Mockito.anyString(), Mockito.any() ) ).thenReturn(Optional.of(paTaxIdIsMVPs));

        Boolean resultTrue = isMVPParameterConsumer.isMvp(PA_TAX_ID_MVP);
        Assertions.assertTrue( resultTrue );
        Boolean resultFalse = isMVPParameterConsumer.isMvp( PA_TAX_ID_NO_MVP );
        Assertions.assertFalse( resultFalse );
        Boolean resultDefault = isMVPParameterConsumer.isMvp( "" );
        Assertions.assertEquals( DEFAULT_VALUE_PA_IS_MVP, resultDefault );
    }

}
