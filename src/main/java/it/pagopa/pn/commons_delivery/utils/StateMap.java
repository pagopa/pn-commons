package it.pagopa.pn.commons_delivery.utils;

import it.pagopa.pn.api.dto.notification.status.NotificationStatus;
import it.pagopa.pn.api.dto.notification.timeline.TimelineElementCategory;
import it.pagopa.pn.commons.exceptions.PnInternalException;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
class StateMap {
    //TODO Da rivedere meglio gli stati
    
    private final Map<MapKey, MapValue> mappings = new HashMap<>();

    public StateMap() {
        // Received state
        this.fromState(NotificationStatus.IN_VALIDATION)
                .withTimelineGoToState(TimelineElementCategory.REQUEST_ACCEPTED, NotificationStatus.ACCEPTED)
                .withTimelineGoToState(TimelineElementCategory.REQUEST_REFUSED, NotificationStatus.REFUSED)
        ;
                
        this.fromState(NotificationStatus.ACCEPTED)
                .withTimelineGoToState(TimelineElementCategory.NOTIFICATION_PATH_CHOOSE, NotificationStatus.DELIVERING)
                .withTimelineGoToState(TimelineElementCategory.SEND_COURTESY_MESSAGE, NotificationStatus.ACCEPTED)
                .withTimelineGoToState(TimelineElementCategory.GET_ADDRESS, NotificationStatus.ACCEPTED)
                .withTimelineGoToState(TimelineElementCategory.PUBLIC_REGISTRY_CALL, NotificationStatus.ACCEPTED)
                .withTimelineGoToState(TimelineElementCategory.PUBLIC_REGISTRY_RESPONSE, NotificationStatus.ACCEPTED)
                .withTimelineGoToState(TimelineElementCategory.SCHEDULE_ANALOG_WORKFLOW, NotificationStatus.DELIVERING)
                .withTimelineGoToState(TimelineElementCategory.SEND_DIGITAL_DOMICILE, NotificationStatus.DELIVERING)
                .withTimelineGoToState(TimelineElementCategory.SEND_ANALOG_DOMICILE, NotificationStatus.DELIVERING)
                .withTimelineGoToState(TimelineElementCategory.NOTIFICATION_VIEWED, NotificationStatus.VIEWED)
                .withTimelineGoToState(TimelineElementCategory.PAYMENT, NotificationStatus.PAID)
        ;

        // Delivering state
        this.fromState(NotificationStatus.DELIVERING)
                .withTimelineGoToState(TimelineElementCategory.SEND_DIGITAL_DOMICILE_FAILURE, NotificationStatus.DELIVERING)
                .withTimelineGoToState(TimelineElementCategory.SEND_PAPER_FEEDBACK, NotificationStatus.DELIVERING)
                .withTimelineGoToState(TimelineElementCategory.SEND_DIGITAL_DOMICILE, NotificationStatus.DELIVERING)
                .withTimelineGoToState(TimelineElementCategory.SEND_DIGITAL_DOMICILE_FEEDBACK, NotificationStatus.DELIVERING)
                .withTimelineGoToState(TimelineElementCategory.END_OF_DIGITAL_DELIVERY_WORKFLOW, NotificationStatus.DELIVERED)
                .withTimelineGoToState(TimelineElementCategory.END_OF_ANALOG_DELIVERY_WORKFLOW, NotificationStatus.DELIVERED)
                .withTimelineGoToState(TimelineElementCategory.NOTIFICATION_VIEWED, NotificationStatus.VIEWED)
                .withTimelineGoToState(TimelineElementCategory.PAYMENT, NotificationStatus.PAID)
                .withTimelineGoToState(TimelineElementCategory.NOTIFICATION_PATH_CHOOSE, NotificationStatus.DELIVERING)
                .withTimelineGoToState(TimelineElementCategory.SEND_ANALOG_DOMICILE, NotificationStatus.DELIVERING)
                .withTimelineGoToState(TimelineElementCategory.COMPLETELY_UNREACHABLE, NotificationStatus.UNREACHABLE)

                .withTimelineGoToState(TimelineElementCategory.GET_ADDRESS, NotificationStatus.DELIVERING)
                .withTimelineGoToState(TimelineElementCategory.PUBLIC_REGISTRY_CALL, NotificationStatus.DELIVERING)
                .withTimelineGoToState(TimelineElementCategory.PUBLIC_REGISTRY_RESPONSE, NotificationStatus.DELIVERING)
                .withTimelineGoToState(TimelineElementCategory.SEND_DIGITAL_FEEDBACK, NotificationStatus.DELIVERING)
                .withTimelineGoToState(TimelineElementCategory.SEND_SIMPLE_REGISTERED_LETTER, NotificationStatus.DELIVERING)
                .withTimelineGoToState(TimelineElementCategory.ANALOG_FAILURE_WORKFLOW, NotificationStatus.DELIVERING)
                .withTimelineGoToState(TimelineElementCategory.SCHEDULE_DIGITAL_WORKFLOW, NotificationStatus.DELIVERING)

                .withTimelineGoToState(TimelineElementCategory.DIGITAL_SUCCESS_WORKFLOW, NotificationStatus.DELIVERED)
                .withTimelineGoToState(TimelineElementCategory.DIGITAL_FAILURE_WORKFLOW, NotificationStatus.DELIVERED)
                .withTimelineGoToState(TimelineElementCategory.ANALOG_SUCCESS_WORKFLOW, NotificationStatus.DELIVERED)
        ;

        // Delivered state
        this.fromState(NotificationStatus.DELIVERED)
                .withTimelineGoToState(TimelineElementCategory.NOTIFICATION_VIEWED, NotificationStatus.VIEWED)
                .withTimelineGoToState(TimelineElementCategory.PAYMENT, NotificationStatus.PAID)
                .withTimelineGoToState(TimelineElementCategory.REFINEMENT, NotificationStatus.EFFECTIVE_DATE)
                .withTimelineGoToState(TimelineElementCategory.SCHEDULE_REFINEMENT, NotificationStatus.DELIVERED)
        ;

        // Effective date state
        this.fromState(NotificationStatus.EFFECTIVE_DATE)
                .withTimelineGoToState(TimelineElementCategory.NOTIFICATION_VIEWED, NotificationStatus.VIEWED)
                .withTimelineGoToState(TimelineElementCategory.PAYMENT, NotificationStatus.PAID)
                .withTimelineGoToState(TimelineElementCategory.REFINEMENT, NotificationStatus.EFFECTIVE_DATE)
        ;

        // Viewed state
        this.fromState(NotificationStatus.VIEWED)
                .withTimelineGoToState(TimelineElementCategory.SEND_DIGITAL_DOMICILE, NotificationStatus.VIEWED)
                .withTimelineGoToState(TimelineElementCategory.SEND_DIGITAL_DOMICILE_FEEDBACK, NotificationStatus.VIEWED)
                .withTimelineGoToState(TimelineElementCategory.END_OF_DIGITAL_DELIVERY_WORKFLOW, NotificationStatus.VIEWED)
                .withTimelineGoToState(TimelineElementCategory.PAYMENT, NotificationStatus.PAID)
                .withTimelineGoToState(TimelineElementCategory.REFINEMENT, NotificationStatus.VIEWED)
        ;

        // Paid state
        this.fromState(NotificationStatus.PAID)
                .withTimelineGoToState(TimelineElementCategory.SEND_DIGITAL_DOMICILE, NotificationStatus.PAID)
                .withTimelineGoToState(TimelineElementCategory.SEND_DIGITAL_DOMICILE_FEEDBACK, NotificationStatus.PAID)
                .withTimelineGoToState(TimelineElementCategory.END_OF_DIGITAL_DELIVERY_WORKFLOW, NotificationStatus.PAID)
                .withTimelineGoToState(TimelineElementCategory.REFINEMENT, NotificationStatus.PAID)
        ;
        this.fromState(NotificationStatus.UNREACHABLE)
                .withTimelineGoToState(TimelineElementCategory.SCHEDULE_REFINEMENT, NotificationStatus.UNREACHABLE)
                .withTimelineGoToState(TimelineElementCategory.NOTIFICATION_VIEWED, NotificationStatus.VIEWED)
                .withTimelineGoToState(TimelineElementCategory.REFINEMENT, NotificationStatus.EFFECTIVE_DATE)
        ;
    }

    NotificationStatus getStateTransition(NotificationStatus fromStatus, TimelineElementCategory timelineRowType) throws PnInternalException {
        MapKey key = new MapKey(fromStatus, timelineRowType);
        if (!this.mappings.containsKey(key)) {
            log.warn("Illegal input \"" + timelineRowType + "\" in state \"" + fromStatus + "\"");
            return fromStatus;
        }

        final MapValue mapValue = this.mappings.get(key);
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
            StateMap.this.mappings.put(new MapKey(fromStatus, timelineRowType), new MapValue(destinationStatus));
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
    }
}
