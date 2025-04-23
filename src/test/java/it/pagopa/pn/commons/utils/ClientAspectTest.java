package it.pagopa.pn.commons.utils;


import it.pagopa.pn.commons.log.PnLogger;
import it.pagopa.pn.test.generated.openapi.msclient.test.api.TestApi;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class ClientAspectTest {

    @Test
    void testSendMetricToCloudWatch() {
        PnLogger fooLogger = PnLogger.getLogger(TestApi.class.getName());
        fooLogger.info("TEST LOG");
        System.out.println("AAAAAAAAAA");
        TestApi t = new TestApi();
        t.testCall();
        Assertions.assertTrue(true);
    }


}
