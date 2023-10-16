package it.pagopa.pn.commons.configs.cache;

import static org.junit.jupiter.api.Assertions.*;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
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
    void get1() {
        pnLRUCache.put("chiave","valore");
        String v = pnLRUCache.get("chiave1", () -> {return "valore1";});
        assertNotNull(v);
        assertEquals("valore1", v);

        v = pnLRUCache.get("chiave1", () -> {return "valore2";});
        assertNotNull(v);
        assertEquals("valore1", v);


        Assertions.assertThrows(PnInternalException.class, () -> pnLRUCache.get("chiave2", () -> {throw new RuntimeException("errore");}));
    }

    @Test
    void get2() {
        pnLRUCache.put("chiave","valore");
        String v = pnLRUCache.get("chiave", String.class);
        assertNotNull(v);
        assertEquals("valore", v);
    }

    @Test
    void put() {
        pnLRUCache.put("chiave","valore");
        assertNotNull( pnLRUCache.get("chiave"));
    }


    @Test
    void putIfAbset() {
        pnLRUCache.putIfAbsent("chiave","valore");
        assertNotNull( pnLRUCache.get("chiave"));
        assertEquals("valore",  pnLRUCache.get("chiave").get());
        pnLRUCache.putIfAbsent("chiave","valore1");
        assertEquals("valore",  pnLRUCache.get("chiave").get());
    }

    @Test
    void clear() {
        pnLRUCache.put("chiave","valore");
        pnLRUCache.clear();
        assertNull( pnLRUCache.get("chiave"));
    }


    @Test
    void evict() {
        pnLRUCache.put("chiave","valore");
        assertEquals("valore",  pnLRUCache.get("chiave").get());
        pnLRUCache.evict("chiave");
        assertNull( pnLRUCache.get("chiave"));

    }
}