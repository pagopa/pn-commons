package it.pagopa.pn.commons.configs.cache;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


class PnCacheManagerTest {

    PnCacheManager pnCacheManager;
    @BeforeEach
    void setUp() {
        pnCacheManager = new PnCacheManager();
    }


    @Test
    void initialize() {
        Map<String, Integer> v = new HashMap<>();
        v.put("k", 100);

        pnCacheManager.initialize(v);
        Collection<String> c = pnCacheManager.getCacheNames();
        Assertions.assertNotNull(c);
        Assertions.assertEquals(1, c.size());
    }

    @Test
    void getCacheNames() {
        Map<String, Integer> v = new HashMap<>();
        v.put("k", 100);

        pnCacheManager.initialize(v);
        Collection<String> c = pnCacheManager.getCacheNames();
        Assertions.assertNotNull(c);
        Assertions.assertEquals(1, c.size());
    }

    @Test
    void getCache() {
        Map<String, Integer> v = new HashMap<>();
        v.put("k", 100);

        pnCacheManager.initialize(v);
        Cache c = pnCacheManager.getCache("k");
        Assertions.assertNotNull(c);
    }
}