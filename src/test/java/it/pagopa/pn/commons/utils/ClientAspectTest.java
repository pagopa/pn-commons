package it.pagopa.pn.commons.utils;


import it.pagopa.pn.commons.log.PnLogger;
import it.pagopa.pn.test.generated.openapi.msclient.test.api.TestApi;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
class ClientAspectTest {

    @Test
    void testSendMetricToCloudWatch() {

        System.out.println("AAAAAAAAAA");
        TestApi t = new TestApi();
        t.testCall();
        Assertions.assertTrue(true);
    }


}
