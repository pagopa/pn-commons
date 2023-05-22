package it.pagopa.pn.commons.utils;

import lombok.CustomLog;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.adapter.DefaultServerWebExchange;
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

    private static final String sensitiveData = "<Hidden Data>";

    @Pointcut("execution(* it.pagopa.pn..generated.openapi.server..api.*.*(..))")
    public void server() {
        // all rest controller method
    }

    @Around(value = "server()")
    public Object logAroundServer(ProceedingJoinPoint joinPoint) throws Throwable {
        String process = joinPoint.getSignature().getName();
        String endingMessage = "Successful API operation: {}(). Result: {} ";
        log.logStartingProcess(process);
        Method m = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Object[] arguments = Arrays.stream(joinPoint.getArgs()).toArray();
        String url = ((DefaultServerWebExchange) arguments[arguments.length-1]).getRequest().getPath().toString();

        //RETRIEVE ARGUMENTS TO PRINT
        List<Annotation[]> annotationsParams = Arrays.stream(m.getParameterAnnotations()).toList();
        ArrayList<Object> argsDefined = getEvaluableArguments(annotationsParams, arguments);
        log.debug("Invoked operationId {} with uri {} with args: {}", joinPoint.getSignature().getName(), url, argsDefined);

        for (Object v : arguments) {
            if (v instanceof Mono<?> mono) {
                Mono<?> tempMono = mono.map(value ->{
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

    private void logResult(JoinPoint joinPoint, Object result, String message) {
        //Case: Mono
        if (result instanceof Mono<?> monoResult) {
            monoResult.doOnNext(o -> log.info(message,
                    joinPoint.getSignature().toShortString(),
                    o instanceof String ? sensitiveData : result)).subscribe();
            //Case: Flux
        } else if (result instanceof Flux<?> fluxResult) {
            fluxResult.doOnNext(o -> log.info(message,
                    joinPoint.getSignature().toShortString(),
                    o instanceof String ? sensitiveData : result)).subscribe();
            //Case: Other
        } else {
            log.info(message, joinPoint.getSignature().toShortString(), result instanceof String ? sensitiveData : result);
        }
    }

    private ArrayList<Object> getEvaluableArguments(List<Annotation[]> parametersAnnotation, Object[] parameterValue){
        ArrayList<Object> result = new ArrayList<>();
        for(int i = 0; i < parametersAnnotation.size(); i++){
            final int index = i;
            List<Annotation> ann = Arrays.stream(parametersAnnotation.get(i)).toList();
            if (!ann.isEmpty()) {
                for (Annotation value : ann) {
                    if (!(value instanceof RequestHeader || value instanceof RequestParam)) {
                        result.add(parameterValue[index]);
                        break;
                    }
                }
            }
            else
            if(!(parameterValue[index] instanceof ServerWebExchange))
                result.add(parameterValue[index]);
        }
        return result;
    }

    private Object proceed(ProceedingJoinPoint joinPoint, Object result, String endingMessage, String process) throws Throwable {
        if (result instanceof Mono<?> monoResult) {
            return monoResult.doOnSuccess(o -> {
                        logResult(joinPoint, o, endingMessage);
                        log.logEndingProcess(process);
                    })
                    .doOnError(o->{
                        log.warn("Warning: {} on mono", o.getMessage());
                    });
        }
        else if (result instanceof Flux<?> fluxResult) {
            return fluxResult.doOnNext(o -> {
                logResult(joinPoint, o, endingMessage);
                log.logEndingProcess(process);
            }).doOnError(o->{
                log.warn("Warning: {} on flux", o.getMessage());
            });
        }
        else {
            logResult(joinPoint, result, endingMessage);
            log.logEndingProcess(process);
            return result;
        }
    }
}

