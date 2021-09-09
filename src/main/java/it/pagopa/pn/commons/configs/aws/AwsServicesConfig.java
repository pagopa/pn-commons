package it.pagopa.pn.commons.configs.aws;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import it.pagopa.pn.commons.abstractions.impl.AwsS3FileStorage;
import it.pagopa.pn.commons.configs.RuntimeMode;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@ConditionalOnProperty( name = "pn.middleware.init.aws", havingValue = "true")
public class AwsServicesConfig {
    
    @Bean
    public AwsS3FileStorage awsS3FileStorage(S3Client client, AwsConfigs props, RuntimeMode runtimeMode) {
		return new AwsS3FileStorage (client, props, runtimeMode);
    }

}
