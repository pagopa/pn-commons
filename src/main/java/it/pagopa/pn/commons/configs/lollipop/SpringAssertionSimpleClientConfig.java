package it.pagopa.pn.commons.configs.lollipop;

import it.pagopa.tech.lollipop.consumer.assertion.client.simple.AssertionSimpleClientConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationProperties(
        prefix = "lollipop.assertion.rest.config"
)
@ConfigurationPropertiesScan
public class SpringAssertionSimpleClientConfig extends AssertionSimpleClientConfig {}
