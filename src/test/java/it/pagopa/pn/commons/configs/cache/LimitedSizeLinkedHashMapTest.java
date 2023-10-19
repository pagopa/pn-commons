package it.pagopa.pn.commons.configs.cache;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LimitedSizeLinkedHashMapTest {

    @Test
    void removeEldestEntry() {
        LimitedSizeLinkedHashMap<String, String> limitedMap= new LimitedSizeLinkedHashMap<>(10);
        for (int i=0;i<11;i++){
            limitedMap.put("key"+i, "value"+i);
        }

        Assertions.assertEquals(10, limitedMap.size());
        Assertions.assertFalse(limitedMap.containsKey("key0"));
    }
}