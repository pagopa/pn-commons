package it.pagopa.pn.commons.utils;

import com.amazonaws.DefaultRequest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class CustomAWSRequestHandlerTest {


    @Test
    void test() {
        CustomAWSRequestHandler handler = new CustomAWSRequestHandler();
        assertDoesNotThrow(() -> handler.afterError(new DefaultRequest<>("SQS"), null, new RuntimeException()));
    }
}
