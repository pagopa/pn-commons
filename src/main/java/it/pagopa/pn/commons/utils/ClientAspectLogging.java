package it.pagopa.pn.commons.utils;

import lombok.CustomLog;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;


@Aspect
@CustomLog
public class ClientAspectLogging {
    @Pointcut("execution(* it.pagopa.pn.*.middleware.msclient..*.*(..)) || execution(* it.pagopa.pn.*.*.middleware.msclient..*.*(..))")
    public void client() {
        // all client methods
    }

    @Around(value = "client()")
    public Object logAroundClient(ProceedingJoinPoint joinPoint) throws Throwable {
        log.debug("Client method {} with args: {}", joinPoint.getSignature().toShortString(), joinPoint.getArgs());
        String endingMessage = "Return client method: {}() = {} ";
        var result = joinPoint.proceed();
        if (result instanceof Mono<?> monoResult) {
            return monoResult.doOnSuccess(o -> {
                        var response = "";
                        if (Objects.nonNull(o)) {
                            response = o.toString();
                        }
                        logResult(joinPoint, response, endingMessage);
                    })
                    .doOnError(o->{
                        log.warn("Warning: {} ON MONO", o.getMessage());
                    });
        }
        else if (result instanceof Flux<?> fluxResult) {
            return fluxResult.doOnNext(o -> {
                var response = "";
                if (Objects.nonNull(o)) {
                    response = o.toString();
                }
                logResult(joinPoint, response, endingMessage);
            }).doOnError(o->{
                log.warn("Warning: {} ON FLUX", o.getMessage());
            });
        }
        else {
            logResult(joinPoint, result, endingMessage);
            return result;
        }
    }

    private void logResult(JoinPoint joinPoint, Object result, String message) {
        //Case: Mono
        if (result instanceof Mono<?> monoResult) {
            monoResult.doOnNext(o -> log.info(message,
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    o,
                    result)).subscribe();
            //Case: Flux
        } else if (result instanceof Flux<?> fluxResult) {
            fluxResult.doOnNext(o -> log.info(message,
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    o,
                    result)).subscribe();
            //Case: Other
        } else {
            log.info(message, joinPoint.getSignature().toShortString(), result);
        }
    }


    Object maskIfsensitive(Object param){
        if(param instanceof String){
            return LogUtils.maskTaxId((String) param);
        }
        else {
            return "****";
        }
    }
}

