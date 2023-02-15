package it.pagopa.pn.commons.configs;

import it.pagopa.pn.commons.abstractions.ParameterConsumer;
import it.pagopa.pn.commons.utils.ValidateUtils;
import org.springframework.context.annotation.Bean;

public class ValidationCFConfiguration {

    private final ParameterConsumer parameterConsumer;

    public ValidationCFConfiguration(ParameterConsumer parameterConsumer) {
        this.parameterConsumer = parameterConsumer;
    }

    @Bean
    public TaxIdInWhiteListParameterConsumer taxIdInWhiteListParameterConsumer() {
        return new TaxIdInWhiteListParameterConsumer( parameterConsumer );
    }

    @Bean
    public ValidateUtils validationUtils() {
        return new ValidateUtils( taxIdInWhiteListParameterConsumer() );
    }

}
