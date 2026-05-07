package it.pagopa.pn.commons.pnclients;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.common.rest.error.v1.dto.Problem;
import it.pagopa.pn.commons.exceptions.PnHttpResponseException;
import it.pagopa.pn.commons.exceptions.mapper.DtoProblemToProblemErrorMapper;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
public class RestClientResponseErrorHandler implements RestClient.ResponseSpec.ErrorHandler {

    private final ObjectMapper objectMapper;

    public RestClientResponseErrorHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(@NotNull HttpRequest request, @NotNull ClientHttpResponse response) throws IOException {
        String body = null;
        try {
            body = getBody(response);
        } catch (IOException e) {
            log.trace("Empty body");
        }

        String errorMsg = String.format(
                "Error in call url=%s method=%s statusCode=%s and body=%s",
                request.getURI(),
                request.getMethod(),
                response.getStatusCode(),
                body
        );

        log.error(errorMsg);

        proceedWithThrowPnHttpResponseException(body, response.getStatusCode().value(), errorMsg);
    }

    private void proceedWithThrowPnHttpResponseException(String body, int rawStatusCode, String errorMsg) {
        Problem problem = null;
        try {
            problem = objectMapper.readValue(body, Problem.class);
        } catch (Exception e) {
            log.info("cannot parse body as problem", e);
        }

        if (problem != null && problem.getErrors() != null)
            throw new PnHttpResponseException(problem.getTitle(), problem.getDetail(), rawStatusCode,
                    problem.getErrors().stream().map(DtoProblemToProblemErrorMapper::toProblemError).toList(), null);
        else
            throw new PnHttpResponseException(errorMsg, rawStatusCode);
    }

    @NotNull
    private String getBody(ClientHttpResponse response) throws IOException {
        try (InputStream responseBody = response.getBody()) {
            return StreamUtils.copyToString(responseBody, StandardCharsets.UTF_8);
        }
    }
}
