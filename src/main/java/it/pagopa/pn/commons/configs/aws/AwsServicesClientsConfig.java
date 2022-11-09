package it.pagopa.pn.commons.configs.aws;

import it.pagopa.pn.commons.configs.RuntimeMode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.awscore.client.builder.AwsClientBuilder;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.ssm.SsmClient;


import java.net.URI;

@Configuration
@ConditionalOnProperty( name = "pn.middleware.init.aws", havingValue = "true")
@Slf4j
public class AwsServicesClientsConfig {

    private final AwsConfigs props;

    public AwsServicesClientsConfig(AwsConfigs props, RuntimeMode runtimeMode) {
        this.props = props;
        log.info("AWS RuntimeMode is={}", runtimeMode);
    }

    @Bean
    public DynamoDbAsyncClient dynamoDbAsyncClient() {
        return this.configureBuilder( DynamoDbAsyncClient.builder() );
    }

    @Bean
    public DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient( DynamoDbAsyncClient baseAsyncClient) {
        return DynamoDbEnhancedAsyncClient.builder()
                .dynamoDbClient( baseAsyncClient )
                .build();
    }

    @Bean
    public DynamoDbClient dynamoDbClient() {
        return configureBuilder( DynamoDbClient.builder() );
    }

    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient( DynamoDbClient baseClient ) {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient( baseClient )
                .build();
    }
    
    @Bean
    public SqsClient sqsClient() {
        return configureBuilder( SqsClient.builder() );
    }

    @Bean
    public SsmClient ssmClient() { return configureBuilder( SsmClient.builder() ); }


    private <C> C configureBuilder(AwsClientBuilder<?, C> builder) {
        if( props != null ) {

            String profileName = props.getProfileName();
            if( StringUtils.isNotBlank( profileName ) ) {
                builder.credentialsProvider( ProfileCredentialsProvider.create( profileName ));
            }

            String regionCode = props.getRegionCode();
            if( StringUtils.isNotBlank( regionCode )) {
                builder.region( Region.of( regionCode ));
            }

            String endpointUrl = props.getEndpointUrl();
            if( StringUtils.isNotBlank( endpointUrl )) {
                builder.endpointOverride( URI.create( endpointUrl ));
            }

        }

        return builder.build();
    }

}
