package it.pagopa.pn.commons.log;

import org.slf4j.MarkerFactory;

public enum PnAuditLogMarker {
    AUDIT10Y, AUDIT5Y, AUDIT2Y;

    final org.slf4j.Marker mark;

    PnAuditLogMarker() {
        this.mark = MarkerFactory.getMarker(this.name());
    }
}
