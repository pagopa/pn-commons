package it.pagopa.pn.commons.conf;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.ResourceUtils;

@SpringBootApplication
@PropertySource(ResourceUtils.CLASSPATH_URL_PREFIX + "application-shared.properties") //can be overridden by application.properties
public class SharedAutoConfiguration {
}