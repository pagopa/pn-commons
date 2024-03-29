package it.pagopa.pn.commons.pnclients;

import it.pagopa.pn.commons.exceptions.PnHttpResponseException;
import java.net.SocketTimeoutException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.SocketPolicy;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.ResourceAccessException;
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
        RestTemplate res = restTemplateFactory.restTemplateWithTracing(3, 3000,8000);
        assertNotNull(res);
    }

    @Test
    void enrichWithTracing() {
        RestTemplate res = restTemplateFactory.restTemplateWithTracing(3, 3000,8000);

        restTemplateFactory
                .enrichWithTracing(res);

        assertTrue(res.getInterceptors().get(0) instanceof RestTemplateFactory.RestTemplateHeaderModifierInterceptor);
    }

    @Test
    void testRetryWithTreeFails() throws IOException {
        RestTemplate restTemplate = restTemplateFactory.restTemplateWithTracing(3, 3000,8000);

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
        RestTemplate restTemplate = restTemplateFactory.restTemplateWithTracing(3, 3000,8000);

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
        RestTemplate restTemplate = restTemplateFactory.restTemplateWithTracing(3, 3000,8000);

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
        RestTemplate restTemplate = restTemplateFactory.restTemplateWithTracing(3, 3000,8000);

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
        RestTemplate restTemplate = restTemplateFactory.restTemplateWithTracing(3, 3000,8000);

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

    /* Work with:
    - EncodingMode.TEMPLATE_AND_VALUES
    - EncodingMode.VALUES_ONLY
    (default of RestTemplate is EncodingMode.URI_COMPONENT)
     */
    @Test
    @Disabled("in quanto si utilizza l'encoding di default URI_COMPONENT")
    void pathVariableWithSemicolonTest() throws IOException {
        String contextPath = "/test/";
        String pathVariable = "path;withsemicolon";
        RestTemplate restTemplate = restTemplateFactory.restTemplateWithTracing(3, 3000,8000);

        MockWebServer mockWebServer = new MockWebServer();

        String expectedResponse = "expect that it works";
        mockWebServer.enqueue(new MockResponse().setResponseCode(200)
                .setBody(expectedResponse));

        mockWebServer.start();

        HttpUrl url = mockWebServer.url(contextPath + pathVariable);
        String basePath = url.scheme() + "://" + url.host() + ":" + url.port();
        ResponseEntity<String> response = restTemplate.getForEntity(basePath + "/test/{key1}", String.class, pathVariable);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedResponse);

        URI expandURI = restTemplate.getUriTemplateHandler().expand(basePath + "/test/{key1}", pathVariable);
        assertThat(expandURI.getRawPath()).isEqualTo("/test/path%3Bwithsemicolon");

        mockWebServer.shutdown();
    }

    /* Work with:
    - EncodingMode.TEMPLATE_AND_VALUES
    - EncodingMode.VALUES_ONLY
    - EncodingMode.URI_COMPONENT
    (default of RestTemplate is EncodingMode.URI_COMPONENT)
     */
    @Test
    void pathVariableWithSpaceTest() throws IOException {
        String contextPath = "/test/";
        String pathVariable = "path with space";
        RestTemplate restTemplate = restTemplateFactory.restTemplateWithTracing(3, 3000,8000);

        MockWebServer mockWebServer = new MockWebServer();

        String expectedResponse = "expect that it works";
        mockWebServer.enqueue(new MockResponse().setResponseCode(200)
                .setBody(expectedResponse));

        mockWebServer.start();

        HttpUrl url = mockWebServer.url(contextPath + pathVariable);
        String basePath = url.scheme() + "://" + url.host() + ":" + url.port();
        ResponseEntity<String> response = restTemplate.getForEntity(basePath + "/test/{key1}", String.class, pathVariable);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedResponse);

        URI expandURI = restTemplate.getUriTemplateHandler().expand(basePath + "/test/{key1}", pathVariable);
        assertThat(expandURI.getRawPath()).isEqualTo("/test/path%20with%20space");

        mockWebServer.shutdown();
    }
    @Test
    void testRetryWithConnectionException() throws IOException {
        RestTemplate restTemplate = restTemplateFactory.restTemplateWithTracing(3, 10000,10000);
        MockWebServer mockWebServer = new MockWebServer();
        String expectedResponse = "expect that it works";
        mockWebServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START));
        mockWebServer.enqueue(new MockResponse().setResponseCode(200));
        mockWebServer.start();
        HttpUrl url = mockWebServer.url("/test");
        ResponseEntity<String> response = restTemplate.postForEntity(url.uri(), "myRequest", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockWebServer.shutdown();
    }

    @Test
    void testSocketTimeoutException() throws IOException {
        RestTemplate restTemplate = restTemplateFactory.restTemplateWithTracing(3, 1000, 1000);

        MockWebServer mockWebServer = new MockWebServer();
        String expectedResponse = "expect that it works";
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(expectedResponse).setBodyDelay(1, TimeUnit.HOURS));
        mockWebServer.start();
        HttpUrl url = mockWebServer.url("/test?param=1");
        URI uri = url.uri();

        Duration deltaTime = Duration.ZERO;
        Instant beginTime = Instant.now();
        Exception err = null;
        try{
            restTemplate.postForEntity(uri.toString(), "Body", String.class, Map.of("param", 1));
        }catch(Exception e){
            err = e;
            deltaTime =  Duration.between(beginTime, Instant.now());
        }
        Assert.assertTrue(err instanceof ResourceAccessException);
        Assert.assertTrue(err.getCause() instanceof SocketTimeoutException);
        //Verify 3 attempts = maxRetries * ReadTimeout
        Assert.assertTrue(deltaTime.getSeconds() >= 3);
        try{
            mockWebServer.shutdown();
        } catch(Exception quiet){}
    }

    @Test
    void testRetryWithSocketTimeoutException() throws IOException {
        MockWebServer mockWebServer = new MockWebServer();
        String expectedResponse = "expect that it works";
        mockWebServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START));
        mockWebServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START));
        mockWebServer.enqueue(new MockResponse().setResponseCode(200));
        mockWebServer.start();
        HttpUrl url = mockWebServer.url("/test");
        RestTemplate restTemplate = restTemplateFactory.restTemplateWithTracing(3, 10000, 10000);

        ResponseEntity<String> response = restTemplate.postForEntity(url.uri(), "myRequest", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockWebServer.shutdown();
    }
}