package it.pagopa.pn.commons.log;

import it.pagopa.pn.commons.utils.MDCUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Subscription;
import org.slf4j.MDC;
import reactor.core.CoreSubscriber;
import reactor.util.context.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper that copies the state of Reactor [Context] to MDC on the #onNext function.
 */
@Slf4j
class MdcContextLifter<T> implements CoreSubscriber<T> {

    CoreSubscriber<T> coreSubscriber;

    public MdcContextLifter(CoreSubscriber<T> coreSubscriber) {
        this.coreSubscriber = coreSubscriber;
    }

    @Override
    public void onSubscribe(@NotNull Subscription subscription) {
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
    public @NotNull Context currentContext() {
        return coreSubscriber.currentContext();
    }

    /**
     * Adding correlationId in MDC as closeable statement.
     * @param task task da eseguire
     */
    private void injectMdc(Runnable task) {
        List<MDC.MDCCloseable> closeables = new ArrayList<>();
        try {
            // pulisco/aggiorno le chiavi di pertinenza di PN
            closeables = MDCUtils.alignMDCToWebfluxContext(coreSubscriber.currentContext());
            task.run();
        } catch (Exception e) {
            log.error("cannot update MDC with context key-values", e);
        } finally {
            for (MDC.MDCCloseable c :
                    closeables) {
                c.close();
            }
        }
    }

}