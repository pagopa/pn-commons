package it.pagopa.pn.commons.pnclients;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

class RestTemplateFactoryTest {

    RestTemplateFactory restTemplateFactory;

    @BeforeEach
    public void init(){
        restTemplateFactory = new RestTemplateFactory();
    }


    @Test
    void restTemplateWithTracing() {
        RestTemplate res = restTemplateFactory.restTemplateWithTracing();
        assertNotNull(res);
    }

    @Test
    void enrichWithTracing() {
        RestTemplate res = restTemplateFactory.restTemplateWithTracing();

        restTemplateFactory
                .enrichWithTracing(res);

        assertTrue(res.getInterceptors().get(0) instanceof RestTemplateFactory.RestTemplateHeaderModifierInterceptor);
    }
}