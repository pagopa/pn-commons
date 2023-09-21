package it.pagopa.pn.commons.configs.cache;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

@Slf4j
public class PnCacheManager implements CacheManager{
	private static final int DEFAULT_SIZE = 100;

	private final ConcurrentMap<String, Cache> cacheMap = new ConcurrentHashMap<String, Cache>(16);
	private boolean dynamic;
	
	public PnCacheManager() {
	}

	public void initialize(Map<String, Integer> values) {
		log.info("Initializing PnCacheManager with cache names: {}", values.keySet());
		if (values != null) {
			for (String name : values.keySet()) {
				int size = values.get(name) > 0 ? values.get(name) : DEFAULT_SIZE;
				this.cacheMap.put(name, createCache(name, size));
			}
			this.dynamic = false;
		}
		else {
			this.dynamic = true;
		}
	}
	
	@Override
	public Collection<String> getCacheNames() {
		return cacheMap.keySet();
	}
	
	@Override
	public Cache getCache(String name) {
		Cache cache =  cacheMap.get(name);
		if (cache == null && this.dynamic) {
			cache = createCache(name);
			cacheMap.put(name, cache);
		}
		return cache;
	}

	private Cache createCache(String name) {
		return new PnLRUCache(name, DEFAULT_SIZE);
	}
	private Cache createCache(String name, Integer size) {
		return new PnLRUCache(name, size);
	}
}
