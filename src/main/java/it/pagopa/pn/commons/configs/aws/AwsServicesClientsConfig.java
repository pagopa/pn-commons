package it.pagopa.pn.commons.configs.aws;

import it.pagopa.pn.commons.configs.RuntimeMode;
import it.pagopa.pn.commons.utils.dynamodb.async.DynamoDbAsyncClientDecorator;
import it.pagopa.pn.commons.utils.dynamodb.async.DynamoDbEnhancedAsyncClientDecorator;
import it.pagopa.pn.commons.utils.dynamodb.sync.DynamoDbEnhancedClientDecorator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.awscore.client.builder.AwsClientBuilder;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.eventbridge.EventBridgeAsyncClient;
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
    public DynamoDbAsyncClient dynamoDbAsyncClientWithMDC() {
        return new DynamoDbAsyncClientDecorator(this.dynamoDbAsyncClient());
    }

    @Bean
    public DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClientWithMDC(DynamoDbAsyncClient delegate) {
        return new DynamoDbEnhancedAsyncClientDecorator(this.dynamoDbEnhancedAsyncClient(delegate));
    }

    @Bean
    public DynamoDbClient dynamoDbClient() {
        return configureBuilder( DynamoDbClient.builder() );
    }

    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClientWithLog(DynamoDbClient delegate) {
        return new DynamoDbEnhancedClientDecorator(dynamoDbEnhancedClient(delegate));
    }

    @Bean
    public SqsClient sqsClient() {
        return configureBuilder( SqsClient.builder() );
    }

    @Bean
    public SsmClient ssmClient() { return configureBuilder( SsmClient.builder() ); }

    @Bean
    @Lazy
    public CloudWatchAsyncClient cloudWatchClient() {
        return configureBuilder(CloudWatchAsyncClient.builder());
    }

    @Bean
    public EventBridgeAsyncClient eventBridgeClient() {
        return configureBuilder(EventBridgeAsyncClient.builder());
    }

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

    private DynamoDbAsyncClient dynamoDbAsyncClient() {
        return this.configureBuilder( DynamoDbAsyncClient.builder() );
    }

    private DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient( DynamoDbAsyncClient baseAsyncClient) {
        return DynamoDbEnhancedAsyncClient.builder()
                .dynamoDbClient( baseAsyncClient )
                .build();
    }

    public DynamoDbEnhancedClient dynamoDbEnhancedClient( DynamoDbClient baseClient ) {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient( baseClient )
                .build();
    }

}
