package it.pagopa.pn.commons.configs.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
@Slf4j
@AllArgsConstructor
@Configuration
@ConditionalOnProperty( name = "pn.cache.enabled", havingValue = "true")
public class PnCacheConfiguration {


	
	private final CacheConfigs cacheProperties;


	@Bean
	public PnCacheManager cacheManager() {
		String regEx = "([a-zA-Z]+)(\\()(\\d+)";
		Pattern pattern = Pattern.compile(regEx);
		PnCacheManager cacheManager = new PnCacheManager() ;
		List<String> cacheNames = this.cacheProperties.getCacheNames();
		Map<String, Integer> config = new HashMap<>();
		cacheNames.stream().forEach(name -> {
			Matcher matcher = pattern.matcher(name);
			int size = matcher.find() ? Integer.valueOf(matcher.group(3)) : 0;
			config.put(matcher.group(1), size);
		});
		
		if (!config.isEmpty()) {
			cacheManager.initialize(config);
		}

		return cacheManager;
	}

}
