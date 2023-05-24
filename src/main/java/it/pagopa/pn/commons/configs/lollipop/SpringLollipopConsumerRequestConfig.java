/* (C)2023 */
package it.pagopa.pn.commons.configs.lollipop;

import it.pagopa.tech.lollipop.consumer.config.LollipopConsumerRequestConfig;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/** Spring instance of the {@link LollipopConsumerRequestConfig} */
@ConfigurationProperties(prefix = "lollipop.core.config")
@ConfigurationPropertiesScan
@NoArgsConstructor
@Data
public class SpringLollipopConsumerRequestConfig extends LollipopConsumerRequestConfig {}
