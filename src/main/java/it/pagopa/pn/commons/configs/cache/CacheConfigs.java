package it.pagopa.pn.commons.configs.cache;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("pn.cache")
public class CacheConfigs {

    private List<String> cacheNames;
}
