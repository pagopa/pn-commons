package it.pagopa.pn.commons.pnclients;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class CommonBaseClientTest {

    CommonBaseClient commonBaseClient;

    @BeforeEach
    public void init(){
        commonBaseClient = new CommonBaseClient(){};
        commonBaseClient.setTraceIdHeader("trace");
        commonBaseClient.setConnectionTimeoutMillis(10000);
        commonBaseClient.setRetryMaxAttempts(3);
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

    @Test
    void testRetryWithTreeFails() throws IOException
    {
        MockWebServer mockWebServer = new MockWebServer();

        String expectedResponse = "expect that it works";
        mockWebServer.enqueue(new MockResponse().setResponseCode(429));
        mockWebServer.enqueue(new MockResponse().setResponseCode(502));
        mockWebServer.enqueue(new MockResponse().setResponseCode(429));
        mockWebServer.enqueue(new MockResponse().setResponseCode(200)
                .setBody(expectedResponse));

        mockWebServer.start();

        HttpUrl url = mockWebServer.url("/test");

        WebClient webClient = commonBaseClient.initWebClient(WebClient.builder());
        Mono<String> responseMono = webClient.post()
                .uri(url.uri())
                .body(BodyInserters.fromObject("myRequest"))
                .retrieve()
                .bodyToMono(String.class);

        StepVerifier.create(responseMono)
                .expectNext(expectedResponse)
                .expectComplete().verify();

        mockWebServer.shutdown();
    }

    @Test
    void testRetryWithTwoFails() throws IOException
    {
        MockWebServer mockWebServer = new MockWebServer();

        String expectedResponse = "expect that it works";
        mockWebServer.enqueue(new MockResponse().setResponseCode(429));
        mockWebServer.enqueue(new MockResponse().setResponseCode(502));
        mockWebServer.enqueue(new MockResponse().setResponseCode(200)
                .setBody(expectedResponse));

        mockWebServer.start();

        HttpUrl url = mockWebServer.url("/test");

        WebClient webClient = commonBaseClient.initWebClient(WebClient.builder());
        Mono<String> responseMono = webClient.post()
                .uri(url.uri())
                .body(BodyInserters.fromObject("myRequest"))
                .retrieve()
                .bodyToMono(String.class);

        StepVerifier.create(responseMono)
                .expectNext(expectedResponse)
                .expectComplete().verify();

        mockWebServer.shutdown();
    }

    @Test
    void testRetryWithOneFail() throws IOException
    {
        MockWebServer mockWebServer = new MockWebServer();

        String expectedResponse = "expect that it works";
        mockWebServer.enqueue(new MockResponse().setResponseCode(429));
        mockWebServer.enqueue(new MockResponse().setResponseCode(200)
                .setBody(expectedResponse));

        mockWebServer.start();

        HttpUrl url = mockWebServer.url("/test");

        WebClient webClient = commonBaseClient.initWebClient(WebClient.builder());
        Mono<String> responseMono = webClient.post()
                .uri(url.uri())
                .body(BodyInserters.fromObject("myRequest"))
                .retrieve()
                .bodyToMono(String.class);

        StepVerifier.create(responseMono)
                .expectNext(expectedResponse)
                .expectComplete().verify();

        mockWebServer.shutdown();
    }

    @Test
    void testRetryFourTimesButParameterIsSetToThree() throws IOException
    {
        MockWebServer mockWebServer = new MockWebServer();

        String expectedResponse = "expect that it works";
        mockWebServer.enqueue(new MockResponse().setResponseCode(429));
        mockWebServer.enqueue(new MockResponse().setResponseCode(502));
        mockWebServer.enqueue(new MockResponse().setResponseCode(429));
        mockWebServer.enqueue(new MockResponse().setResponseCode(503));
        mockWebServer.enqueue(new MockResponse().setResponseCode(200)
                .setBody(expectedResponse));

        mockWebServer.start();

        HttpUrl url = mockWebServer.url("/test");

        WebClient webClient = commonBaseClient.initWebClient(WebClient.builder());
        Mono<String> responseMono = webClient.post()
                .uri(url.uri())
                .body(BodyInserters.fromObject("myRequest"))
                .retrieve()
                .bodyToMono(String.class);

        StepVerifier.create(responseMono)
                .expectError(WebClientResponseException.ServiceUnavailable.class)
                .verify();

        mockWebServer.shutdown();
    }

    @Test
    void testExceptionNotRetryable() throws IOException
    {
        MockWebServer mockWebServer = new MockWebServer();

        String expectedResponse = "expect that it works";
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));
        mockWebServer.enqueue(new MockResponse().setResponseCode(200)
                .setBody(expectedResponse));

        mockWebServer.start();

        HttpUrl url = mockWebServer.url("/test");

        WebClient webClient = commonBaseClient.initWebClient(WebClient.builder());
        Mono<String> responseMono = webClient.post()
                .uri(url.uri())
                .body(BodyInserters.fromObject("myRequest"))
                .retrieve()
                .bodyToMono(String.class);

        StepVerifier.create(responseMono)
                .expectError(WebClientResponseException.InternalServerError.class)
                .verify();

        mockWebServer.shutdown();
    }

}