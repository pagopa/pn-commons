package it.pagopa.pn.commons.log;

import org.jetbrains.annotations.NotNull;
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
        final Object mdcTraceidVal = coreSubscriber.currentContext().getOrDefault(MDCWebFilter.MDC_TRACE_ID_KEY, null);
        final Object mdcJtiVal = coreSubscriber.currentContext().getOrDefault(MDCWebFilter.MDC_JTI_KEY, null);
        final Object mdcPnUidVal = coreSubscriber.currentContext().getOrDefault(MDCWebFilter.MDC_PN_UID_KEY, null);
        final Object mdcCxIdVal = coreSubscriber.currentContext().getOrDefault(MDCWebFilter.MDC_CX_ID_KEY, null);
        final Object mdcPnCxTypeVal = coreSubscriber.currentContext().getOrDefault(MDCWebFilter.MDC_PN_CX_TYPE_KEY, null);
        final Object mdcPnCxGroupsVal = coreSubscriber.currentContext().getOrDefault(MDCWebFilter.MDC_PN_CX_GROUPS_KEY, null);
        // NB: MDC supporta il value null, a patto che chi lo usa lo supporti a sua volta.
        // si sceglie comunque di inserire i valori solo se presenti gestendo i vari casi
        if(mdcTraceidVal != null && mdcJtiVal != null && mdcPnUidVal != null && mdcCxIdVal != null && mdcPnCxTypeVal != null
        && mdcPnCxGroupsVal != null) {

            try(MDC.MDCCloseable ignored = MDC.putCloseable(MDCWebFilter.MDC_TRACE_ID_KEY, mdcTraceidVal.toString());
                MDC.MDCCloseable ignored1 = MDC.putCloseable(MDCWebFilter.MDC_JTI_KEY, mdcJtiVal.toString());
                MDC.MDCCloseable ignored2 = MDC.putCloseable(MDCWebFilter.MDC_PN_UID_KEY, mdcPnUidVal.toString());
                MDC.MDCCloseable ignored3 = MDC.putCloseable(MDCWebFilter.MDC_CX_ID_KEY, mdcCxIdVal.toString());
                MDC.MDCCloseable ignored4 = MDC.putCloseable(MDCWebFilter.MDC_PN_CX_TYPE_KEY, mdcPnCxTypeVal.toString());
                MDC.MDCCloseable ignored5 = MDC.putCloseable(MDCWebFilter.MDC_PN_CX_GROUPS_KEY, mdcPnCxGroupsVal.toString())
            ) {
                task.run();
            }
        } else if (mdcTraceidVal != null) {
            try(MDC.MDCCloseable ignored = MDC.putCloseable(MDCWebFilter.MDC_TRACE_ID_KEY, mdcTraceidVal.toString())) {
                task.run();
            }
        } else if (mdcJtiVal != null) {
            try(MDC.MDCCloseable ignored = MDC.putCloseable(MDCWebFilter.MDC_JTI_KEY, mdcJtiVal.toString())) {
                task.run();
            }
        } else if (mdcPnUidVal != null) {
            try(MDC.MDCCloseable ignored = MDC.putCloseable(MDCWebFilter.MDC_PN_UID_KEY, mdcPnUidVal.toString())) {
                task.run();
            }
        } else if (mdcCxIdVal != null) {
            try(MDC.MDCCloseable ignored = MDC.putCloseable(MDCWebFilter.MDC_CX_ID_KEY, mdcCxIdVal.toString())) {
                task.run();
            }
        } else if (mdcPnCxTypeVal != null) {
            try(MDC.MDCCloseable ignored = MDC.putCloseable(MDCWebFilter.MDC_PN_CX_TYPE_KEY, mdcPnCxTypeVal.toString())) {
                task.run();
            }
        } else if (mdcPnCxGroupsVal != null) {
            try(MDC.MDCCloseable ignored = MDC.putCloseable(MDCWebFilter.MDC_PN_CX_GROUPS_KEY, mdcPnCxGroupsVal.toString())) {
                task.run();
            }
        }
        else {
            task.run();
        }
    }
}