package it.pagopa.pn.commons.pnclients;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


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

}
