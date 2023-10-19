package it.pagopa.pn.commons.pnclients;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.common.rest.error.v1.dto.Problem;
import it.pagopa.pn.common.rest.error.v1.dto.ProblemError;
import it.pagopa.pn.commons.exceptions.PnHttpResponseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.http.client.MockClientHttpResponse;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static it.pagopa.pn.commons.exceptions.PnExceptionsCodes.ERROR_CODE_PN_HTTPRESPONSE_GENERIC_ERROR;
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
    void handleErrorWithProblem() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Problem p = new Problem();
        p.setDetail("errore");
        p.setTitle("errore");
        p.setErrors(new ArrayList<>());
        p.getErrors().add(ProblemError.builder().code("CODICE_ERRORE").element("elemento").detail("dettaglio").build());
        String problem = objectMapper.writeValueAsString(p);
        ClientHttpResponse resp = new MockClientHttpResponse(problem.getBytes(StandardCharsets.UTF_8), HttpStatus.CONFLICT);

        try{
            restTemplateResponseErrorHandler.handleError(resp);
            Assertions.fail("no exception thrown");
        } catch (PnHttpResponseException exc){
            assertEquals(HttpStatus.CONFLICT.value(), exc.getStatusCode());
            assertEquals("CODICE_ERRORE", exc.getProblem().getErrors().get(0).getCode());
            assertEquals("elemento", exc.getProblem().getErrors().get(0).getElement());
            assertEquals("dettaglio", exc.getProblem().getErrors().get(0).getDetail());
        } catch (Exception exc1) {
            Assertions.fail("wrong exception thrown");
        }

    }

    private static class FakeProb {
        public String errore;
        public int codice;
    }

    @Test
    void handleErrorWithUnknownBody() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        FakeProb p = new FakeProb();
        p.errore = "boh";
        p.codice = 231;
        String problem = objectMapper.writeValueAsString(p);
        ClientHttpResponse resp = new MockClientHttpResponse(problem.getBytes(StandardCharsets.UTF_8), HttpStatus.CONFLICT);

        try{
            restTemplateResponseErrorHandler.handleError(resp);
            Assertions.fail("no exception thrown");
        } catch (PnHttpResponseException exc){
            assertEquals(HttpStatus.CONFLICT.value(), exc.getStatusCode());
            assertEquals(ERROR_CODE_PN_HTTPRESPONSE_GENERIC_ERROR, exc.getProblem().getErrors().get(0).getCode());
        } catch (Exception exc1) {
            Assertions.fail("wrong exception thrown");
        }

    }

    @Test
    void testHandleError() throws URISyntaxException, IOException {
        URI url = new URI("http://something.com");
        ClientHttpResponse resp = new MockClientHttpResponse(new byte[0], HttpStatus.INTERNAL_SERVER_ERROR);

        assertThrows(PnHttpResponseException.class, () -> restTemplateResponseErrorHandler.handleError(url, HttpMethod.GET, resp));
    }
}