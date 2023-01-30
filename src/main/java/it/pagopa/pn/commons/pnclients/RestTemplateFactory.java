package it.pagopa.pn.commons.pnclients;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.*;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RestTemplateFactory {

    @Bean
    @Qualifier("withTracing")
    public RestTemplate restTemplateWithTracing(@Value("${pn.commons.retry.max-attempts}") int retryMaxAttempts,
                                                @Value("${pn.commons.connection-timeout-millis}") int connectionTimeout) {
        //RetryTemplate nel parametro retryMaxAttempts vuole le invocazioni totali (compresa la prima che non è fallita)
        RestTemplate template = new RestTemplateRetryable(retryMaxAttempts + 1);
        configureRestTemplate(connectionTimeout, template);
        return template;
    }

    protected void configureRestTemplate(int connectionTimeout, RestTemplate template) {
        SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(connectionTimeout);
        clientHttpRequestFactory.setReadTimeout(connectionTimeout);
        template.setRequestFactory(clientHttpRequestFactory);
        enrichWithTracing(template);
        template.setErrorHandler(new RestTemplateResponseErrorHandler());
    }

    public void enrichWithTracing(RestTemplate template) {
        List<ClientHttpRequestInterceptor> interceptors
                = template.getInterceptors();
        if (CollectionUtils.isEmpty(interceptors)) {
            interceptors = new ArrayList<>();
        }
        interceptors.add(new RestTemplateHeaderModifierInterceptor());
        template.setInterceptors(interceptors);
    }


    // interceptor per il trace verso altri MS
    @Value("${pn.log.trace-id-header}")
    private String traceIdHeader;

    public class RestTemplateHeaderModifierInterceptor
            implements ClientHttpRequestInterceptor {

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
