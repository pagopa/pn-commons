package it.pagopa.pn.commons.configs;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@SpringBootApplication(scanBasePackages = {"it.pagopa.pn"})
@EntityScan( basePackages = {"it.pagopa.pn"})
@Import({PnAutoConfigurationSelector.class})
public @interface PnSpringBootApplication {
}
