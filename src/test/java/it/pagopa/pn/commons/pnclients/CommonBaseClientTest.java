package it.pagopa.pn.commons.pnclients;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.SocketPolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class CommonBaseClientTest {

    CommonBaseClient commonBaseClient;

    @BeforeEach
    public void init(){
        commonBaseClient = new CommonBaseClient(){};
        commonBaseClient.setTraceIdHeader("trace");
        commonBaseClient.setConnectionTimeoutMillis(3000);
        commonBaseClient.setReadTimeoutMillis(8000);
        commonBaseClient.setRetryMaxAttempts(3);
        commonBaseClient.setWireTapActivation(false);
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

    /* Work with:
   - EncodingMode.TEMPLATE_AND_VALUES
   - EncodingMode.VALUES_ONLY
   (default of WebClient is EncodingMode.TEMPLATE_AND_VALUES)
    */
    @Test
    void pathVariableWithSemicolonTest() throws IOException {
        String contextPath = "/test/";
        String pathVariable = "path;withsemicolon";
        WebClient webClient = commonBaseClient.initWebClient(WebClient.builder());

        MockWebServer mockWebServer = new MockWebServer();

        String expectedResponse = "expect that it works";
        mockWebServer.enqueue(new MockResponse().setResponseCode(200)
                .setBody(expectedResponse));

        mockWebServer.start();

        HttpUrl url = mockWebServer.url(contextPath + pathVariable);
        String basePath = url.scheme() + "://" + url.host() + ":" + url.port();
        Mono<String> webclientCall = webClient.get()
                .uri(basePath + "/test/{key1}", pathVariable)
                .retrieve()
                .bodyToMono(String.class);

        StepVerifier.create(webclientCall)
                .expectNext(expectedResponse)
                .verifyComplete();

        mockWebServer.shutdown();

        //dai log
        //DEBUG org.springframework.web.reactive.function.client.ExchangeFunctions - [37f21974] HTTP GET http://localhost:53257/test/path%3Bwithsemicolon
    }

    /* Work with:
    - EncodingMode.TEMPLATE_AND_VALUES
    - EncodingMode.VALUES_ONLY
    - EncodingMode.URI_COMPONENT
    (default of WebClient is EncodingMode.TEMPLATE_AND_VALUES)
     */
    @Test
    void pathVariableWithSpaceTest() throws IOException {
        String contextPath = "/test/";
        String pathVariable = "path with space";
        WebClient webClient = commonBaseClient.initWebClient(WebClient.builder());

        MockWebServer mockWebServer = new MockWebServer();

        String expectedResponse = "expect that it works";
        mockWebServer.enqueue(new MockResponse().setResponseCode(200)
                .setBody(expectedResponse));

        mockWebServer.start();

        HttpUrl url = mockWebServer.url(contextPath + pathVariable);
        String basePath = url.scheme() + "://" + url.host() + ":" + url.port();

        Mono<String> webclientCall = webClient.get()
                .uri(basePath + "/test/{key1}", pathVariable)
                .retrieve()
                .bodyToMono(String.class);

        StepVerifier.create(webclientCall)
                .expectNext(expectedResponse)
                .verifyComplete();

        mockWebServer.shutdown();

        //dai log:
        //DEBUG org.springframework.web.reactive.function.client.ExchangeFunctions - [4ef27d66] HTTP GET http://localhost:53252/test/path%20with%20space

    }


    @Test
    void testRetryWithSocketNoResponse() throws IOException {
        MockWebServer mockWebServer = new MockWebServer();

        String expectedResponse = "expect that it works";

        mockWebServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE));
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(expectedResponse));

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
    void testRetryWithSocketTimeoutException() throws IOException {
        MockWebServer mockWebServer = new MockWebServer();

        String expectedResponse = "expect that it works";

        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setHeadersDelay(12,TimeUnit.SECONDS));
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(expectedResponse));

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

}