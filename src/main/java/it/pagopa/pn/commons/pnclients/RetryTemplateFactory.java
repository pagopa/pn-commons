package it.pagopa.pn.commons.pnclients;

import lombok.extern.slf4j.Slf4j;
import org.springframework.classify.Classifier;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.ExponentialRandomBackOffPolicy;
import org.springframework.retry.policy.ExceptionClassifierRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import javax.net.ssl.SSLHandshakeException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

@Slf4j
public class RetryTemplateFactory {

    public static RetryTemplate create(Classifier<Throwable, RetryPolicy> classifier) {
        RetryTemplate retry = new RetryTemplate();
        ExceptionClassifierRetryPolicy policy = new ExceptionClassifierRetryPolicy();
        policy.setExceptionClassifier(classifier);
        ExponentialRandomBackOffPolicy exponentialRandomBackOffPolicy = new ExponentialRandomBackOffPolicy();
        exponentialRandomBackOffPolicy.setInitialInterval(20);
        exponentialRandomBackOffPolicy.setMaxInterval(30000L);
        exponentialRandomBackOffPolicy.setMultiplier(2);
        retry.setRetryPolicy(policy);
        retry.setBackOffPolicy(exponentialRandomBackOffPolicy);
        return retry;
    }

    public static boolean isIOExceptionRetryable(Throwable throwable) {
        return throwable instanceof SocketTimeoutException ||
               throwable instanceof SSLHandshakeException  ||
               throwable instanceof UnknownHostException   ||
               throwable instanceof SocketException;
    }
}
