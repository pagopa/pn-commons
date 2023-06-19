package it.pagopa.pn.commons.utils;

import lombok.CustomLog;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Aspect
@CustomLog
public class ServerAspectLogging {

    private static final String SENSITIVE_DATA = "<Hidden Data>";

    @Pointcut("execution(* it.pagopa.pn..generated.openapi.server..api.*.*(..))")
    public void server() {
        // all rest controller method
    }

    @Around(value = "server()")
    public Object logAroundServer(ProceedingJoinPoint joinPoint) throws Throwable {
        String endingMessage = "Successful API operation: {}(). Result: {} ";
        String process = joinPoint.getSignature().getName();
        log.logStartingProcess(process);
        Method m = ((MethodSignature) joinPoint.getSignature()).getMethod();
        String url = m.getAnnotation(RequestMapping.class).value()[0];
        List<Annotation[]> annotationsParams = Arrays.stream(m.getParameterAnnotations()).toList();
        Object[] arguments = joinPoint.getArgs();
        logAPIInvoked(joinPoint.getSignature().toShortString(), url, annotationsParams, arguments);
        for (Object v : arguments) {
            if (v instanceof Mono<?> mono) {
                Mono<?> tempMono = mono.map(value -> {
                    log.debug("MonoLift value: {} ", value);
                    return value;
                });
                for (int i = 0; i < arguments.length; i++) {
                    if (arguments[i] instanceof Mono<?>) {
                        arguments[i] = tempMono;
                    }
                }
                //Proceed with new Mono in arguments
                var result = joinPoint.proceed(arguments);
                return this.proceed(joinPoint, result, endingMessage, process);
            }
        }
        //Proceed same arguments
        var result = joinPoint.proceed();
        return this.proceed(joinPoint, result, endingMessage, process);

    }

    private void logAPIInvoked(String operationId, String url, List<Annotation[]> parametersAnnotation, Object[] parameterValue) {
        ArrayList<Object> args = new ArrayList<>();
        for (int i = 0; i < parametersAnnotation.size(); i++) {
            List<Annotation> ann = Arrays.stream(parametersAnnotation.get(i)).toList();
            boolean toAdd = true;
            for (Annotation value : ann) {
                if (value instanceof RequestHeader || value instanceof RequestParam) {
                    toAdd = false;
                } else if (value instanceof PathVariable v) {
                    toAdd = false;
                    url = url.replace("{" + v.value() + "}", String.valueOf(parameterValue[i]));
                }
            }
            if (toAdd && !(parameterValue[i] instanceof ServerWebExchange)) {
                args.add(parameterValue[i]);
            }
        }
        log.debug("Invoked operationId {} with path {} with args: {}", operationId, url, args);
    }

    private void logDebugMessage(String endingMessage, String method, Object response) {
        if (response == null) {
            log.debug(endingMessage, method, "<Null>");
        } else if (response instanceof ResponseEntity<?> res) {
            log.debug(endingMessage, method, res.getBody() instanceof String ? SENSITIVE_DATA : res);
        } else {
            log.debug(endingMessage, method, "<unsupported return type>");
        }
    }

    private Object proceed(ProceedingJoinPoint joinPoint, Object result, String endingMessage, String process) throws Throwable {
        if (result instanceof Mono<?> monoResult) {
            return monoResult.doOnSuccess(o -> {
                        this.logDebugMessage(endingMessage, joinPoint.getSignature().toShortString(), o);
                        log.logEndingProcess(process);
                    })
                    .doOnError(o ->
                            log.logEndingProcess(process, false, o.getMessage())
                    );
        } else if (result instanceof Flux<?> fluxResult) {
            return fluxResult.doOnNext(o -> {
                this.logDebugMessage(endingMessage, joinPoint.getSignature().toShortString(), o);
                log.logEndingProcess(process);
            }).doOnError(o ->
                    log.logEndingProcess(process, false, o.getMessage())
            );
        } else {
            this.logDebugMessage(endingMessage, joinPoint.getSignature().toShortString(), result);
            log.logEndingProcess(process);
            return result;
        }
    }
}

