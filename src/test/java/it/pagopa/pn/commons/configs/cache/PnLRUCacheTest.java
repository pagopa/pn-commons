package it.pagopa.pn.commons.configs.cache;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache.ValueWrapper;

class PnLRUCacheTest {

    PnLRUCache<String,String> pnLRUCache;
    @BeforeEach
    void setUp() {
        pnLRUCache = new PnLRUCache<>("test",10);
    }

    @Test
    void getName() {
        assertEquals("test", pnLRUCache.getName());
    }

    @Test
    void getNativeCache() {
        assertNotNull(pnLRUCache.getNativeCache());
    }

    @Test
    void get() {
        pnLRUCache.put("chiave","valore");
        ValueWrapper valueWrapper = pnLRUCache.get("chiave");
        assertNotNull(valueWrapper);
        assertEquals("valore", valueWrapper.get());
    }

    @Test
    void put() {
        pnLRUCache.put("chiave","valore");
        assertNotNull( pnLRUCache.get("chiave"));
    }

    @Test
    void clear() {
        pnLRUCache.put("chiave","valore");
        pnLRUCache.clear();
        assertNull( pnLRUCache.get("chiave"));
    }
}