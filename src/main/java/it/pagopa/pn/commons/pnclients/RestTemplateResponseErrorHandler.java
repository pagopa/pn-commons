package it.pagopa.pn.commons.pnclients;

import it.pagopa.pn.commons.exceptions.PnHttpResponseException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;
import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;

@Slf4j
@Component
public class RestTemplateResponseErrorHandler
        implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse httpResponse)
            throws IOException {
        return (httpResponse.getStatusCode().series() == CLIENT_ERROR
                        || httpResponse.getStatusCode().series() == SERVER_ERROR);
    }

    @Override
    public void handleError(@NotNull ClientHttpResponse response) throws IOException {
        String body = getBody(response);
        
        String errorMsg = String.format(
                "Error with statusCode=%s and body=%s",
                response.getStatusCode(),
                body
        );
        
        log.error(errorMsg);
        throw new PnHttpResponseException(errorMsg, response.getRawStatusCode());
    }

    @Override
    public void handleError(@NotNull URI url, @NotNull HttpMethod method, @NotNull ClientHttpResponse response)
            throws IOException {

        String body = getBody(response);
        
        String errorMsg = String.format(
                "Error in call url=%s method=%s statusCode=%s and body=%s",
                url, 
                method,
                response.getStatusCode(),
                body
        );
        
        log.error(errorMsg);

        throw new PnHttpResponseException(errorMsg, response.getRawStatusCode());
    }

    @NotNull
    private String getBody(ClientHttpResponse response) throws IOException {
        String body;
        try (InputStream responseBody = response.getBody()) {
            body = StreamUtils.copyToString(responseBody, StandardCharsets.UTF_8);
        }
        return body;
    }
}