package it.pagopa.pn.commons.configs;

import it.pagopa.pn.commons.abstractions.ParameterConsumer;
import it.pagopa.pn.commons.utils.ValidateUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;


class ValidationCFConfigurationTest {

    @Mock
    private ParameterConsumer parameterConsumer;

    private ValidationCFConfiguration validationCFConfiguration;

    @BeforeEach
    void setup() {
        validationCFConfiguration = new ValidationCFConfiguration( parameterConsumer );
    }

    @Test
    void createTaxIdInWhiteListParameterConsumer() {
        TaxIdInWhiteListParameterConsumer taxIdInWhiteListParameterConsumer = validationCFConfiguration.taxIdInWhiteListParameterConsumer();
        Assertions.assertNotNull( taxIdInWhiteListParameterConsumer );
    }

    @Test
    void createTaxIdInBlackListParameterConsumer() {
        TaxIdInBlackListParameterConsumer taxIdInBlackListParameterConsumer = validationCFConfiguration.taxIdInBlackListParameterConsumer();
        Assertions.assertNotNull( taxIdInBlackListParameterConsumer );
    }

    @Test
    void createValidationCFConfig() {
        ValidateUtils validateUtils = validationCFConfiguration.validationUtils();
        Assertions.assertNotNull( validateUtils );
    }

}
