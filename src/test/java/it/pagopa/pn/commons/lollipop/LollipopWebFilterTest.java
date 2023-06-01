package it.pagopa.pn.commons.lollipop;


import it.pagopa.tech.lollipop.consumer.command.LollipopConsumerCommand;
import it.pagopa.tech.lollipop.consumer.command.LollipopConsumerCommandBuilder;
import it.pagopa.tech.lollipop.consumer.model.CommandResult;
import it.pagopa.tech.lollipop.consumer.model.LollipopConsumerRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.server.WebHandler;
import org.springframework.web.server.handler.DefaultWebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static it.pagopa.tech.lollipop.consumer.command.impl.LollipopConsumerCommandImpl.VERIFICATION_SUCCESS_CODE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
class LollipopWebFilterTest {

    @Mock
    private LollipopConsumerCommandBuilder commandBuilder;

    @Mock
    private LollipopConsumerCommand command;

    private LollipopWebFilter webFilter;

    private static final String HEADER_FIELD = "x-pagopa-pn-src-ch";
    private static final String HEADER_VALUE = "IO";

    @BeforeEach
    void setup() {
        webFilter = new LollipopWebFilter(commandBuilder);
    }

    @Test
    void testFilterWithValidRequest() {
        MockServerHttpRequest request = MockServerHttpRequest.get("http://localhost")
                .header(HEADER_FIELD, HEADER_VALUE).build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        WebHandler webHandler = serverWebExchange -> {
            Assertions.assertEquals("IO", HEADER_VALUE);
            return Mono.empty();
        };

        CommandResult commandResult =
                new CommandResult(VERIFICATION_SUCCESS_CODE, "request validation success");

        Mockito.when(commandBuilder.createCommand(Mockito.any(LollipopConsumerRequest.class))).thenReturn(command);

        Mockito.when(command.doExecute()).thenReturn( commandResult );

        WebFilterChain filterChain = new DefaultWebFilterChain(webHandler, Collections.emptyList());

        assertDoesNotThrow( () -> {
            webFilter.filter(exchange, filterChain).block();
        });

    }

    @Test
    void testFilterWithValidPostRequest() {
        MockServerHttpRequest request = MockServerHttpRequest.post("http://localhost")
                .header(HEADER_FIELD, HEADER_VALUE).build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        WebHandler webHandler = serverWebExchange -> {
            Assertions.assertEquals("IO", HEADER_VALUE);
            return Mono.empty();
        };

        CommandResult commandResult =
                new CommandResult(VERIFICATION_SUCCESS_CODE, "request validation success");

        Mockito.when(commandBuilder.createCommand(Mockito.any(LollipopConsumerRequest.class))).thenReturn(command);

        Mockito.when(command.doExecute()).thenReturn( commandResult );

        WebFilterChain filterChain = new DefaultWebFilterChain(webHandler, Collections.emptyList());

        assertDoesNotThrow( () -> {
            webFilter.filter(exchange, filterChain).block();
        });

    }

    @Test
    void testFilterWithoutIOHeaderRequest() {
        MockServerHttpRequest request = MockServerHttpRequest.get("http://localhost").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        WebHandler webHandler = serverWebExchange -> {
            Assertions.assertEquals("IO", HEADER_VALUE);
            return Mono.empty();
        };

        WebFilterChain filterChain = new DefaultWebFilterChain(webHandler, Collections.emptyList());

        assertDoesNotThrow( () -> {
            webFilter.filter(exchange, filterChain).block();
        });

    }

    @Test
    void testFilterWithInvalidRequest() {
        MockServerHttpRequest request = MockServerHttpRequest.get("http://localhost")
                .header(HEADER_FIELD, HEADER_VALUE).build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        WebHandler webHandler = serverWebExchange -> {
            Assertions.assertEquals("IO", HEADER_VALUE);
            return Mono.empty();
        };

        CommandResult commandResult =
                new CommandResult("UNSUCCESSFUL_CODE", "request validation error");

        Mockito.when(commandBuilder.createCommand(Mockito.any(LollipopConsumerRequest.class))).thenReturn(command);

        Mockito.when(command.doExecute()).thenReturn( commandResult );

        WebFilterChain filterChain = new DefaultWebFilterChain(webHandler, Collections.emptyList());

        assertDoesNotThrow( () -> {
            webFilter.filter(exchange, filterChain).block();
        });

        Assertions.assertNotNull( exchange.getResponse().getStatusCode() );
        Assertions.assertEquals( 404, exchange.getResponse().getStatusCode().value() );

    }
}
