package it.pagopa.pn.commons.configs;

import it.pagopa.pn.commons.abstractions.impl.AbstractCachedSsmParameterConsumer;
import org.springframework.stereotype.Component;

@Component
public class IsMVPParameterConsumerTestActivator extends IsMVPParameterConsumer{
    public IsMVPParameterConsumerTestActivator(AbstractCachedSsmParameterConsumer abstractCachedSsmParameterConsumer) {
        super(abstractCachedSsmParameterConsumer);
    }
}
