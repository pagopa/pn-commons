package it.pagopa.pn.commons_delivery.utils;

import it.pagopa.pn.api.dto.notification.status.NotificationStatus;
import it.pagopa.pn.api.dto.notification.timeline.TimelineElementCategory;
import it.pagopa.pn.commons.exceptions.PnInternalException;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
@Slf4j
class StateMap {

    private final Map<MapKey, MapValue> mappings = new HashMap<>();

    public StateMap() {

        // Received state
        this.fromState(NotificationStatus.RECEIVED)
                .withTimelineGoToState(TimelineElementCategory.NOTIFICATION_PATH_CHOOSE, NotificationStatus.DELIVERING)
                .withTimelineGoToState(TimelineElementCategory.RECEIVED_ACK, NotificationStatus.RECEIVED)
                .remainToStateWithWarning(TimelineElementCategory.END_OF_DIGITAL_DELIVERY_WORKFLOW, NotificationStatus.RECEIVED)
                .remainToStateWithWarning(TimelineElementCategory.SEND_DIGITAL_DOMICILE, NotificationStatus.RECEIVED)
                .remainToStateWithWarning(TimelineElementCategory.SEND_DIGITAL_DOMICILE_FEEDBACK, NotificationStatus.RECEIVED)
                .remainToStateWithWarning(TimelineElementCategory.NOTIFICATION_VIEWED, NotificationStatus.RECEIVED)
                .remainToStateWithWarning(TimelineElementCategory.PAYMENT, NotificationStatus.RECEIVED)
                .remainToStateWithWarning(TimelineElementCategory.WAIT_FOR_RECIPIENT_TIMEOUT, NotificationStatus.RECEIVED)
        ;

        // Delivering state
        this.fromState(NotificationStatus.DELIVERING)
                .withTimelineGoToState(TimelineElementCategory.SEND_DIGITAL_DOMICILE, NotificationStatus.DELIVERING)
                .withTimelineGoToState(TimelineElementCategory.SEND_DIGITAL_DOMICILE_FEEDBACK, NotificationStatus.DELIVERING)
                .withTimelineGoToState(TimelineElementCategory.END_OF_DIGITAL_DELIVERY_WORKFLOW, NotificationStatus.DELIVERED)
                .withTimelineGoToState(TimelineElementCategory.NOTIFICATION_VIEWED, NotificationStatus.VIEWED)
                .withTimelineGoToState(TimelineElementCategory.PAYMENT, NotificationStatus.PAID)
                .withTimelineGoToState(TimelineElementCategory.NOTIFICATION_PATH_CHOOSE, NotificationStatus.DELIVERING)
                .remainToStateWithWarning(TimelineElementCategory.RECEIVED_ACK, NotificationStatus.DELIVERING)
                .remainToStateWithWarning(TimelineElementCategory.WAIT_FOR_RECIPIENT_TIMEOUT, NotificationStatus.DELIVERING)
        ;

        // Delivered state
        this.fromState(NotificationStatus.DELIVERED)
                .remainToStateWithWarning(TimelineElementCategory.SEND_DIGITAL_DOMICILE, NotificationStatus.DELIVERED)
                .remainToStateWithWarning(TimelineElementCategory.SEND_DIGITAL_DOMICILE_FEEDBACK, NotificationStatus.DELIVERED)
                .remainToStateWithWarning(TimelineElementCategory.END_OF_DIGITAL_DELIVERY_WORKFLOW, NotificationStatus.DELIVERED)
                .withTimelineGoToState(TimelineElementCategory.NOTIFICATION_VIEWED, NotificationStatus.VIEWED)
                .withTimelineGoToState(TimelineElementCategory.PAYMENT, NotificationStatus.PAID)
                .remainToStateWithWarning(TimelineElementCategory.NOTIFICATION_PATH_CHOOSE, NotificationStatus.DELIVERED)
                .remainToStateWithWarning(TimelineElementCategory.RECEIVED_ACK, NotificationStatus.DELIVERED)
                .withTimelineGoToState(TimelineElementCategory.WAIT_FOR_RECIPIENT_TIMEOUT, NotificationStatus.EFFECTIVE_DATE)
        ;

        // Effective date state
        this.fromState(NotificationStatus.EFFECTIVE_DATE)
                .remainToStateWithWarning(TimelineElementCategory.SEND_DIGITAL_DOMICILE, NotificationStatus.EFFECTIVE_DATE)
                .remainToStateWithWarning(TimelineElementCategory.SEND_DIGITAL_DOMICILE_FEEDBACK, NotificationStatus.EFFECTIVE_DATE)
                .remainToStateWithWarning(TimelineElementCategory.END_OF_DIGITAL_DELIVERY_WORKFLOW, NotificationStatus.EFFECTIVE_DATE)
                .withTimelineGoToState(TimelineElementCategory.NOTIFICATION_VIEWED, NotificationStatus.VIEWED)
                .withTimelineGoToState(TimelineElementCategory.PAYMENT, NotificationStatus.PAID)
                .remainToStateWithWarning(TimelineElementCategory.NOTIFICATION_PATH_CHOOSE, NotificationStatus.EFFECTIVE_DATE)
                .remainToStateWithWarning(TimelineElementCategory.RECEIVED_ACK, NotificationStatus.EFFECTIVE_DATE)
                .remainToStateWithWarning(TimelineElementCategory.WAIT_FOR_RECIPIENT_TIMEOUT, NotificationStatus.EFFECTIVE_DATE)
        ;

        // Viewed state
        this.fromState(NotificationStatus.VIEWED)
                .withTimelineGoToState(TimelineElementCategory.SEND_DIGITAL_DOMICILE, NotificationStatus.VIEWED)
                .withTimelineGoToState(TimelineElementCategory.SEND_DIGITAL_DOMICILE_FEEDBACK, NotificationStatus.VIEWED)
                .withTimelineGoToState(TimelineElementCategory.END_OF_DIGITAL_DELIVERY_WORKFLOW, NotificationStatus.VIEWED)
                .remainToStateWithWarning(TimelineElementCategory.NOTIFICATION_VIEWED, NotificationStatus.VIEWED)
                .withTimelineGoToState(TimelineElementCategory.PAYMENT, NotificationStatus.PAID)
                .remainToStateWithWarning(TimelineElementCategory.NOTIFICATION_PATH_CHOOSE, NotificationStatus.VIEWED)
                .remainToStateWithWarning(TimelineElementCategory.RECEIVED_ACK, NotificationStatus.VIEWED)
                .withTimelineGoToState(TimelineElementCategory.WAIT_FOR_RECIPIENT_TIMEOUT, NotificationStatus.VIEWED)
        ;

        // Paid state
        this.fromState(NotificationStatus.PAID)
                .withTimelineGoToState(TimelineElementCategory.SEND_DIGITAL_DOMICILE, NotificationStatus.PAID)
                .withTimelineGoToState(TimelineElementCategory.SEND_DIGITAL_DOMICILE_FEEDBACK, NotificationStatus.PAID)
                .withTimelineGoToState(TimelineElementCategory.END_OF_DIGITAL_DELIVERY_WORKFLOW, NotificationStatus.PAID)
                .remainToStateWithWarning(TimelineElementCategory.NOTIFICATION_VIEWED, NotificationStatus.PAID)
                .remainToStateWithWarning(TimelineElementCategory.PAYMENT, NotificationStatus.PAID)
                .remainToStateWithWarning(TimelineElementCategory.NOTIFICATION_PATH_CHOOSE, NotificationStatus.PAID)
                .remainToStateWithWarning(TimelineElementCategory.RECEIVED_ACK, NotificationStatus.PAID)
                .withTimelineGoToState(TimelineElementCategory.WAIT_FOR_RECIPIENT_TIMEOUT, NotificationStatus.PAID)
            ;

    }

    NotificationStatus getStateTransition(NotificationStatus fromStatus, TimelineElementCategory timelineRowType) throws PnInternalException {
        MapKey key = new MapKey(fromStatus, timelineRowType);
        if (!this.mappings.containsKey(key)) {
            throw new PnInternalException("Unsupported state transition from state " + fromStatus + " with timeline element type " + timelineRowType);
        }

        final MapValue mapValue = this.mappings.get(key);
        if (mapValue.isWarning()) {
            log.warn("Illegal input \"" + timelineRowType + "\" in state \"" + fromStatus + "\"");
        }
        return mapValue.getStatus();
    }


    private InputMapper fromState(NotificationStatus fromStatus) {
        return new InputMapper(fromStatus);
    }


    private class InputMapper {

        private final NotificationStatus fromStatus;

        public InputMapper(NotificationStatus fromStatus) {
            this.fromStatus = fromStatus;
        }

        public InputMapper withTimelineGoToState(TimelineElementCategory timelineRowType, NotificationStatus destinationStatus) {
            StateMap.this.mappings.put(new MapKey(fromStatus, timelineRowType), new MapValue(destinationStatus, false));
            return this;
        }

        public InputMapper remainToStateWithWarning(TimelineElementCategory timelineRowType, NotificationStatus destinationStatus) {
            StateMap.this.mappings.put(new MapKey(fromStatus, timelineRowType), new MapValue(destinationStatus, true));
            return this;
        }
    }

    @Value
    private static class MapKey {
        private final NotificationStatus status;
        private final TimelineElementCategory timelineElementCategory;
    }

    @Value
    private static class MapValue {
        private final NotificationStatus status;
        private final boolean warning;
    }
}
