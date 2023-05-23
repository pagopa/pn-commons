package it.pagopa.pn.commons.log;

import it.pagopa.pn.commons.utils.MDCUtils;
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
@TestPropertySource(properties = { "pn.log.trace-id-header=X-Amzn-Trace-Id", "pn.log.jti-header=x-pagopa-pn-jti",
        "pn.log.pn-uid-header=x-pagopa-pn-uid", "pn.log.cx-id-header=x-pagopa-cx-id", "pn.log.pn-cx-type-header=x-pagopa-pn-cx-type",
        "pn.log.pn-cx-groups-header=x-pagopa-pn-cx-groups", "pn.log.pn-cx-role-header=x-pagopa-pn-cx-role" })
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
            Assertions.assertEquals(MY_HEADER, MDC.get(MDCUtils.MDC_TRACE_ID_KEY));
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
            Assertions.assertEquals(MY_HEADER, MDC.get(MDCUtils.MDC_TRACE_ID_KEY));
            Assertions.assertEquals(MY_HEADER_JTI, MDC.get(MDCUtils.MDC_JTI_KEY));
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
            Assertions.assertNotNull(MDC.get(MDCUtils.MDC_TRACE_ID_KEY));
            Assertions.assertEquals(MY_HEADER_JTI, MDC.get(MDCUtils.MDC_JTI_KEY));
            return Mono.empty();
        };
        WebFilterChain filterChain = new DefaultWebFilterChain(webHandler, Collections.emptyList());

        mdcTraceIdWebFilter.filter(exchange, filterChain).block();

    }

    @Test
    void filterWithUid() {

        MockServerHttpRequest request = MockServerHttpRequest.get("http://localhost")
                .header("x-pagopa-pn-uid", "uid-value")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        WebHandler webHandler = serverWebExchange -> {
            Assertions.assertEquals("uid-value", MDC.get(MDCUtils.MDC_PN_UID_KEY));
            return Mono.empty();
        };
        WebFilterChain filterChain = new DefaultWebFilterChain(webHandler, Collections.emptyList());

        mdcTraceIdWebFilter.filter(exchange, filterChain).block();

    }

    @Test
    void filterWithCxId() {

        MockServerHttpRequest request = MockServerHttpRequest.get("http://localhost")
                .header("x-pagopa-cx-id", "cx-id-value")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        WebHandler webHandler = serverWebExchange -> {
            Assertions.assertEquals("cx-id-value", MDC.get(MDCUtils.MDC_CX_ID_KEY));
            return Mono.empty();
        };
        WebFilterChain filterChain = new DefaultWebFilterChain(webHandler, Collections.emptyList());

        mdcTraceIdWebFilter.filter(exchange, filterChain).block();

    }

    @Test
    void filterWithCxType() {

        MockServerHttpRequest request = MockServerHttpRequest.get("http://localhost")
                .header("x-pagopa-pn-cx-type", "PF")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        WebHandler webHandler = serverWebExchange -> {
            Assertions.assertEquals("PF", MDC.get(MDCUtils.MDC_PN_CX_TYPE_KEY));
            return Mono.empty();
        };
        WebFilterChain filterChain = new DefaultWebFilterChain(webHandler, Collections.emptyList());

        mdcTraceIdWebFilter.filter(exchange, filterChain).block();

    }

    @Test
    void filterWithCxGroups() {

        MockServerHttpRequest request = MockServerHttpRequest.get("http://localhost")
                .header("x-pagopa-pn-cx-groups", "cx-groups-value")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        WebHandler webHandler = serverWebExchange -> {
            Assertions.assertEquals("cx-groups-value", MDC.get(MDCUtils.MDC_PN_CX_GROUPS_KEY));
            return Mono.empty();
        };
        WebFilterChain filterChain = new DefaultWebFilterChain(webHandler, Collections.emptyList());

        mdcTraceIdWebFilter.filter(exchange, filterChain).block();

    }

    @Test
    void filterWithCxRole() {

        MockServerHttpRequest request = MockServerHttpRequest.get("http://localhost")
                .header("x-pagopa-pn-cx-role", "role-value")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        WebHandler webHandler = serverWebExchange -> {
            Assertions.assertEquals("role-value", MDC.get(MDCUtils.MDC_PN_CX_ROLE_KEY));
            return Mono.empty();
        };
        WebFilterChain filterChain = new DefaultWebFilterChain(webHandler, Collections.emptyList());

        mdcTraceIdWebFilter.filter(exchange, filterChain).block();

    }

    @Test
    void filterWithAllHeaders() {

        MockServerHttpRequest request = MockServerHttpRequest.get("http://localhost")
                .header("X-Amzn-Trace-Id", MY_HEADER)
                .header("x-pagopa-pn-jti", MY_HEADER_JTI)
                .header("x-pagopa-pn-uid", "uid-value")
                .header("x-pagopa-cx-id", "cx-id-value")
                .header("x-pagopa-pn-cx-type", "PF")
                .header("x-pagopa-pn-cx-groups", "cx-groups-value")
                .header("x-pagopa-pn-cx-role", "role-value")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        WebHandler webHandler = serverWebExchange -> {
            Assertions.assertEquals(MY_HEADER, MDC.get(MDCUtils.MDC_TRACE_ID_KEY));
            Assertions.assertEquals(MY_HEADER_JTI, MDC.get(MDCUtils.MDC_JTI_KEY));
            Assertions.assertEquals("uid-value", MDC.get(MDCUtils.MDC_PN_UID_KEY));
            Assertions.assertEquals("cx-id-value", MDC.get(MDCUtils.MDC_CX_ID_KEY));
            Assertions.assertEquals("PF", MDC.get(MDCUtils.MDC_PN_CX_TYPE_KEY));
            Assertions.assertEquals("cx-groups-value", MDC.get(MDCUtils.MDC_PN_CX_GROUPS_KEY));
            Assertions.assertEquals("role-value", MDC.get(MDCUtils.MDC_PN_CX_ROLE_KEY));
            return Mono.empty();
        };
        WebFilterChain filterChain = new DefaultWebFilterChain(webHandler, Collections.emptyList());

        mdcTraceIdWebFilter.filter(exchange, filterChain).block();

    }
}