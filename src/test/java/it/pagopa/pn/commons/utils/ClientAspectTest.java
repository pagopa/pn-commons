package it.pagopa.pn.commons.utils;


import it.pagopa.pn.national.registries.generated.openapi.msclient.downstream.inad.v1.api.TestApi;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
        public TestApi initTestApiBean(){
            return new TestApi();
        }
    }

    @Autowired
    private TestApi t;

    @Test
    void testSendMetricToCloudWatch() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(baos));
        t.testCall().subscribe(System.out::println);
        System.setOut(originalSystemOut);

        String logOutput = baos.toString();
        System.out.println(logOutput);
        Assertions.assertTrue(logOutput.contains("[DOWNSTREAM inad]. Client method TestApi.testCall() with args: []"));
    }
}
