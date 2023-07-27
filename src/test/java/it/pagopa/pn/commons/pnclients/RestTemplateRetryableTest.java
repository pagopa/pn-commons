package it.pagopa.pn.commons.pnclients;

import it.pagopa.pn.commons.configs.HttpClientConfig;
import java.net.SocketTimeoutException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.validation.constraints.AssertTrue;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.SocketPolicy;
import org.junit.Assert;
import org.junit.function.ThrowingRunnable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.ResourceAccessException;

import static org.assertj.core.api.Assertions.assertThat;
/*
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { HttpClientConfig.class })*/
class RestTemplateRetryableTest {

    private RestTemplateRetryable restTemplateRetryable;
    @BeforeEach
    public void init() {
        restTemplateRetryable = new RestTemplateRetryable(3);
    }

    @Test
    void getForObjectTest() throws IOException {
        MockWebServer mockWebServer = new MockWebServer();

        String expectedResponse = "expect that it works";
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(expectedResponse));
        mockWebServer.start();
        HttpUrl url = mockWebServer.url("/test");
        URI uri = url.uri();
        String response = restTemplateRetryable.getForObject(uri, String.class);
        assertThat(response).isEqualTo(expectedResponse);
        mockWebServer.shutdown();
    }

    @Test
    void getForObjectTwoTest() throws IOException {
        MockWebServer mockWebServer = new MockWebServer();

        String expectedResponse = "expect that it works";
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(expectedResponse));
        mockWebServer.start();
        HttpUrl url = mockWebServer.url("/test?param=1");
        URI uri = url.uri();
        String response = restTemplateRetryable.getForObject(uri.toString(), String.class, "param", 1);
        assertThat(response).isEqualTo(expectedResponse);
        mockWebServer.shutdown();
    }

    @Test
    void getForObjectThreeTest() throws IOException {
        MockWebServer mockWebServer = new MockWebServer();

        String expectedResponse = "expect that it works";
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(expectedResponse));
        mockWebServer.start();
        HttpUrl url = mockWebServer.url("/test?param=1");
        URI uri = url.uri();
        String response = restTemplateRetryable.getForObject(uri.toString(), String.class, Map.of("param", 1));
        assertThat(response).isEqualTo(expectedResponse);
        mockWebServer.shutdown();
    }

    @Test
    void getForEntityTest() throws IOException {
        MockWebServer mockWebServer = new MockWebServer();

        String expectedResponse = "expect that it works";
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(expectedResponse));
        mockWebServer.start();
        HttpUrl url = mockWebServer.url("/test");
        URI uri = url.uri();
        ResponseEntity<String> response = restTemplateRetryable.getForEntity(uri, String.class);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(expectedResponse);
        mockWebServer.shutdown();
    }

    @Test
    void getForEntityTwoTest() throws IOException {
        MockWebServer mockWebServer = new MockWebServer();

        String expectedResponse = "expect that it works";
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(expectedResponse));
        mockWebServer.start();
        HttpUrl url = mockWebServer.url("/test?param=1");
        URI uri = url.uri();
        ResponseEntity<String> response = restTemplateRetryable.getForEntity(uri.toString(), String.class, "param", 1);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(expectedResponse);
        mockWebServer.shutdown();
    }

    @Test
    void getForEntityThreeTest() throws IOException {
        MockWebServer mockWebServer = new MockWebServer();

        String expectedResponse = "expect that it works";
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(expectedResponse));
        mockWebServer.start();
        HttpUrl url = mockWebServer.url("/test?param=1");
        URI uri = url.uri();
        ResponseEntity<String> response = restTemplateRetryable.getForEntity(uri.toString(), String.class, Map.of("param", 1));
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(expectedResponse);
        mockWebServer.shutdown();
    }

    @Test
    void postForObjectTest() throws IOException {
        MockWebServer mockWebServer = new MockWebServer();

        String expectedResponse = "expect that it works";
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(expectedResponse));
        mockWebServer.start();
        HttpUrl url = mockWebServer.url("/test");
        URI uri = url.uri();
        String response = restTemplateRetryable.postForObject(uri, "Body", String.class);
        assertThat(response).isEqualTo(expectedResponse);
        mockWebServer.shutdown();
    }

    @Test
    void postForObjectTwoTest() throws IOException {
        MockWebServer mockWebServer = new MockWebServer();

        String expectedResponse = "expect that it works";
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(expectedResponse));
        mockWebServer.start();
        HttpUrl url = mockWebServer.url("/test?param=1");
        URI uri = url.uri();
        String response = restTemplateRetryable.postForObject(uri.toString(), "Body", String.class, "param", 1);
        assertThat(response).isEqualTo(expectedResponse);
        mockWebServer.shutdown();
    }

    @Test
    void postForObjectThreeTest() throws IOException {
        MockWebServer mockWebServer = new MockWebServer();

        String expectedResponse = "expect that it works";
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(expectedResponse));
        mockWebServer.start();
        HttpUrl url = mockWebServer.url("/test?param=1");
        URI uri = url.uri();
        String response = restTemplateRetryable.postForObject(uri.toString(), "Body", String.class, Map.of("param", 1));
        assertThat(response).isEqualTo(expectedResponse);
        mockWebServer.shutdown();
    }

    @Test
    void postForEntityTest() throws IOException {
        MockWebServer mockWebServer = new MockWebServer();

        String expectedResponse = "expect that it works";
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(expectedResponse));
        mockWebServer.start();
        HttpUrl url = mockWebServer.url("/test");
        URI uri = url.uri();
        ResponseEntity<String> response = restTemplateRetryable.postForEntity(uri, "Body", String.class);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(expectedResponse);
        mockWebServer.shutdown();
    }

    @Test
    void postForEntityTwoTest() throws IOException {
        MockWebServer mockWebServer = new MockWebServer();

        String expectedResponse = "expect that it works";
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(expectedResponse));
        mockWebServer.start();
        HttpUrl url = mockWebServer.url("/test?param=1");
        URI uri = url.uri();
        ResponseEntity<String> response = restTemplateRetryable.postForEntity(uri.toString(), "Body", String.class, "param", 1);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(expectedResponse);
        mockWebServer.shutdown();
    }

    @Test
    void postForEntityThreeTest() throws IOException {
        MockWebServer mockWebServer = new MockWebServer();
        String expectedResponse = "expect that it works";
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(expectedResponse));
        mockWebServer.start();
        HttpUrl url = mockWebServer.url("/test?param=1");
        URI uri = url.uri();
        ResponseEntity<String> response = restTemplateRetryable.postForEntity(uri.toString(), "Body", String.class, Map.of("param", 1));
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(expectedResponse);
        mockWebServer.shutdown();
    }

    @Test
    void testSocketTimeoutException() throws IOException {
        SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        simpleClientHttpRequestFactory.setConnectTimeout(1000);
        simpleClientHttpRequestFactory.setReadTimeout(1000);
        RestTemplateRetryable restTemplateRetryable = new RestTemplateRetryable(3, simpleClientHttpRequestFactory);

        MockWebServer mockWebServer = new MockWebServer();
        String expectedResponse = "expect that it works";
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(expectedResponse).setBodyDelay(1, TimeUnit.HOURS));
        mockWebServer.start();
        HttpUrl url = mockWebServer.url("/test?param=1");
        URI uri = url.uri();


        /*
        Assert.assertThrows(
            ResourceAccessException.class, () -> {
               restTemplateRetryable.postForEntity(uri.toString(), "Body", String.class, Map.of("param", 1));
           }
        );*/

        Duration deltaTime = Duration.ZERO;
        Instant beginTime = Instant.now();
        Exception err = null;
        try{
            restTemplateRetryable.postForEntity(uri.toString(), "Body", String.class, Map.of("param", 1));
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
        ResponseEntity<String> response = restTemplateRetryable.postForEntity(url.uri(), "myRequest", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockWebServer.shutdown();
    }

}
