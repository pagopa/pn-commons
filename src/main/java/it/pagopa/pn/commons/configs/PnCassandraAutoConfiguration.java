package it.pagopa.pn.commons.configs;

import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import it.pagopa.pn.commons.configs.aws.AwsConfigs;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration;
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import software.aws.mcs.auth.SigV4AuthProvider;

import java.util.Optional;

@Configuration
@ConditionalOnProperty( name = "pn.middleware.init.cassandra", havingValue = "true")
@Slf4j
public class PnCassandraAutoConfiguration extends CassandraAutoConfiguration {

    private final RuntimeMode runtimeMode;
    private final AwsConfigs awsConfigs;
    private final Optional<SigV4AuthProvider> awsSigner;

    public PnCassandraAutoConfiguration(RuntimeMode runtimeMode, AwsConfigs awsConfigs, Optional<SigV4AuthProvider> awsSigner) {
        this.runtimeMode = runtimeMode;
        this.awsConfigs = awsConfigs;
        this.awsSigner = awsSigner;
    }

    @Bean
    @ConditionalOnMissingBean
    @Scope("prototype")
    public CqlSessionBuilder cassandraSessionBuilder(CassandraProperties properties,
                                                     DriverConfigLoader driverConfigLoader, ObjectProvider<CqlSessionBuilderCustomizer> builderCustomizers) {
        log.info("Custom cassandra autoconfigurator");
        
        if( isDevelopmentMode() && ! useAwsKeyspace() ) {
            
            String keyspaceName = properties.getKeyspaceName();
            if(StringUtils.isNotBlank( keyspaceName )) {
                log.info("Custom cassandra autoconfigurator create keyspace {}", keyspaceName );
                
                properties.setKeyspaceName( null );
                super.cassandraSessionBuilder( properties, driverConfigLoader, builderCustomizers)
                        .build()
                        .execute("CREATE KEYSPACE IF NOT EXISTS " + keyspaceName + " WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'}");
                properties.setKeyspaceName( keyspaceName );
            }
        }

        CqlSessionBuilder cqlSessionBuilder = super.cassandraSessionBuilder( properties, driverConfigLoader, builderCustomizers);
        if( useAwsKeyspace() ) {
            activateAwsToken( cqlSessionBuilder );
        }
        return cqlSessionBuilder;
    }

    private boolean isDevelopmentMode() {
        return RuntimeMode.DEVELOPMENT.equals(runtimeMode);
    }

    private boolean useAwsKeyspace() {
        return Boolean.TRUE.equals(awsConfigs.getUseAwsKeyspace());
    }

    private void activateAwsToken(CqlSessionBuilder cqlSessionBuilder) {
        String region = awsConfigs.getRegionCode();

        log.info("Custom cassandra autoconfigurator use aws keyspace in region {}", region );

        cqlSessionBuilder
                .withAuthProvider( awsSigner.get() )
                .withLocalDatacenter( region );
    }


}
