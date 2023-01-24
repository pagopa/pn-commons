package it.pagopa.pn.commons.pnclients;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.util.retry.Retry;

import java.net.ConnectException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class CommonBaseClient {

    protected CommonBaseClient(){
    }

    private String traceIdHeader;

    private int retryMaxAttempts;

    private int connectionTimeoutMillis;


    protected WebClient.Builder enrichBuilder(WebClient.Builder builder){
        return enrichBuilderWithTraceId(builder);
    }

    private WebClient.Builder enrichBuilderWithTraceId(WebClient.Builder builder){
        return builder
                .filters(filterList -> filterList.add(ExchangeFilterFunction.ofRequestProcessor(this::traceIdFilter)));
    }

    private Mono<ClientRequest> traceIdFilter(ClientRequest request) {
        String traceId = MDC.get("trace_id");
        if (StringUtils.hasText(traceIdHeader) && StringUtils.hasText(traceId))
        {
            return Mono.just(ClientRequest.from(request)
                    .header(traceIdHeader, traceId)
                    .build());
        }
        else
        {
            log.warn("missing trace_id");
            return Mono.just(request);
        }

    }


    protected String elabExceptionMessage(Throwable x)
    {
        try {
            String message = x.getMessage()==null?"":x.getMessage();
            if (x instanceof WebClientResponseException webClientResponseException)
            {
                message += ";" + webClientResponseException.getResponseBodyAsString();
            }
            return  message;
        } catch (Exception e) {
            log.error("exception reading body", e);
            return x.getMessage();
        }
    }

    public WebClient initWebClient(WebClient.Builder builder){

        HttpClient httpClient = HttpClient.create().option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeoutMillis)
                .doOnConnected(connection -> connection.addHandlerLast(new ReadTimeoutHandler(10000, TimeUnit.MILLISECONDS)));

        return this.enrichBuilder(builder)
                .filter(buildRetryExchangeFilterFunction())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    private ExchangeFilterFunction buildRetryExchangeFilterFunction() {
        return (request, next) -> next.exchange(request)
                .flatMap(clientResponse -> Mono.just(clientResponse)
                .filter(response -> clientResponse.statusCode().isError())
                .flatMap(response -> clientResponse.createException())
                .flatMap(Mono::error)
                .thenReturn(clientResponse))
                .retryWhen( Retry.backoff(retryMaxAttempts, Duration.ofMillis(25))
                        .filter(throwable -> throwable instanceof TimeoutException ||
                                throwable instanceof ConnectException ||
                                throwable instanceof WebClientResponseException.TooManyRequests));
    }

    @Autowired
    public void setTraceIdHeader(@Value("${pn.log.trace-id-header}") String traceIdHeader) {
        this.traceIdHeader = traceIdHeader;
    }

    @Autowired
    public void setRetryMaxAttempts(@Value("${pn.commons.retry.max-attempts}") int retryMaxAttempts) {
        this.retryMaxAttempts = retryMaxAttempts;
    }

    @Autowired
    public void setConnectionTimeoutMillis(@Value("${pn.commons.connection-timeout-millis}") int connectionTimeoutMillis) {
        this.connectionTimeoutMillis = connectionTimeoutMillis;
    }


}
