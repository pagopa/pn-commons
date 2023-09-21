package it.pagopa.pn.commons.configs.cache;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Callable;

import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

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
	public ValueWrapper get(Object key) {
		return toValueWrapper(this.cache.get(key));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(Object key, Callable<T> valueLoader) {
		try {
			if (this.cache.containsKey(key)){
				return (T) this.get(key);
			}
			T value = valueLoader.call();
			this.put(key, value);
			return value;
		}catch(Exception err){
			return null;
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
		Object existingElement = this.cache.get(key);
		if (existingElement == null) {
			this.put(key, value);
		}
		return toValueWrapper(existingElement);
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
