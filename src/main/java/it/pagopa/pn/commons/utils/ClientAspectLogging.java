package it.pagopa.pn.commons.utils;

import lombok.CustomLog;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


@Aspect
@Component
@CustomLog
public class ClientAspectLogging {

    private static final String SENSITIVE_DATA = "<Hidden Data>";

    @Pointcut("execution(* it.pagopa.pn..generated.openapi.msclient..api.*.*(..))")
    public void clientDownStream() {
        // all client methods
    }


    @Around(value = "clientDownStream()")
    public Object logAroundDownstreamClient(ProceedingJoinPoint joinPoint) throws Throwable {
        String downstream = "<unknown>";
        String downstreamRegex = "downstream\\.([^.]+)\\.";
        if(joinPoint.toLongString().contains("downstream")){
            Pattern pattern = Pattern.compile(downstreamRegex);
            Matcher matcher = pattern.matcher(joinPoint.toLongString());
            if (matcher.find()) {
                downstream = matcher.group(1);
                log.debug("[DOWNSTREAM] Sono il downstream {}", downstream);
            }
        }
        Object[] arguments = joinPoint.getArgs();
        long startTime = Instant.now().toEpochMilli();
        String templateMessage = "[DOWNSTREAM {}]. Result {} - Execution time: {} - Start: {} - Stop: {}";
        var result = joinPoint.proceed();
        return this.proceedWithTimeCompute(joinPoint, result, templateMessage, downstream, startTime);



    }

   // @Around(value = "client()")
    public Object logAroundClient(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] arguments = joinPoint.getArgs();
        ArrayList<Object> argsDefined = getEvaluableArguments(arguments);
        log.debug("Client method {} with args: {}", joinPoint.getSignature().toShortString(), argsDefined);
        String endingMessage = "Return client method: {} Result: {} ";
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

    private Object proceedWithTimeCompute(ProceedingJoinPoint joinPoint, Object result, String templateMessage, String downstream, long startTime) {
        if (result instanceof Mono<?> monoResult) {
            return monoResult.doOnSuccess(res -> {
                        long endTime = Instant.now().toEpochMilli();
                        log.debug(templateMessage, downstream, endTime - startTime, startTime, endTime);
                      //  logDebugMessage(joinPoint, res, templateMessage);
                    }
            );
        }
        else if (result instanceof Flux<?> fluxResult) {
            return fluxResult.doOnNext(res -> {
                        long endTime = Instant.now().toEpochMilli();
                        log.debug(templateMessage, downstream, endTime - startTime, startTime, endTime);


                //logDebugMessage(joinPoint, res, templateMessage)
                    }
            );
        }
        else {
            logDebugMessage(joinPoint, result, templateMessage);
            return result;
        }
    }

}

