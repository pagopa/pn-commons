package it.pagopa.pn.commons.conf;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.ResourceUtils;

@Configuration
@PropertySource(ResourceUtils.CLASSPATH_URL_PREFIX + "application-shared.properties") //can be overridden by application.properties
public class SharedAutoConfiguration {
}