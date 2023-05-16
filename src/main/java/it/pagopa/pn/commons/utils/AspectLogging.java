package it.pagopa.pn.commons.utils;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Aspect
@Slf4j
public class AspectLogging {
    @Pointcut("@within(org.springframework.validation.annotation.Validated)")
    public void validatedInterface() {
        // all rest controllers with @Validated annotation
    }

    @Pointcut("execution(* it.pagopa.pn.*.middleware.msclient..*.*(..)) || execution(* it.pagopa.pn.*.*.middleware.msclient..*.*(..))")
    public void client() {
        // all client methods
    }

    @Before(value = "validatedInterface()")
    public void logApiInvocation(JoinPoint joinPoint) {
        log.debug("Invoked operationId {} with args: {}", joinPoint.getSignature().getName(), joinPoint.getArgs());

    }

    @AfterReturning(value = "validatedInterface()", returning = "result")
    public void returnApiInvocation(JoinPoint joinPoint, Object result) {
        String message = "Successful API operation: {}() = {} ";
        logResult(joinPoint, result, message);
    }

    @Before(value = "client()")
    public void clientLog(JoinPoint joinPoint) {
        log.debug("Client method {} with args: {}", joinPoint.getSignature().toShortString(), joinPoint.getArgs());
    }

    @AfterReturning(value = "client()", returning = "result")
    public void returnClientLog(JoinPoint joinPoint, Object result) {
        String message = "Return client method: {}() = {} ";
        logResult(joinPoint, result, message);
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

