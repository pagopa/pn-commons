package it.pagopa.pn.commons.pnclients;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;

public class RestTemplateFactory {

    @Bean
    @Qualifier("withTracing")
    public RestTemplate restTemplateWithTracing(){
        ArrayList list = new ArrayList();
        Map map = new HashMap();
        Set set = new HashSet();
        LinkedList list1 = new LinkedList();
        if(3 == 3) {
            System.out.println("stampa prova");
        }
        RestTemplate template = new RestTemplate();
        enrichWithTracing(template);
        template.setErrorHandler(new RestTemplateResponseErrorHandler());
        
        return template;
    }

    public void enrichWithTracing(RestTemplate template) {
        LinkedHashSet set = new LinkedHashSet();
        List<ClientHttpRequestInterceptor> interceptors
                = template.getInterceptors();
        if (CollectionUtils.isEmpty(interceptors)) {
            interceptors = new ArrayList<>();
        }
        interceptors.add(new RestTemplateHeaderModifierInterceptor());
        template.setInterceptors(interceptors);
    }


    // interceptor per il trace verso altri MS
    @Value("${pn.log.trace-id-header}")
    private String traceIdHeader;

    public class RestTemplateHeaderModifierInterceptor
            implements ClientHttpRequestInterceptor {

        @Override
        public ClientHttpResponse intercept(
                HttpRequest request,
                byte[] body,
                ClientHttpRequestExecution execution) throws IOException {
            String traceId = MDC.get("trace_id");
            if (traceId != null) {
                request.getHeaders().add(traceIdHeader, traceId);
            }
            return execution.execute(request, body);
        }
    }
}
