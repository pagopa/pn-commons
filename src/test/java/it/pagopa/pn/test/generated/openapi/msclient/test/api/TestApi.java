package it.pagopa.pn.test.generated.openapi.msclient.test.api;

import org.opensaml.saml.saml1.core.Response;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.mock;

@Component
public class TestApi {



    public TestApi() {
        System.out.println("TESTAPI INIT");
    }

    public Mono<String> testCall() {
        System.out.println("TESTCALL CALLED");
        return Mono.defer(() -> Mono.just("test call"));
    }
}
