package it.pagopa.pn.commons.pnclients;

import it.pagopa.pn.commons.exceptions.PnHttpResponseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.classify.Classifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.*;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.ExponentialRandomBackOffPolicy;
import org.springframework.retry.policy.ExceptionClassifierRetryPolicy;
import org.springframework.retry.policy.NeverRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.StreamUtils;

import javax.net.ssl.SSLHandshakeException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Interceptor per la gestione delle retry del RestClient.
 * A differenza di RestTemplate, RestClient viene esposto come interfaccia e non permette override del doExecute()
 */
@Slf4j
public class RestClientRetryable implements ClientHttpRequestInterceptor {

    private final RetryTemplate retryTemplate;
    private final ClientHttpRequestFactory requestFactory;

    public RestClientRetryable(int retryMaxAttempts, ClientHttpRequestFactory requestFactory) {
        this.requestFactory = requestFactory;
        this.retryTemplate = createRetryTemplate(retryMaxAttempts);
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        return retryTemplate.execute(context -> doExecuteWithRetry(request, body));
    }

    private ClientHttpResponse doExecuteWithRetry(HttpRequest originalRequest, byte[] body) throws IOException {
        HttpMethod method = originalRequest.getMethod();
        ClientHttpRequest newRequest = requestFactory.createRequest(originalRequest.getURI(), method);
        newRequest.getHeaders().putAll(originalRequest.getHeaders());
        if (body.length > 0) {
            newRequest.getBody().write(body);
        }

        ClientHttpResponse response = newRequest.execute();

        HttpStatus httpStatus = HttpStatus.resolve(response.getStatusCode().value());
        if (httpStatus != null && isRetryableStatus(httpStatus)) {
            response.close();
            throw new PnHttpResponseException("Retryable HTTP status: " + httpStatus.value(), httpStatus.value());
        }

        // Wrapping del body necessario per la SocketTimeoutException
        return new ClientHttpResponseWrapper(response, StreamUtils.copyToByteArray(response.getBody()));
    }

    private boolean isRetryableStatus(HttpStatus httpStatus) {
        return switch (httpStatus) {
            case BAD_GATEWAY, SERVICE_UNAVAILABLE, GATEWAY_TIMEOUT, TOO_MANY_REQUESTS -> true;
            default -> false;
        };
    }

    private RetryTemplate createRetryTemplate(int retryMaxAttempts) {
        RetryTemplate retry = new RetryTemplate();
        ExceptionClassifierRetryPolicy policy = new ExceptionClassifierRetryPolicy();
        policy.setExceptionClassifier(configureStatusCodeBasedRetryPolicy(retryMaxAttempts));
        ExponentialRandomBackOffPolicy exponentialRandomBackOffPolicy = new ExponentialRandomBackOffPolicy();
        exponentialRandomBackOffPolicy.setInitialInterval(20);
        exponentialRandomBackOffPolicy.setMaxInterval(30000L);
        exponentialRandomBackOffPolicy.setMultiplier(2);
        retry.setBackOffPolicy(exponentialRandomBackOffPolicy);
        retry.setRetryPolicy(policy);
        return retry;
    }

    private Classifier<Throwable, RetryPolicy> configureStatusCodeBasedRetryPolicy(int retryMaxAttempts) {
        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy(retryMaxAttempts);
        NeverRetryPolicy neverRetryPolicy = new NeverRetryPolicy();

        return throwable -> {
            RetryPolicy retryPolicy;
            if (throwable instanceof PnHttpResponseException pnEx && pnEx.getStatusCode() > 0) {
                HttpStatus httpStatus = HttpStatus.resolve(pnEx.getStatusCode());
                retryPolicy = (httpStatus != null && isRetryableStatus(httpStatus)) ? simpleRetryPolicy : neverRetryPolicy;
            } else if (isIOExceptionRetryable(throwable)) {
                retryPolicy = simpleRetryPolicy;
            } else {
                retryPolicy = neverRetryPolicy;
            }
            if (retryPolicy instanceof SimpleRetryPolicy) {
                log.warn("Exception caught by retry", throwable);
            }
            return retryPolicy;
        };
    }

    private boolean isIOExceptionRetryable(Throwable throwable) {
        return throwable instanceof SocketTimeoutException ||
            throwable instanceof SSLHandshakeException ||
            throwable instanceof UnknownHostException ||
            throwable instanceof SocketException;
    }

    /**
     * Wrapper del ClientHttpResponse che riespone il body a partire da bytearray
     * Utilizzato per casi come SocketTimeoutException in cui lo stream originale verrebbe esaurito
     */
    private record ClientHttpResponseWrapper(ClientHttpResponse delegate, byte[] body) implements ClientHttpResponse {

            @Override
            public HttpStatusCode getStatusCode() throws IOException {
                return delegate.getStatusCode();
            }

            @Override
            public String getStatusText() throws IOException {
                return delegate.getStatusText();
            }

            @Override
            public HttpHeaders getHeaders() {
                return delegate.getHeaders();
            }

            @Override
            public InputStream getBody() {
                return new ByteArrayInputStream(body);
            }

            @Override
            public void close() {
                delegate.close();
            }
    }
}
