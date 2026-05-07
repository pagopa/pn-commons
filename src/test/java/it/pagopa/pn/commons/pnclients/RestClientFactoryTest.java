package it.pagopa.pn.commons.pnclients;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.commons.exceptions.PnHttpResponseException;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.SocketPolicy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RestClientFactoryTest {

    ObjectMapper objectMapper;
    RestClientFactory restClientFactory;
    RestClient.Builder restClientBuilder;

    @BeforeEach
    void init() {
        restClientBuilder = RestClient.builder();
        restClientFactory = new RestClientFactory();
        objectMapper = new ObjectMapper();
    }

    @Test
    void restClientWithTracing() {
        RestClient res = restClientFactory.restClientWithTracing(3, 3000, 8000, restClientBuilder, objectMapper);
        assertNotNull(res);
    }

    @Test
    void testRetryWithThreeFails() throws IOException {
        RestClient restClient = restClientFactory.restClientWithTracing(3, 3000, 8000, restClientBuilder, objectMapper);
        MockWebServer mockWebServer = new MockWebServer();
        String expectedResponse = "expect that it works";
        mockWebServer.enqueue(new MockResponse().setResponseCode(429));
        mockWebServer.enqueue(new MockResponse().setResponseCode(502));
        mockWebServer.enqueue(new MockResponse().setResponseCode(429));
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(expectedResponse));
        mockWebServer.start();

        HttpUrl url = mockWebServer.url("/test");
        ResponseEntity<String> resp = restClient.post().uri(url.uri()).body("myRequest").retrieve().toEntity(String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isEqualTo(expectedResponse);

        mockWebServer.shutdown();
    }

    @Test
    void testRetryWithTwoFails() throws IOException {
        RestClient restClient =
            restClientFactory.restClientWithTracing(3, 3000, 8000, restClientBuilder, objectMapper);
        MockWebServer mockWebServer = new MockWebServer();
        String expectedResponse = "expect that it works";
        mockWebServer.enqueue(new MockResponse().setResponseCode(429));
        mockWebServer.enqueue(new MockResponse().setResponseCode(502));
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(expectedResponse));
        mockWebServer.start();

        HttpUrl url = mockWebServer.url("/test");
        ResponseEntity<String> resp = restClient.post().uri(url.uri()).body("myRequest").retrieve().toEntity(String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isEqualTo(expectedResponse);

        mockWebServer.shutdown();
    }

    @Test
    void testRetryWithOneFail() throws IOException {
        RestClient restClient =
            restClientFactory.restClientWithTracing(3, 3000, 8000, restClientBuilder, objectMapper);
        MockWebServer mockWebServer = new MockWebServer();
        String expectedResponse = "expect that it works";
        mockWebServer.enqueue(new MockResponse().setResponseCode(502));
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(expectedResponse));
        mockWebServer.start();

        HttpUrl url = mockWebServer.url("/test");
        ResponseEntity<String> resp = restClient.post().uri(url.uri()).body("myRequest").retrieve().toEntity(String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isEqualTo(expectedResponse);

        mockWebServer.shutdown();
    }

    @Test
    void testRetryFourTimesButParameterIsSetToThree() throws IOException {
        RestClient restClient =
            restClientFactory.restClientWithTracing(3, 3000, 8000, restClientBuilder, objectMapper);
        MockWebServer mockWebServer = new MockWebServer();
        String expectedResponse = "expect that it works";
        mockWebServer.enqueue(new MockResponse().setResponseCode(429));
        mockWebServer.enqueue(new MockResponse().setResponseCode(502));
        mockWebServer.enqueue(new MockResponse().setResponseCode(429));
        mockWebServer.enqueue(new MockResponse().setResponseCode(429));
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(expectedResponse));
        mockWebServer.start();

        HttpUrl url = mockWebServer.url("/test");
        URI uri = url.uri();
        Assertions.assertThrows(PnHttpResponseException.class,
                () -> restClient.post().uri(uri).body("myRequest").retrieve().body(String.class));

        mockWebServer.shutdown();
    }

    @Test
    void testExceptionNotRetryable() throws IOException {
        RestClient restClient =
            restClientFactory.restClientWithTracing(3, 3000, 8000, restClientBuilder, objectMapper);
        MockWebServer mockWebServer = new MockWebServer();
        String expectedResponse = "expect that it works";
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(expectedResponse));
        mockWebServer.start();

        HttpUrl url = mockWebServer.url("/test");
        URI uri = url.uri();
        Assertions.assertThrows(PnHttpResponseException.class,
                () -> restClient.post().uri(uri).body("myRequest").retrieve().body(String.class));

        mockWebServer.shutdown();
    }

    @Test
    void testRetryWithConnectionException() throws IOException {
        RestClient restClient =
            restClientFactory.restClientWithTracing(3, 10000, 10000, restClientBuilder, objectMapper);
        MockWebServer mockWebServer = new MockWebServer();
        mockWebServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START));
        mockWebServer.enqueue(new MockResponse().setResponseCode(200));
        mockWebServer.start();

        HttpUrl url = mockWebServer.url("/test");
        ResponseEntity<String> response = restClient.post().uri(url.uri()).body("myRequest").retrieve().toEntity(String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        mockWebServer.shutdown();
    }

    @Test
    void testSocketTimeoutException() throws IOException {
        RestClient restClient =
            restClientFactory.restClientWithTracing(3, 1000, 1000, restClientBuilder, objectMapper);
        MockWebServer mockWebServer = new MockWebServer();
        String expectedResponse = "expect that it works";
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(expectedResponse).setBodyDelay(1, TimeUnit.HOURS));
        mockWebServer.start();

        HttpUrl url = mockWebServer.url("/test?param=1");
        URI uri = url.uri();

        Duration deltaTime = Duration.ZERO;
        Instant beginTime = Instant.now();
        Exception err = null;
        try {
            restClient.post().uri(uri.toString()).body("Body").retrieve().body(String.class);
        } catch (Exception e) {
            err = e;
            deltaTime = Duration.between(beginTime, Instant.now());
        }
        Assertions.assertNotNull(err);
        Assertions.assertInstanceOf(ResourceAccessException.class, err);
        Assertions.assertInstanceOf(SocketTimeoutException.class, err.getCause());
        Assertions.assertTrue(deltaTime.getSeconds() >= 3);

        try {
            mockWebServer.shutdown();
        } catch (Exception quiet) {}
    }

    @Test
    void testRetryWithSocketTimeoutException() throws IOException {
        MockWebServer mockWebServer = new MockWebServer();
        mockWebServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START));
        mockWebServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START));
        mockWebServer.enqueue(new MockResponse().setResponseCode(200));
        mockWebServer.start();

        HttpUrl url = mockWebServer.url("/test");
        RestClient restClient =
            restClientFactory.restClientWithTracing(3, 10000, 10000, restClientBuilder, objectMapper);

        ResponseEntity<String> response = restClient.post().uri(url.uri()).body("myRequest").retrieve().toEntity(String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        mockWebServer.shutdown();
    }
}
