package it.pagopa.pn.commons.utils;

import it.pagopa.pn.commons.log.dto.metrics.Dimension;
import it.pagopa.pn.commons.log.dto.metrics.GeneralMetric;
import it.pagopa.pn.commons.log.dto.metrics.Metric;
import lombok.CustomLog;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Aspect
@Component
@CustomLog
public class ClientAspectLogging {

    private static final String SENSITIVE_DATA = "<Hidden Data>";
    public static final String STATISTICS = "-statistics";
    public static final String EXECUTE_TIME = "executeTime";
    public static final String DOWNSTREAM = "downstream";
    public static final String UNKNOWN = "<unknown>";
    public static final String APPLICATION_NAME_REGEX = "it\\.pagopa\\.pn\\.(.*)\\.generated";
    public static final String DOWNSTREAM_REGEX = "downstream\\.([^.]+)\\.";

    @Pointcut("execution(* it.pagopa.pn..generated.openapi.msclient..api.*.*(..))")
    public void client() {
        // all client methods
    }

    @Pointcut("execution(* it.pagopa.pn..generated.openapi.msclient.downstream..api.*.*(..))")
    public void clientDownStream() {
        // all client methods
    }

    @Around(value = "clientDownStream()")
    public Object logAroundDownstreamClient(ProceedingJoinPoint joinPoint) throws Throwable {

        String downstreamName = getDownstreamName(joinPoint);
        String applicationName = getApplicationName(joinPoint);
        Object[] arguments = joinPoint.getArgs();
        ArrayList<Object> argsDefined = getEvaluableArguments(arguments);

        String message = String.format("[DOWNSTREAM %s]. Client method %s with args: %s", downstreamName, joinPoint.getSignature().toShortString(), argsDefined);

        long startTime = Instant.now().toEpochMilli();
        var result = joinPoint.proceed();
        return this.proceedWithTimeCompute(joinPoint, result, message, startTime, downstreamName, applicationName);

    }

    private String getApplicationName(ProceedingJoinPoint joinPoint) {
        String applicationName = UNKNOWN;
        if(joinPoint.toLongString().contains("it.pagopa.pn") && joinPoint.toLongString().contains("generated")){
            Pattern pattern = Pattern.compile(APPLICATION_NAME_REGEX);
            Matcher matcher = pattern.matcher(joinPoint.toLongString());
            if (matcher.find()) {
                applicationName = matcher.group(1);
            }
        }
        return applicationName.replace(".", "-");
    }

    private String getDownstreamName(ProceedingJoinPoint joinPoint) {
        String downstreamName = UNKNOWN;
        if(joinPoint.toLongString().contains(DOWNSTREAM)){
            Pattern pattern = Pattern.compile(DOWNSTREAM_REGEX);
            Matcher matcher = pattern.matcher(joinPoint.toLongString());
            if (matcher.find()) {
                downstreamName = matcher.group(1);
            }
        }
        return downstreamName;
    }

    @Around(value = "client()")
    public Object logAroundClient(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] arguments = joinPoint.getArgs();
        ArrayList<Object> argsDefined = getEvaluableArguments(arguments);
        log.debug("Client method {} with args: {}", joinPoint.getSignature().toShortString(), argsDefined);
        String endingMessage = "Return client method: {}() Result: {} ";
        var result = joinPoint.proceed();
        return this.proceed(joinPoint, result, endingMessage);
    }

    private void logDebugMessage(JoinPoint joinPoint, Object result, String message) {
        //Case: Mono
        if (result instanceof Mono<?> monoResult) {
            monoResult.doOnNext(o -> log.debug(message,
                    joinPoint.getSignature().toShortString(),
                    o instanceof String ? SENSITIVE_DATA : o)).subscribe();
            //Case: Flux
        } else if (result instanceof Flux<?> fluxResult) {
            fluxResult.doOnNext(o -> log.debug(message,
                    joinPoint.getSignature().toShortString(),
                    o instanceof String ? SENSITIVE_DATA : o)).subscribe();
            //Case: Other
        } else {
            log.debug(message, joinPoint.getSignature().toShortString(), result instanceof String ? SENSITIVE_DATA : result);
        }
    }

    private void logMetricMessage(Object result, String message, List<GeneralMetric> metrics) {
        //Case: Mono
        if (result instanceof Mono<?> monoResult) {
            monoResult.doOnNext(o -> log.logMetric(metrics, message)).subscribe();
            //Case: Flux
        } else if (result instanceof Flux<?> fluxResult) {
            fluxResult.doOnNext(o -> log.logMetric(metrics, message)).subscribe();
            //Case: Other
        } else {
            log.logMetric(metrics, message);
        }
    }


    private ArrayList<Object> getEvaluableArguments(Object[] parameterValue){
        ArrayList<Object> result = new ArrayList<>();
        for(Object param: parameterValue){
            if (!(param instanceof String || param instanceof List<?>)) {
                result.add(param);
            }
            else {
                result.add(param.getClass());
            }
        }
        return result;
    }

    private Object proceed(ProceedingJoinPoint joinPoint, Object result, String endingMessage) {
        if (result instanceof Mono<?> monoResult) {
            return monoResult.doOnSuccess(res ->
                        logDebugMessage(joinPoint, res, endingMessage)
                    );
        }
        else if (result instanceof Flux<?> fluxResult) {
            return fluxResult.doOnNext(res ->
                logDebugMessage(joinPoint, res, endingMessage)
            );
        }
        else {
            logDebugMessage(joinPoint, result, endingMessage);
            return result;
        }
    }

    private Object proceedWithTimeCompute(ProceedingJoinPoint joinPoint, Object result, String message, long startTime, String downstreamName, String applicationName) {
        if (result instanceof Mono<?> monoResult) {
            return monoResult.doOnSuccess(res -> {
                long endTime = Instant.now().toEpochMilli();
                GeneralMetric metric = createGeneralMetric(startTime, endTime, downstreamName, applicationName);
                logMetricMessage(joinPoint, message, List.of(metric));
            });
        }
        else if (result instanceof Flux<?> fluxResult) {
            return fluxResult.doOnNext(res -> {
                    long endTime = Instant.now().toEpochMilli();
                GeneralMetric metric = createGeneralMetric(startTime, endTime, downstreamName, applicationName);

                logMetricMessage(joinPoint, message, List.of(metric));
                }
            );
        }
        else {
            long endTime = Instant.now().toEpochMilli();
            GeneralMetric metric = createGeneralMetric(startTime, endTime, downstreamName, applicationName);

            logMetricMessage(joinPoint, message, List.of(metric));
            return result;
        }
    }

    private GeneralMetric createGeneralMetric(long startTime, long endTime, String downstream, String applicationName) {
        GeneralMetric metric = new GeneralMetric();
        metric.setTimestamp(Instant.now().toEpochMilli());
        metric.setDimensions(List.of(new Dimension(DOWNSTREAM,downstream)));
        metric.setMetrics(List.of(new Metric(EXECUTE_TIME, endTime - startTime)));
        metric.setNamespace(applicationName + STATISTICS);
        return metric;
    }

}

