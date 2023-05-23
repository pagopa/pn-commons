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
import reactor.netty.resources.ConnectionProvider;
import reactor.util.retry.Retry;

import javax.net.ssl.SSLHandshakeException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Classe utilizzare per creare un @{@link WebClient}
 * I client potranno estendere la classe per arricchirla (tramite @{@link WebClient.Builder}.
 * Esempio di estensione semplice (in questo caso il client può utilizzare il metodo super.initWebClient):
 * <p>
 * public class SimpleClient extends CommonBaseClient {
 *     public void init(){
 *         ApiClient newApiClient = new ApiClient(super.initWebClient(ApiClient.buildWebClientBuilder()));
 *         newApiClient.setBasePath(pnMandateConfig.getClientDatavaultBasepath());
 *     }
 * <p>
 * Esempio di estensione sovrascrivendo il metodo initWebClient, per aggiungere altri parametri:
 * <p>
 * protected WebClient initWebClient(WebClient.Builder builder, String apiKey){
 * <p>
 *         return super.enrichBuilder(builder)
 *                 .defaultHeader(HEADER_API_KEY, apiKey)
 *                 .build();
 *     }
 */
@Slf4j
public abstract class CommonBaseClient {

    private String traceIdHeader;

    private int retryMaxAttempts;

    private int connectionTimeoutMillis;

    protected CommonBaseClient() {}


    public WebClient initWebClient(WebClient.Builder builder) {
        return enrichBuilder(builder).build();
    }
    protected WebClient.Builder enrichBuilder(WebClient.Builder builder){
        WebClient.Builder builderEnriched = enrichBuilderWithTraceId(builder);
        return enrichWithDefaultProps(builderEnriched);
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


    public static String elabExceptionMessage(Throwable x)
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

    protected WebClient.Builder enrichWithDefaultProps(WebClient.Builder builder){
        HttpClient httpClient = buildHttpClient();

        return builder
                .filter(buildRetryExchangeFilterFunction())
                .clientConnector(new ReactorClientHttpConnector(httpClient));
    }

    protected HttpClient buildHttpClient() {
        ConnectionProvider provider = ConnectionProvider.builder("fixed")
        .maxConnections(500)
        .maxIdleTime(Duration.ofSeconds(20))
        .maxLifeTime(Duration.ofSeconds(60))
        .pendingAcquireTimeout(Duration.ofSeconds(60))
        .evictInBackground(Duration.ofSeconds(120)).build();

        return HttpClient.create(provider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeoutMillis)
                .doOnConnected(connection -> connection.addHandlerLast(new ReadTimeoutHandler(connectionTimeoutMillis, TimeUnit.MILLISECONDS)));
    }

    protected ExchangeFilterFunction buildRetryExchangeFilterFunction() {
        return (request, next) -> next.exchange(request)
                .flatMap(clientResponse -> Mono.just(clientResponse)
                .filter(response -> clientResponse.statusCode().isError())
                .flatMap(response -> clientResponse.createException())
                .flatMap(Mono::error)
                .thenReturn(clientResponse))
                .retryWhen( Retry.backoff(retryMaxAttempts, Duration.ofMillis(25)).jitter(0.75)
                        .filter(this::isRetryableException)
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                            Throwable lastExceptionInRetry = retrySignal.failure();
                            log.warn("Retries exhausted {}, with last Exception: {}", retrySignal.totalRetries(), lastExceptionInRetry.getMessage());
                            return lastExceptionInRetry;
                        })
                );
    }

    private boolean isRetryableException(Throwable throwable) {
        return throwable instanceof TimeoutException ||
                throwable instanceof ConnectException ||
                throwable instanceof SSLHandshakeException ||
                throwable instanceof UnknownHostException ||
                throwable instanceof WebClientResponseException.TooManyRequests ||
                throwable instanceof WebClientResponseException.GatewayTimeout ||
                throwable instanceof WebClientResponseException.BadGateway ||
                throwable instanceof WebClientResponseException.ServiceUnavailable
                ;
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
