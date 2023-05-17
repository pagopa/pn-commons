package it.pagopa.pn.commons.utils;

import lombok.CustomLog;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;


@Aspect
@CustomLog
public class ServiceAspectLogging {
    @Pointcut("@within(org.springframework.stereotype.Service)")
    public void service() {
        // all services
    }


    @Around(value = "service()")
    public Object logAroundService(ProceedingJoinPoint joinPoint) throws Throwable {
        log.debug("Invoked service operationId {} with args: {}", joinPoint.getSignature().getName(), joinPoint.getArgs());
        String endingMessage = "Successful service operation: {}() = {} ";
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
                        log.warn("ERRORE ON MONO");
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
}

