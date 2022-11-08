package it.pagopa.pn.commons.pnclients;

import it.pagopa.pn.commons.exceptions.PnHttpResponseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.http.client.MockClientHttpResponse;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

class RestTemplateResponseErrorHandlerTest {

    RestTemplateResponseErrorHandler restTemplateResponseErrorHandler;

    @BeforeEach
    public void init(){
        restTemplateResponseErrorHandler = new RestTemplateResponseErrorHandler();
    }

    @Test
    void hasError() throws IOException {
        ClientHttpResponse resp = new MockClientHttpResponse(new byte[0], HttpStatus.BAD_REQUEST);

        boolean res = restTemplateResponseErrorHandler.hasError(resp);
        assertTrue(res);
    }

    @Test
    void hasError2() throws IOException {
        ClientHttpResponse resp = new MockClientHttpResponse(new byte[0], HttpStatus.INTERNAL_SERVER_ERROR);

        boolean res = restTemplateResponseErrorHandler.hasError(resp);
        assertTrue(res);
    }

    @Test
    void hasError3() throws IOException {
        ClientHttpResponse resp = new MockClientHttpResponse(new byte[0], HttpStatus.ACCEPTED);

        boolean res = restTemplateResponseErrorHandler.hasError(resp);
        assertFalse(res);
    }

    @Test
    void handleError() throws IOException {
        ClientHttpResponse resp = new MockClientHttpResponse(new byte[0], HttpStatus.INTERNAL_SERVER_ERROR);

        assertThrows(PnHttpResponseException.class, () -> restTemplateResponseErrorHandler.handleError(resp));
    }

    @Test
    void testHandleError() throws URISyntaxException, IOException {
        URI url = new URI("http://something.com");
        ClientHttpResponse resp = new MockClientHttpResponse(new byte[0], HttpStatus.INTERNAL_SERVER_ERROR);

        assertThrows(PnHttpResponseException.class, () -> restTemplateResponseErrorHandler.handleError(url, HttpMethod.GET, resp));
    }
}