package it.pagopa.pn.commons.utils;

import lombok.CustomLog;
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

    private static final String SENSITIVE_DATA = "<Hidden Data>";

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
        log.debug("Invoked operationId {} with path {} with args: {}", joinPoint.getSignature().toShortString(), url, argsDefined);

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

    private ArrayList<Object> getEvaluableArguments(List<Annotation[]> parametersAnnotation, Object[] parameterValue){
        ArrayList<Object> result = new ArrayList<>();
        for(int i = 0; i < parametersAnnotation.size(); i++){
            List<Annotation> ann = Arrays.stream(parametersAnnotation.get(i)).toList();
            if (!ann.isEmpty()) {
                for (Annotation value : ann) {
                    if (!(value instanceof RequestHeader || value instanceof RequestParam)) {
                        result.add(parameterValue[i]);
                        break;
                    }
                }
            }
            else if(!(parameterValue[i] instanceof ServerWebExchange))
                result.add(parameterValue[i]);
        }
        return result;
    }

    private Object proceed(ProceedingJoinPoint joinPoint, Object result, String endingMessage, String process) throws Throwable {
        if (result instanceof Mono<?> monoResult) {
            return monoResult.doOnSuccess(o -> {
                        log.info(endingMessage, joinPoint.getSignature().toShortString(), o);
                        log.logEndingProcess(process);
                    })
                    .doOnError(o->
                            //TODO add customlog ending process instead warning below
                        log.warn("Warning: {} on mono", o.getMessage())
                    );
        }
        else if (result instanceof Flux<?> fluxResult) {
            return fluxResult.doOnNext(o -> {
                log.info(endingMessage, joinPoint.getSignature().toShortString(), o);
                log.logEndingProcess(process);
            }).doOnError(o->
                    //TODO add customlog ending process instead warning below
                log.warn("Warning: {} on flux", o.getMessage())
            );
        }
        else {
            log.info(endingMessage, joinPoint.getSignature().toShortString(), result);
            log.logEndingProcess(process);
            return result;
        }
    }
}

