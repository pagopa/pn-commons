package it.pagopa.pn.commons.configs.aws;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("aws")
public class AwsConfigs {

    private String profileName;
    private String regionCode;
    private String bucketName;
    private String endpointUrl;
    private Boolean useAwsKeyspace;

    private String accessKeyId;
    private String secretAccessKey;
}
