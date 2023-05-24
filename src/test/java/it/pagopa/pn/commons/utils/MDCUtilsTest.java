package it.pagopa.pn.commons.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class MDCUtilsTest {


    @BeforeEach
    public void init() {
        MDC.clear();
        MDC.put("key", "value");
    }

    @AfterEach
    public void clean() {
        MDC.clear();
    }

    @Test
    void retrieveMDCContextMapTest() {
        Map<String, String> map = MDCUtils.retrieveMDCContextMap();
        assertThat(map)
                .isNotEmpty()
                .hasSize(1)
                .containsKey("key");
    }

    @Test
    void enrichWithMDCTest() {
        Map<String, String> anotherMap = Map.of("anotherKey", "anotherValue");
        String actualValue = MDCUtils.enrichWithMDC("aValueReturned", anotherMap);
        assertThat(actualValue).isEqualTo("aValueReturned");
        assertThat(MDC.getCopyOfContextMap())
                .isNotEmpty()
                .hasSize(1)
                .containsKey("anotherKey");

    }
}
