package it.pagopa.pn.national.registries.generated.openapi.msclient.downstream.inad.v1.api;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class TestApi {

    public TestApi() {
        System.out.println("TESTAPI INIT");
    }

    public Mono<String> testCall() {
        System.out.println("TESTCALL CALLED");
        return Mono.just( "test call");
    }
}
