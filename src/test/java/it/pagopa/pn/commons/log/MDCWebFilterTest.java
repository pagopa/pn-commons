package it.pagopa.pn.commons.log;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.server.WebHandler;
import org.springframework.web.server.handler.DefaultWebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = { "pn.log.trace-id-header=X-Amzn-Trace-Id", "pn.log.jti-header=x-pagopa-pn-jti" })
class MDCWebFilterTest {

    public static final String MY_HEADER = "Root=1-61b1d38b-752391d8200695e11e2e5bac;";
    public static final String MY_HEADER_JTI = "1234567890";

    @TestConfiguration
    static class SpringTestConfiguration {
        @Bean
        public MDCWebFilter mdcTraceIdWebFilter(){
            return new MDCWebFilter();
        }
    }


    @Autowired
    MDCWebFilter mdcTraceIdWebFilter;

    @Test
    void filter() {

        MockServerHttpRequest request = MockServerHttpRequest.get("http://localhost")
                .header("X-Amzn-Trace-Id", MY_HEADER).build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        WebHandler webHandler = serverWebExchange -> {
            Assertions.assertEquals(MY_HEADER, MDC.get(MDCWebFilter.MDC_TRACE_ID_KEY));
            return Mono.empty();
        };
        WebFilterChain filterChain = new DefaultWebFilterChain(webHandler, Collections.emptyList());

        mdcTraceIdWebFilter.filter(exchange, filterChain).block();

    }

    @Test
    void filterWithTraceAndJti() {

        MockServerHttpRequest request = MockServerHttpRequest.get("http://localhost")
                .header("X-Amzn-Trace-Id", MY_HEADER)
                .header("x-pagopa-pn-jti", MY_HEADER_JTI).build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        WebHandler webHandler = serverWebExchange -> {
            Assertions.assertEquals(MY_HEADER, MDC.get(MDCWebFilter.MDC_TRACE_ID_KEY));
            Assertions.assertEquals(MY_HEADER_JTI, MDC.get(MDCWebFilter.MDC_JTI_KEY));
            return Mono.empty();
        };
        WebFilterChain filterChain = new DefaultWebFilterChain(webHandler, Collections.emptyList());

        mdcTraceIdWebFilter.filter(exchange, filterChain).block();

    }


    @Test
    void filterWithJti() {

        MockServerHttpRequest request = MockServerHttpRequest.get("http://localhost")
                .header("x-pagopa-pn-jti", MY_HEADER_JTI).build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        WebHandler webHandler = serverWebExchange -> {
            Assertions.assertNotNull(MDC.get(MDCWebFilter.MDC_TRACE_ID_KEY));
            Assertions.assertEquals(MY_HEADER_JTI, MDC.get(MDCWebFilter.MDC_JTI_KEY));
            return Mono.empty();
        };
        WebFilterChain filterChain = new DefaultWebFilterChain(webHandler, Collections.emptyList());

        mdcTraceIdWebFilter.filter(exchange, filterChain).block();

    }
}