package it.pagopa.pn.commons.pnclients;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.*;

class CommonBaseClientTest {

    CommonBaseClient commonBaseClient;

    @BeforeEach
    public void init(){
        commonBaseClient = new CommonBaseClient(){};
    }


    @Test
    void enrichBuilder() {


        assertDoesNotThrow(() -> {
            WebClient.Builder builder = WebClient.builder();
            builder = commonBaseClient.enrichBuilder(builder);

            WebClient webClient = builder.build();
        });


    }

    @Test
    void elabExceptionMessage() {
        Exception ex = new RuntimeException("test");
        String res = commonBaseClient.elabExceptionMessage(ex);
        assertNotNull(res);
    }
}