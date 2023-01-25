package it.pagopa.pn.commons.pnclients;

import it.pagopa.pn.commons.exceptions.PnHttpResponseException;
import org.jetbrains.annotations.NotNull;
import org.springframework.classify.Classifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.ExponentialRandomBackOffPolicy;
import org.springframework.retry.policy.ExceptionClassifierRetryPolicy;
import org.springframework.retry.policy.NeverRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLHandshakeException;
import java.net.ConnectException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Map;

public class RestTemplateDecorator extends RestTemplate {

    private final RetryTemplate retryTemplate;

    public RestTemplateDecorator(int retryMaxAttempts) {
        this.retryTemplate = createRetryTemplate(retryMaxAttempts);
    }


    @Override
    public <T> T getForObject(URI url, @NotNull Class<T> responseType) throws RestClientException {
        return retryTemplate.execute(context ->  super.getForObject(url, responseType));
    }

    @Override
    public <T> T getForObject(String url, @NotNull Class<T> responseType, Object @NotNull ... uriVariables) throws RestClientException {
        return retryTemplate.execute(context -> super.getForObject(url, responseType, uriVariables));
    }

    @Override
    public <T> T getForObject(String url, @NotNull Class<T> responseType, @NotNull Map<String, ?> uriVariables) throws RestClientException {
        return retryTemplate.execute(context -> super.getForObject(url, responseType, uriVariables));
    }

    @Override
    public <T> ResponseEntity<T> getForEntity(@NotNull URI url, @NotNull Class<T> responseType) throws RestClientException {
        return retryTemplate.execute(context -> super.getForEntity(url, responseType));
    }

    @Override
    public <T> ResponseEntity<T> getForEntity(@NotNull String url, @NotNull Class<T> responseType, Object @NotNull ... uriVariables) throws RestClientException {
        return retryTemplate.execute(context -> super.getForEntity(url, responseType, uriVariables));
    }

    @Override
    public <T> ResponseEntity<T> getForEntity(@NotNull String url, @NotNull Class<T> responseType, @NotNull Map<String, ?> uriVariables) throws RestClientException {
        return retryTemplate.execute(context -> super.getForEntity(url, responseType, uriVariables));
    }

    @Override
    public <T> ResponseEntity<T> postForEntity(@NotNull URI url, Object request, Class<T> responseType) throws RestClientException {
        return retryTemplate.execute(context -> super.postForEntity(url, request, responseType));
    }

    @Override
    public <T> ResponseEntity<T> postForEntity(@NotNull String url, Object request, @NotNull Class<T> responseType, Object @NotNull ... uriVariables) throws RestClientException {
        return retryTemplate.execute(context -> super.postForEntity(url, request, responseType, uriVariables));
    }

    @Override
    public <T> ResponseEntity<T> postForEntity(@NotNull String url, Object request, @NotNull Class<T> responseType, @NotNull Map<String, ?> uriVariables) throws RestClientException {
        return retryTemplate.execute(context -> super.postForEntity(url, request, responseType, uriVariables));
    }

    @Override
    public <T> T postForObject(@NotNull URI url, Object request, @NotNull Class<T> responseType) throws RestClientException {
        return retryTemplate.execute(context -> super.postForObject(url, request, responseType));
    }

    @Override
    public <T> T postForObject(@NotNull String url, Object request, @NotNull Class<T> responseType, Object @NotNull ... uriVariables) throws RestClientException {
        return retryTemplate.execute(context -> super.postForObject(url, request, responseType, uriVariables));
    }

    @Override
    public <T> T postForObject(@NotNull String url, Object request, @NotNull Class<T> responseType, @NotNull Map<String, ?> uriVariables) throws RestClientException {
        return retryTemplate.execute(context -> super.postForObject(url, request, responseType, uriVariables));
    }

    private RetryTemplate createRetryTemplate(int retryMaxAttempts) {
        RetryTemplate retry = new RetryTemplate();
        ExceptionClassifierRetryPolicy policy = new ExceptionClassifierRetryPolicy();
        policy.setExceptionClassifier(configureStatusCodeBasedRetryPolicy(retryMaxAttempts));
        ExponentialRandomBackOffPolicy exponentialRandomBackOffPolicy = new ExponentialRandomBackOffPolicy();
        exponentialRandomBackOffPolicy.setInitialInterval(20);
        exponentialRandomBackOffPolicy.setMaxInterval(30000L); //default value of ExponentialRandomBackOffPolicy
        exponentialRandomBackOffPolicy.setMultiplier(2); //default value of ExponentialRandomBackOffPolicy
        retry.setBackOffPolicy(exponentialRandomBackOffPolicy);
        retry.setRetryPolicy(policy);

        return retry;
    }

    private Classifier<Throwable, RetryPolicy> configureStatusCodeBasedRetryPolicy(int retryMaxAttempts) {
        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy(retryMaxAttempts);
        NeverRetryPolicy neverRetryPolicy = new NeverRetryPolicy();

        return throwable -> {
            if (throwable instanceof HttpStatusCodeException httpException) {
                return getRetryPolicyForStatus(httpException.getStatusCode(), simpleRetryPolicy, neverRetryPolicy);
            }
            if(throwable instanceof PnHttpResponseException pnHttpResponseException && pnHttpResponseException.getStatusCode() > 0) {
                return getRetryPolicyForStatus(HttpStatus.valueOf(pnHttpResponseException.getStatusCode()), simpleRetryPolicy, neverRetryPolicy);
            }
            if(throwable instanceof ConnectException ||
                    throwable instanceof SSLHandshakeException ||
                    throwable instanceof UnknownHostException) {
                return simpleRetryPolicy;
            }
            return neverRetryPolicy;
        };
    }

    private RetryPolicy getRetryPolicyForStatus(HttpStatus httpStatus, SimpleRetryPolicy simpleRetryPolicy,
                                                NeverRetryPolicy neverRetryPolicy) {
        return switch (httpStatus) {
            case BAD_GATEWAY, SERVICE_UNAVAILABLE, GATEWAY_TIMEOUT, TOO_MANY_REQUESTS -> simpleRetryPolicy;
            default -> neverRetryPolicy;
        };
    }

}
