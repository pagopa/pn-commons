package it.pagopa.pn.commons.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class RuntimeModeHolder {

    public static final String DEVELOPMENT_MODE_PROPERTY = "pn.env.runtime";

    @Value( "${" + DEVELOPMENT_MODE_PROPERTY + "}")
    private String runtimeModeName;

    public static boolean isDevelopment(Environment env) {
        String runtimeModeString = env.getProperty( DEVELOPMENT_MODE_PROPERTY );
        RuntimeMode actual = Enum.valueOf( RuntimeMode.class, runtimeModeString );
        return RuntimeMode.DEVELOPMENT.equals( actual );
    }

    @Bean
    public RuntimeMode runtimeMode() {
        return Enum.valueOf( RuntimeMode.class, runtimeModeName );
    }

}
