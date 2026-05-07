package it.pagopa.pn.commons.pnclients;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.io.IOException;

public class RestClientFactory {

    @Bean
    public RestClient restClientWithTracing(
            @Value("${pn.commons.retry.max-attempts}") int retryMaxAttempts,
            @Value("${pn.commons.connection-timeout-millis}") int connectionTimeout,
            @Value("${pn.commons.read-timeout-millis}") int readTimeout,
            RestClient.Builder restClientBuilder,
            ObjectMapper objectMapper
    ) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectionTimeout);
        requestFactory.setReadTimeout(readTimeout);
        RestClientRetryable retryInterceptor = new RestClientRetryable(retryMaxAttempts + 1, requestFactory);

        return restClientBuilder
                .requestFactory(requestFactory)
                .requestInterceptors(interceptors -> {
                    interceptors.add(new RestClientHeaderModifierInterceptor());
                    interceptors.add(retryInterceptor);
                })
                .defaultStatusHandler(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        new RestClientResponseErrorHandler(objectMapper)
                )
                .build();
    }


    // Interceptor per il trace verso altri MS
    @Value("${pn.log.trace-id-header}")
    private String traceIdHeader;

    public class RestClientHeaderModifierInterceptor implements ClientHttpRequestInterceptor {

        @Override
        public ClientHttpResponse intercept(
                HttpRequest request,
                byte[] body,
                ClientHttpRequestExecution execution) throws IOException {
            String traceId = MDC.get("trace_id");
            if (traceId != null) {
                request.getHeaders().add(traceIdHeader, traceId);
            }
            return execution.execute(request, body);
        }
    }
}
