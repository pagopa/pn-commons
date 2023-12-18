package it.pagopa.pn.commons.log;

import org.slf4j.MarkerFactory;

/**
 * Class for mark audit logs base on retention period
 */
public class PnAuditMarkerFactory {
    enum Marker {
        AUDIT10Y, AUDIT5Y, AUDIT2Y;

        private final org.slf4j.Marker mark;

        Marker() {
            this.mark = MarkerFactory.getMarker(this.name());
        }
    }

    /**
     * Ten years retention audit logs for legal facts data
     * @return 10 years log marker
     */
    public static org.slf4j.Marker get10yMarker() {
        return Marker.AUDIT10Y.mark;
    }

    /**
     * Five years retention audit logs
     * @return 5 years log marker
     */
    public static org.slf4j.Marker get5yMarker() {
        return  Marker.AUDIT5Y.mark;
    }

    /**
     * Five years retention audit logs
     * @return 2 years log marker
     */
    public static org.slf4j.Marker get2yMarker() {
        return  Marker.AUDIT2Y.mark;
    }

}
