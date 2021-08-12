package it.pagopa.pn.commons.configs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigurationImportSelector;
import org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import java.util.*;

@Slf4j
public class PnAutoConfigurationSelector extends AutoConfigurationImportSelector {

    private static final String MIDDLEWARE_DEACTIVATION_PREFIX = "pn.middleware.init.";

    private static final Map<String, List<String>> EXCLUSIONS_MAP = Map.ofEntries(
            Map.entry( MIDDLEWARE_DEACTIVATION_PREFIX + "cassandra",
                Arrays.asList(
                    CassandraAutoConfiguration.class.getName()
                )),
            Map.entry( MIDDLEWARE_DEACTIVATION_PREFIX + "kafka",
                Collections.singletonList(
                    KafkaAutoConfiguration.class.getName()
                ))
        );

    private static final List<String> DEVELOPMENT_EXCLUSION = Collections.singletonList(
            CassandraAutoConfiguration.class.getName()
        );

    private static final List<String> NOT_DEVELOPMENT_EXCLUSION = Collections.emptyList();


    private final Environment env;

    public PnAutoConfigurationSelector(Environment env) {
        this.env = env;
    }

    @Override
    protected Set<String> getExclusions(AnnotationMetadata metadata, AnnotationAttributes attributes) {
        Set<String> exclusions = super.getExclusions(metadata, attributes);

        exclusions.addAll( computeMiddlewareExclusions() );

        if( isDevelopmentMode() ) {
            log.info(" Exclusions: in development mode" );
            exclusions.addAll( DEVELOPMENT_EXCLUSION );
        }
        else {
            log.info(" Exclusions: NOT development mode" );
            exclusions.addAll( NOT_DEVELOPMENT_EXCLUSION );
        }

        log.info(" Exclusions: {}", exclusions );
        return exclusions;
    }

    private Set<String> computeMiddlewareExclusions() {
        Set<String> exclusions = new HashSet<>();

        for( Map.Entry<String, List<String>> entry: EXCLUSIONS_MAP.entrySet() ) {
            String propertyValue = env.getProperty( entry.getKey() );
            if( isFalse( propertyValue ) ) {
                exclusions.addAll( entry.getValue() );
            }
        }
        return exclusions;
    }

    private boolean isDevelopmentMode() {
        return RuntimeModeHolder.isDevelopment( env );
    }

    private boolean isFalse(String propertyValue) {
        return ! "true".equalsIgnoreCase( propertyValue == null ? "": propertyValue.trim() );
    }

}
