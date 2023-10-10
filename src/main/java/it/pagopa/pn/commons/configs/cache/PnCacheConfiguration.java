package it.pagopa.pn.commons.configs.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Slf4j
@Configuration
@ConditionalOnProperty( name = "pn.cache.enabled", havingValue = "true")
public class PnCacheConfiguration {

	@Value("${pn.cache.cacheNames:}")
	private List<String> cacheNames;

	@java.lang.SuppressWarnings("java:S5852")
	@Bean(name = "pnCacheManager")
	public PnCacheManager cacheManager() {
		log.info("cacheNames: [{}]", cacheNames);
		String regEx = "([a-zA-Z]+)(\\()(\\d+)";
		Pattern pattern = Pattern.compile(regEx);
		PnCacheManager cacheManager = new PnCacheManager() ;
		Map<String, Integer> config = new HashMap<>();
		cacheNames.forEach(name -> {
			Matcher matcher = pattern.matcher(name);
			int size = matcher.find() ? Integer.parseInt(matcher.group(3)) : 0;
			config.put(matcher.group(1), size);
		});
		
		if (!config.isEmpty()) {
			cacheManager.initialize(config);
		}

		return cacheManager;
	}

}
