package it.pagopa.pn.commons.utils;


import it.pagopa.pn.commons.log.MDCWebFilter;
import it.pagopa.pn.test.generated.openapi.msclient.test.api.TestApi;
import lombok.CustomLog;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;



@ComponentScan
@EnableAspectJAutoProxy
@SpringBootTest(classes = {ClientAspectTest.SpringTestConfiguration.class})
class ClientAspectTest {


    @Configuration
    public static class SpringTestConfiguration {
        @Bean
        public TestApi mdcTraceIdWebFilter(){
            return new TestApi();
        }
    }

    @Autowired
    private TestApi t;

    @Test
    public void testSendMetricToCloudWatch() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(baos));
        t.testCall();
        System.setOut(originalSystemOut);

        String logOutput = baos.toString();
        System.out.println("YYYY" + logOutput + "XXXX");
        Assertions.assertTrue(logOutput.contains("Execution time:"));
    }
}
