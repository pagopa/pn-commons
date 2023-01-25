package it.pagopa.pn.commons.pnclients;

import it.pagopa.pn.commons.exceptions.PnHttpResponseException;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RestTemplateFactoryTest {

    RestTemplateFactory restTemplateFactory;

    @BeforeEach
    public void init(){
        restTemplateFactory = new RestTemplateFactory();
    }


    @Test
    void restTemplateWithTracing() {
        RestTemplate res = restTemplateFactory.restTemplateWithTracing(3, 10000);
        assertNotNull(res);
    }

    @Test
    void enrichWithTracing() {
        RestTemplate res = restTemplateFactory.restTemplateWithTracing(3, 10000);

        restTemplateFactory
                .enrichWithTracing(res);

        assertTrue(res.getInterceptors().get(0) instanceof RestTemplateFactory.RestTemplateHeaderModifierInterceptor);
    }

    @Test
    void testRetryWithTreeFails() throws IOException {
        RestTemplate restTemplate = restTemplateFactory.restTemplateWithTracing(3, 10000);

        MockWebServer mockWebServer = new MockWebServer();

        String expectedResponse = "expect that it works";
        mockWebServer.enqueue(new MockResponse().setResponseCode(429));
        mockWebServer.enqueue(new MockResponse().setResponseCode(502));
        mockWebServer.enqueue(new MockResponse().setResponseCode(429));
        mockWebServer.enqueue(new MockResponse().setResponseCode(200)
                .setBody(expectedResponse));

        mockWebServer.start();

        HttpUrl url = mockWebServer.url("/test");
        ResponseEntity<String> response = restTemplate.postForEntity(url.uri(), "myRequest", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedResponse);


        mockWebServer.shutdown();
    }

    @Test
    void testRetryWithTwoFails() throws IOException {
        RestTemplate restTemplate = restTemplateFactory.restTemplateWithTracing(3, 10000);

        MockWebServer mockWebServer = new MockWebServer();

        String expectedResponse = "expect that it works";
        mockWebServer.enqueue(new MockResponse().setResponseCode(429));
        mockWebServer.enqueue(new MockResponse().setResponseCode(502));
        mockWebServer.enqueue(new MockResponse().setResponseCode(200)
                .setBody(expectedResponse));

        mockWebServer.start();

        HttpUrl url = mockWebServer.url("/test");
        ResponseEntity<String> response = restTemplate.postForEntity(url.uri(), "myRequest", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedResponse);


        mockWebServer.shutdown();
    }

    @Test
    void testRetryWithOneFail() throws IOException {
        RestTemplate restTemplate = restTemplateFactory.restTemplateWithTracing(3, 10000);

        MockWebServer mockWebServer = new MockWebServer();

        String expectedResponse = "expect that it works";
        mockWebServer.enqueue(new MockResponse().setResponseCode(502));
        mockWebServer.enqueue(new MockResponse().setResponseCode(200)
                .setBody(expectedResponse));

        mockWebServer.start();

        HttpUrl url = mockWebServer.url("/test");
        ResponseEntity<String> response = restTemplate.postForEntity(url.uri(), "myRequest", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedResponse);


        mockWebServer.shutdown();
    }

    @Test
    void testRetryFourTimesButParameterIsSetToThree() throws IOException {
        RestTemplate restTemplate = restTemplateFactory.restTemplateWithTracing(3, 10000);

        MockWebServer mockWebServer = new MockWebServer();

        String expectedResponse = "expect that it works";
        mockWebServer.enqueue(new MockResponse().setResponseCode(429));
        mockWebServer.enqueue(new MockResponse().setResponseCode(502));
        mockWebServer.enqueue(new MockResponse().setResponseCode(429));
        mockWebServer.enqueue(new MockResponse().setResponseCode(429));
        mockWebServer.enqueue(new MockResponse().setResponseCode(200)
                .setBody(expectedResponse));

        mockWebServer.start();

        HttpUrl url = mockWebServer.url("/test");
        URI uri = url.uri();
        Assertions.assertThrows(PnHttpResponseException.class,
                () -> restTemplate.postForEntity(uri, "myRequest", String.class));

        mockWebServer.shutdown();
    }

    @Test
    void testExceptionNotRetryable() throws IOException {
        RestTemplate restTemplate = restTemplateFactory.restTemplateWithTracing(3, 10000);

        MockWebServer mockWebServer = new MockWebServer();

        String expectedResponse = "expect that it works";
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));
        mockWebServer.enqueue(new MockResponse().setResponseCode(200)
                .setBody(expectedResponse));

        mockWebServer.start();

        HttpUrl url = mockWebServer.url("/test");
        URI uri = url.uri();
        Assertions.assertThrows(PnHttpResponseException.class,
                () -> restTemplate.postForEntity(uri, "myRequest", String.class));

        mockWebServer.shutdown();
    }

}