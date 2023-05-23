package it.pagopa.pn.commons.utils;

import lombok.CustomLog;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Aspect
@CustomLog
public class ClientAspectLogging {

    private static final String SENSITIVE_DATA = "<Hidden Data>";

    @Pointcut("execution(* it.pagopa.pn..generated.openapi.msclient..api.*.*(..))")
    public void client() {
        // all client methods
    }

    @Around(value = "client()")
    public Object logAroundClient(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] arguments = Arrays.stream(joinPoint.getArgs()).toArray();
        ArrayList<Object> argsDefined = getEvaluableArguments(arguments);
        log.debug("Client method {} with args: {}", joinPoint.getSignature().toShortString(), argsDefined);
        String endingMessage = "Return client method: {}() Result: {} ";
        var result = joinPoint.proceed();
        return this.proceed(joinPoint, result, endingMessage);
    }

    private void logResult(JoinPoint joinPoint, Object result, String message) {
        //Case: Mono
        if (result instanceof Mono<?> monoResult) {
            monoResult.doOnNext(o -> log.info(message,
                    joinPoint.getSignature().toShortString(),
                    o instanceof String ? SENSITIVE_DATA : o)).subscribe();
            //Case: Flux
        } else if (result instanceof Flux<?> fluxResult) {
            fluxResult.doOnNext(o -> log.info(message,
                    joinPoint.getSignature().toShortString(),
                    o instanceof String ? SENSITIVE_DATA : o)).subscribe();
            //Case: Other
        } else {
            log.info(message, joinPoint.getSignature().toShortString(), result instanceof String ? SENSITIVE_DATA : result);
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

    private Object proceed(ProceedingJoinPoint joinPoint, Object result, String endingMessage) throws Throwable {
        if (result instanceof Mono<?> monoResult) {
            return monoResult.doOnSuccess(res ->
                        logResult(joinPoint, res, endingMessage)
                    )
                    .doOnError(o->
                        log.warn("Warning: {} on mono", o.getMessage())
                    );
        }
        else if (result instanceof Flux<?> fluxResult) {
            return fluxResult.doOnNext(res ->
                logResult(joinPoint, res, endingMessage)
            ).doOnError(o->
                log.warn("Warning: {} on flux", o.getMessage())
            );
        }
        else {
            logResult(joinPoint, result, endingMessage);
            return result;
        }
    }
}

