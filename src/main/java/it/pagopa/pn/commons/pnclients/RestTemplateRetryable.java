package it.pagopa.pn.commons.pnclients;

import it.pagopa.pn.commons.exceptions.PnHttpResponseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.classify.Classifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.ExponentialRandomBackOffPolicy;
import org.springframework.retry.policy.ExceptionClassifierRetryPolicy;
import org.springframework.retry.policy.NeverRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.*;

import javax.net.ssl.SSLHandshakeException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.UnknownHostException;

@Slf4j
public class RestTemplateRetryable extends RestTemplate {

    private final RetryTemplate retryTemplate;

    public RestTemplateRetryable(int retryMaxAttempts) {
        this.retryTemplate = createRetryTemplate(retryMaxAttempts);
    }

    @Override
    protected <T> T doExecute(URI url, @Nullable String uriTemplate, @Nullable HttpMethod method, @Nullable RequestCallback requestCallback,
                              @Nullable ResponseExtractor<T> responseExtractor) throws RestClientException {
        return retryTemplate.execute(context -> super.doExecute(url, uriTemplate, method, requestCallback, responseExtractor));
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
            RetryPolicy retryPolicy;
            if (throwable instanceof HttpStatusCodeException httpException) {
                HttpStatus httpStatus = HttpStatus.resolve(httpException.getStatusCode().value());
                retryPolicy = getRetryPolicyForStatus(httpStatus, simpleRetryPolicy, neverRetryPolicy);
            }
            else if(throwable instanceof PnHttpResponseException pnHttpResponseException && pnHttpResponseException.getStatusCode() > 0) {
                retryPolicy = getRetryPolicyForStatus(HttpStatus.valueOf(pnHttpResponseException.getStatusCode()), simpleRetryPolicy, neverRetryPolicy);
            }
            else if(throwable instanceof ResourceAccessException && isIOExceptionRetryable(throwable.getCause())){
                retryPolicy = simpleRetryPolicy;
            }
            else if (isIOExceptionRetryable(throwable)) {
                retryPolicy = simpleRetryPolicy;
            }
            else retryPolicy = neverRetryPolicy;

            if(retryPolicy instanceof SimpleRetryPolicy) {
                log.warn("Exception caught by retry", throwable);
            }
            return retryPolicy;
        };
    }

    private boolean isIOExceptionRetryable(Throwable throwable){
        return (throwable instanceof SocketTimeoutException ||
            throwable instanceof SSLHandshakeException ||
            throwable instanceof UnknownHostException ||
            throwable instanceof SocketException
        );
    }
    private RetryPolicy getRetryPolicyForStatus(HttpStatus httpStatus, SimpleRetryPolicy simpleRetryPolicy,
                                                NeverRetryPolicy neverRetryPolicy) {
        return switch (httpStatus) {
            case BAD_GATEWAY, SERVICE_UNAVAILABLE, GATEWAY_TIMEOUT, TOO_MANY_REQUESTS -> simpleRetryPolicy;
            default -> neverRetryPolicy;
        };
    }

}
