package it.pagopa.pn.commons.configs;

import it.pagopa.pn.commons.abstractions.impl.AbstractCachedSsmParameterConsumer;
import org.springframework.stereotype.Component;

@Component
public class MVPParameterConsumerTestActivator extends MVPParameterConsumer {
    public MVPParameterConsumerTestActivator(AbstractCachedSsmParameterConsumer abstractCachedSsmParameterConsumer) {
        super(abstractCachedSsmParameterConsumer);
    }
}
