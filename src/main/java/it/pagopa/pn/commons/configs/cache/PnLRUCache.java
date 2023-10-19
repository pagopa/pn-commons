package it.pagopa.pn.commons.configs.cache;

import it.pagopa.pn.commons.exceptions.PnExceptionsCodes;
import it.pagopa.pn.commons.exceptions.PnInternalException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Callable;

@Slf4j
public class PnLRUCache<K,V> implements Cache{
	
	private final Map<K,V> cache;
	private final String name;

	public PnLRUCache(String name, int maxEntries) {
		this.cache = Collections.synchronizedMap(new LimitedSizeLinkedHashMap<K,V>(maxEntries));
		this.name = name;
	}

	@Override
	public final String getName() {
		return this.name;
	}

	@Override
	public final Map<K,V> getNativeCache() {
		return this.cache;
	}

	@Override
	public ValueWrapper get(@NotNull Object key) {
		return toValueWrapper(this.cache.get(key));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(@NotNull Object key, Callable<T> valueLoader) {
		try {
			synchronized (this.cache) {
				if (this.cache.containsKey(key)) {
					return (T) this.get(key).get();
				}
			}
			T value = valueLoader.call();
			this.put(key, value);
			return value;
		}catch(Exception err){
			log.error ("error getting object with callable valueLoader", err);
			throw new PnInternalException("error getting object with callable valueLoader", PnExceptionsCodes.ERROR_CODE_PN_GENERIC_ERROR, err);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T get(Object key, Class<T> type) {
		Object value = this.cache.get(key);
		if (value != null && type != null && !type.isInstance(value)) {
			throw new IllegalStateException("Cached value is not of required type [" + type.getName() + "]: " + value);
		}
		return (T) value;
	}

	@Override
	public void put(Object key, Object value) {
		this.cache.put((K) key, (V)value);
	}

	@Override
	public ValueWrapper putIfAbsent(Object key, Object value) {
		synchronized (this.cache) {
			Object existingElement = this.cache.get(key);
			if (existingElement == null) {
				this.put(key, value);
			}
			return toValueWrapper(existingElement);
		}
	}

	@Override
	public void evict(Object key) {
		this.cache.remove(key);
	}

	@Override
	public void clear() {
		this.cache.clear();
	}

	private ValueWrapper toValueWrapper(Object element) {
		return (element != null ? new SimpleValueWrapper(element) : null);
	}

}
