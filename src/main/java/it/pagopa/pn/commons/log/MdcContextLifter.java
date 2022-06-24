package it.pagopa.pn.commons.log;

import org.reactivestreams.Subscription;
import org.slf4j.MDC;
import reactor.core.CoreSubscriber;
import reactor.util.context.Context;

/**
 * Helper that copies the state of Reactor [Context] to MDC on the #onNext function.
 */
class MdcContextLifter<T> implements CoreSubscriber<T> {

    CoreSubscriber<T> coreSubscriber;

    public MdcContextLifter(CoreSubscriber<T> coreSubscriber) {
        this.coreSubscriber = coreSubscriber;
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        coreSubscriber.onSubscribe(subscription);
    }

    @Override
    public void onNext(T obj) {
        injectMdc(() -> coreSubscriber.onNext(obj));
    }

    @Override
    public void onError(Throwable t) {
        injectMdc(() -> coreSubscriber.onError(t));
    }

    @Override
    public void onComplete() {
        injectMdc(() -> coreSubscriber.onComplete());
    }

    @Override
    public Context currentContext() {
        return coreSubscriber.currentContext();
    }

    /**
     * Adding correlationId in MDC as closeable statement.
     * @param task
     */
    private void injectMdc(Runnable task) {
        final Object mdcVal = coreSubscriber.currentContext().getOrDefault(MDCWebFilter.MDC_TRACE_ID_KEY, null);
        if(mdcVal != null ) {
            try(MDC.MDCCloseable ignored = MDC.putCloseable(MDCWebFilter.MDC_TRACE_ID_KEY, mdcVal.toString())) {
                task.run();
            }
        } else {
            task.run();
        }
    }
}