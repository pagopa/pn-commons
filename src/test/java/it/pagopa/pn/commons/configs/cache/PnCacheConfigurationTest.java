package it.pagopa.pn.commons.configs.cache;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = {PnCacheConfiguration.class}, properties={"pn.cache.enabled=true", "pn.cache.cacheNames=testcache(10000)"})
class PnCacheConfigurationTest {

    @Autowired
    PnCacheConfiguration pnCacheConfiguration;


    @Test
    void cacheManager() {
        PnCacheManager pnCacheManager = pnCacheConfiguration.cacheManager();
        Assertions.assertNotNull(pnCacheManager);
    }
}