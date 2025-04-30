package it.pagopa.pn.test.generated.openapi.msclient.test.api;

import org.springframework.stereotype.Component;

@Component
public class TestApi {
    public TestApi() {
        System.out.println("TESTAPI INIT");
    }
    public void testCall() {
        System.out.println("TESTCALL CALLED");
    }
}
