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
        this.fromState(NotificationStatus.RECEIVED)
                .withTimelineGoToState(TimelineElementCategory.NOTIFICATION_PATH_CHOOSE, NotificationStatus.DELIVERING)
                .withTimelineGoToState(TimelineElementCategory.REQUEST_ACCEPTED, NotificationStatus.RECEIVED)
                .remainToStateWithWarning(TimelineElementCategory.END_OF_DIGITAL_DELIVERY_WORKFLOW, NotificationStatus.RECEIVED)
                .remainToStateWithWarning(TimelineElementCategory.SEND_DIGITAL_DOMICILE, NotificationStatus.RECEIVED)
                .remainToStateWithWarning(TimelineElementCategory.SEND_DIGITAL_DOMICILE_FEEDBACK, NotificationStatus.RECEIVED)
                .remainToStateWithWarning(TimelineElementCategory.NOTIFICATION_VIEWED, NotificationStatus.RECEIVED)
                .remainToStateWithWarning(TimelineElementCategory.PAYMENT, NotificationStatus.RECEIVED)
                .remainToStateWithWarning(TimelineElementCategory.REFINEMENT, NotificationStatus.RECEIVED)
                .remainToStateWithWarning(TimelineElementCategory.SEND_DIGITAL_DOMICILE_FAILURE, NotificationStatus.RECEIVED)
                .remainToStateWithWarning(TimelineElementCategory.SEND_PAPER_FEEDBACK, NotificationStatus.RECEIVED)

                .withTimelineGoToState(TimelineElementCategory.SEND_COURTESY_MESSAGE, NotificationStatus.RECEIVED)
                .withTimelineGoToState(TimelineElementCategory.GET_ADDRESS, NotificationStatus.RECEIVED)
                .withTimelineGoToState(TimelineElementCategory.PUBLIC_REGISTRY_CALL, NotificationStatus.RECEIVED)
                .withTimelineGoToState(TimelineElementCategory.PUBLIC_REGISTRY_RESPONSE, NotificationStatus.RECEIVED)
                .withTimelineGoToState(TimelineElementCategory.SCHEDULE_ANALOG_WORKFLOW, NotificationStatus.DELIVERING)

                .remainToStateWithWarning(TimelineElementCategory.SEND_DIGITAL_FEEDBACK, NotificationStatus.RECEIVED)
                .remainToStateWithWarning(TimelineElementCategory.SCHEDULE_REFINEMENT, NotificationStatus.RECEIVED)
                .remainToStateWithWarning(TimelineElementCategory.DIGITAL_SUCCESS_WORKFLOW, NotificationStatus.RECEIVED)
                .remainToStateWithWarning(TimelineElementCategory.DIGITAL_FAILURE_WORKFLOW, NotificationStatus.RECEIVED)
                .remainToStateWithWarning(TimelineElementCategory.ANALOG_SUCCESS_WORKFLOW, NotificationStatus.RECEIVED)
                .remainToStateWithWarning(TimelineElementCategory.ANALOG_FAILURE_WORKFLOW, NotificationStatus.RECEIVED)
                .remainToStateWithWarning(TimelineElementCategory.SEND_SIMPLE_REGISTERED_LETTER, NotificationStatus.RECEIVED)

                .withTimelineGoToState(TimelineElementCategory.SEND_DIGITAL_DOMICILE, NotificationStatus.DELIVERING)
                .withTimelineGoToState(TimelineElementCategory.SEND_ANALOG_DOMICILE, NotificationStatus.DELIVERING)
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
                .remainToStateWithWarning(TimelineElementCategory.REQUEST_ACCEPTED, NotificationStatus.DELIVERING)
                .remainToStateWithWarning(TimelineElementCategory.REFINEMENT, NotificationStatus.DELIVERING)


                .withTimelineGoToState(TimelineElementCategory.GET_ADDRESS, NotificationStatus.DELIVERING)
                .withTimelineGoToState(TimelineElementCategory.PUBLIC_REGISTRY_CALL, NotificationStatus.DELIVERING)
                .withTimelineGoToState(TimelineElementCategory.PUBLIC_REGISTRY_RESPONSE, NotificationStatus.DELIVERING)
                .withTimelineGoToState(TimelineElementCategory.SEND_DIGITAL_FEEDBACK, NotificationStatus.DELIVERING)
                .withTimelineGoToState(TimelineElementCategory.SEND_SIMPLE_REGISTERED_LETTER, NotificationStatus.DELIVERING)
                .withTimelineGoToState(TimelineElementCategory.ANALOG_FAILURE_WORKFLOW, NotificationStatus.DELIVERING)
                .withTimelineGoToState(TimelineElementCategory.SCHEDULE_DIGITAL_WORKFLOW, NotificationStatus.DELIVERING)

                .remainToStateWithWarning(TimelineElementCategory.SEND_COURTESY_MESSAGE, NotificationStatus.DELIVERING)
                .remainToStateWithWarning(TimelineElementCategory.SCHEDULE_REFINEMENT, NotificationStatus.DELIVERING)
                .remainToStateWithWarning(TimelineElementCategory.SCHEDULE_ANALOG_WORKFLOW, NotificationStatus.DELIVERING)

                .withTimelineGoToState(TimelineElementCategory.DIGITAL_SUCCESS_WORKFLOW, NotificationStatus.DELIVERED)
                .withTimelineGoToState(TimelineElementCategory.DIGITAL_FAILURE_WORKFLOW, NotificationStatus.DELIVERED)
                .withTimelineGoToState(TimelineElementCategory.ANALOG_SUCCESS_WORKFLOW, NotificationStatus.DELIVERED)
        ;

        // Delivered state
        this.fromState(NotificationStatus.DELIVERED)
                .remainToStateWithWarning(TimelineElementCategory.SEND_DIGITAL_DOMICILE, NotificationStatus.DELIVERED)
                .remainToStateWithWarning(TimelineElementCategory.SEND_DIGITAL_DOMICILE_FEEDBACK, NotificationStatus.DELIVERED)
                .remainToStateWithWarning(TimelineElementCategory.END_OF_DIGITAL_DELIVERY_WORKFLOW, NotificationStatus.DELIVERED)
                .withTimelineGoToState(TimelineElementCategory.NOTIFICATION_VIEWED, NotificationStatus.VIEWED)
                .withTimelineGoToState(TimelineElementCategory.PAYMENT, NotificationStatus.PAID)
                .remainToStateWithWarning(TimelineElementCategory.NOTIFICATION_PATH_CHOOSE, NotificationStatus.DELIVERED)
                .remainToStateWithWarning(TimelineElementCategory.REQUEST_ACCEPTED, NotificationStatus.DELIVERED)
                .withTimelineGoToState(TimelineElementCategory.REFINEMENT, NotificationStatus.EFFECTIVE_DATE)
                .remainToStateWithWarning(TimelineElementCategory.SEND_DIGITAL_DOMICILE_FAILURE, NotificationStatus.DELIVERED)
                .remainToStateWithWarning(TimelineElementCategory.SEND_PAPER_FEEDBACK, NotificationStatus.DELIVERED)

                .remainToStateWithWarning(TimelineElementCategory.GET_ADDRESS, NotificationStatus.DELIVERED)
                .remainToStateWithWarning(TimelineElementCategory.PUBLIC_REGISTRY_CALL, NotificationStatus.DELIVERED)
                .remainToStateWithWarning(TimelineElementCategory.PUBLIC_REGISTRY_RESPONSE, NotificationStatus.DELIVERED)
                .remainToStateWithWarning(TimelineElementCategory.SEND_DIGITAL_FEEDBACK, NotificationStatus.DELIVERED)
                .remainToStateWithWarning(TimelineElementCategory.SEND_SIMPLE_REGISTERED_LETTER, NotificationStatus.DELIVERED)
                .remainToStateWithWarning(TimelineElementCategory.DIGITAL_SUCCESS_WORKFLOW, NotificationStatus.DELIVERED)
                .remainToStateWithWarning(TimelineElementCategory.DIGITAL_FAILURE_WORKFLOW, NotificationStatus.DELIVERED)
                .remainToStateWithWarning(TimelineElementCategory.ANALOG_SUCCESS_WORKFLOW, NotificationStatus.DELIVERED)
                .remainToStateWithWarning(TimelineElementCategory.ANALOG_FAILURE_WORKFLOW, NotificationStatus.DELIVERED)
                .remainToStateWithWarning(TimelineElementCategory.SEND_COURTESY_MESSAGE, NotificationStatus.DELIVERED)
                .remainToStateWithWarning(TimelineElementCategory.SCHEDULE_DIGITAL_WORKFLOW, NotificationStatus.DELIVERED)
                .remainToStateWithWarning(TimelineElementCategory.SCHEDULE_ANALOG_WORKFLOW, NotificationStatus.DELIVERED)
                .withTimelineGoToState(TimelineElementCategory.SCHEDULE_REFINEMENT, NotificationStatus.DELIVERED)

        ;

        // Effective date state
        this.fromState(NotificationStatus.EFFECTIVE_DATE)
                .remainToStateWithWarning(TimelineElementCategory.SEND_DIGITAL_DOMICILE, NotificationStatus.EFFECTIVE_DATE)
                .remainToStateWithWarning(TimelineElementCategory.SEND_DIGITAL_DOMICILE_FEEDBACK, NotificationStatus.EFFECTIVE_DATE)
                .remainToStateWithWarning(TimelineElementCategory.END_OF_DIGITAL_DELIVERY_WORKFLOW, NotificationStatus.EFFECTIVE_DATE)
                .withTimelineGoToState(TimelineElementCategory.NOTIFICATION_VIEWED, NotificationStatus.VIEWED)
                .withTimelineGoToState(TimelineElementCategory.PAYMENT, NotificationStatus.PAID)
                .remainToStateWithWarning(TimelineElementCategory.NOTIFICATION_PATH_CHOOSE, NotificationStatus.EFFECTIVE_DATE)
                .remainToStateWithWarning(TimelineElementCategory.REQUEST_ACCEPTED, NotificationStatus.EFFECTIVE_DATE)
                .withTimelineGoToState(TimelineElementCategory.REFINEMENT, NotificationStatus.EFFECTIVE_DATE)
                .remainToStateWithWarning(TimelineElementCategory.SEND_DIGITAL_DOMICILE_FAILURE, NotificationStatus.EFFECTIVE_DATE)
                .remainToStateWithWarning(TimelineElementCategory.SEND_PAPER_FEEDBACK, NotificationStatus.EFFECTIVE_DATE)

                .remainToStateWithWarning(TimelineElementCategory.GET_ADDRESS, NotificationStatus.EFFECTIVE_DATE)
                .remainToStateWithWarning(TimelineElementCategory.PUBLIC_REGISTRY_CALL, NotificationStatus.EFFECTIVE_DATE)
                .remainToStateWithWarning(TimelineElementCategory.PUBLIC_REGISTRY_RESPONSE, NotificationStatus.EFFECTIVE_DATE)
                .remainToStateWithWarning(TimelineElementCategory.SEND_DIGITAL_FEEDBACK, NotificationStatus.EFFECTIVE_DATE)
                .remainToStateWithWarning(TimelineElementCategory.SEND_SIMPLE_REGISTERED_LETTER, NotificationStatus.EFFECTIVE_DATE)
                .remainToStateWithWarning(TimelineElementCategory.DIGITAL_SUCCESS_WORKFLOW, NotificationStatus.EFFECTIVE_DATE)
                .remainToStateWithWarning(TimelineElementCategory.DIGITAL_FAILURE_WORKFLOW, NotificationStatus.EFFECTIVE_DATE)
                .remainToStateWithWarning(TimelineElementCategory.ANALOG_SUCCESS_WORKFLOW, NotificationStatus.EFFECTIVE_DATE)
                .remainToStateWithWarning(TimelineElementCategory.SEND_COURTESY_MESSAGE, NotificationStatus.EFFECTIVE_DATE)
                .remainToStateWithWarning(TimelineElementCategory.SCHEDULE_REFINEMENT, NotificationStatus.EFFECTIVE_DATE)
                .remainToStateWithWarning(TimelineElementCategory.SCHEDULE_DIGITAL_WORKFLOW, NotificationStatus.EFFECTIVE_DATE)
                .remainToStateWithWarning(TimelineElementCategory.SCHEDULE_ANALOG_WORKFLOW, NotificationStatus.EFFECTIVE_DATE)
        ;

        // Viewed state
        this.fromState(NotificationStatus.VIEWED)
                .withTimelineGoToState(TimelineElementCategory.SEND_DIGITAL_DOMICILE, NotificationStatus.VIEWED)
                .withTimelineGoToState(TimelineElementCategory.SEND_DIGITAL_DOMICILE_FEEDBACK, NotificationStatus.VIEWED)
                .withTimelineGoToState(TimelineElementCategory.END_OF_DIGITAL_DELIVERY_WORKFLOW, NotificationStatus.VIEWED)
                .remainToStateWithWarning(TimelineElementCategory.NOTIFICATION_VIEWED, NotificationStatus.VIEWED)
                .withTimelineGoToState(TimelineElementCategory.PAYMENT, NotificationStatus.PAID)
                .remainToStateWithWarning(TimelineElementCategory.NOTIFICATION_PATH_CHOOSE, NotificationStatus.VIEWED)
                .remainToStateWithWarning(TimelineElementCategory.REQUEST_ACCEPTED, NotificationStatus.VIEWED)
                .withTimelineGoToState(TimelineElementCategory.REFINEMENT, NotificationStatus.VIEWED)
                .remainToStateWithWarning(TimelineElementCategory.SEND_DIGITAL_DOMICILE_FAILURE, NotificationStatus.VIEWED)
                .remainToStateWithWarning(TimelineElementCategory.SEND_PAPER_FEEDBACK, NotificationStatus.VIEWED)

                .remainToStateWithWarning(TimelineElementCategory.GET_ADDRESS, NotificationStatus.VIEWED)
                .remainToStateWithWarning(TimelineElementCategory.PUBLIC_REGISTRY_CALL, NotificationStatus.VIEWED)
                .remainToStateWithWarning(TimelineElementCategory.PUBLIC_REGISTRY_RESPONSE, NotificationStatus.VIEWED)
                .remainToStateWithWarning(TimelineElementCategory.SEND_DIGITAL_FEEDBACK, NotificationStatus.VIEWED)
                .remainToStateWithWarning(TimelineElementCategory.SEND_SIMPLE_REGISTERED_LETTER, NotificationStatus.VIEWED)
                .remainToStateWithWarning(TimelineElementCategory.DIGITAL_SUCCESS_WORKFLOW, NotificationStatus.VIEWED)
                .remainToStateWithWarning(TimelineElementCategory.DIGITAL_FAILURE_WORKFLOW, NotificationStatus.VIEWED)
                .remainToStateWithWarning(TimelineElementCategory.ANALOG_SUCCESS_WORKFLOW, NotificationStatus.VIEWED)
                .remainToStateWithWarning(TimelineElementCategory.SEND_COURTESY_MESSAGE, NotificationStatus.VIEWED)
                .remainToStateWithWarning(TimelineElementCategory.SCHEDULE_REFINEMENT, NotificationStatus.VIEWED)
                .remainToStateWithWarning(TimelineElementCategory.SCHEDULE_DIGITAL_WORKFLOW, NotificationStatus.VIEWED)
                .remainToStateWithWarning(TimelineElementCategory.SCHEDULE_ANALOG_WORKFLOW, NotificationStatus.VIEWED)
        ;

        // Paid state
        this.fromState(NotificationStatus.PAID)
                .withTimelineGoToState(TimelineElementCategory.SEND_DIGITAL_DOMICILE, NotificationStatus.PAID)
                .withTimelineGoToState(TimelineElementCategory.SEND_DIGITAL_DOMICILE_FEEDBACK, NotificationStatus.PAID)
                .withTimelineGoToState(TimelineElementCategory.END_OF_DIGITAL_DELIVERY_WORKFLOW, NotificationStatus.PAID)
                .remainToStateWithWarning(TimelineElementCategory.NOTIFICATION_VIEWED, NotificationStatus.PAID)
                .remainToStateWithWarning(TimelineElementCategory.PAYMENT, NotificationStatus.PAID)
                .remainToStateWithWarning(TimelineElementCategory.NOTIFICATION_PATH_CHOOSE, NotificationStatus.PAID)
                .remainToStateWithWarning(TimelineElementCategory.REQUEST_ACCEPTED, NotificationStatus.PAID)
                .withTimelineGoToState(TimelineElementCategory.REFINEMENT, NotificationStatus.PAID)
                .remainToStateWithWarning(TimelineElementCategory.SEND_DIGITAL_DOMICILE_FAILURE, NotificationStatus.PAID)
                .remainToStateWithWarning(TimelineElementCategory.SEND_PAPER_FEEDBACK, NotificationStatus.PAID)

                .remainToStateWithWarning(TimelineElementCategory.GET_ADDRESS, NotificationStatus.PAID)
                .remainToStateWithWarning(TimelineElementCategory.PUBLIC_REGISTRY_CALL, NotificationStatus.PAID)
                .remainToStateWithWarning(TimelineElementCategory.PUBLIC_REGISTRY_RESPONSE, NotificationStatus.PAID)
                .remainToStateWithWarning(TimelineElementCategory.SEND_DIGITAL_FEEDBACK, NotificationStatus.PAID)
                .remainToStateWithWarning(TimelineElementCategory.SEND_SIMPLE_REGISTERED_LETTER, NotificationStatus.PAID)
                .remainToStateWithWarning(TimelineElementCategory.DIGITAL_SUCCESS_WORKFLOW, NotificationStatus.PAID)
                .remainToStateWithWarning(TimelineElementCategory.DIGITAL_FAILURE_WORKFLOW, NotificationStatus.PAID)
                .remainToStateWithWarning(TimelineElementCategory.ANALOG_SUCCESS_WORKFLOW, NotificationStatus.PAID)
                .remainToStateWithWarning(TimelineElementCategory.SEND_COURTESY_MESSAGE, NotificationStatus.PAID)
                .remainToStateWithWarning(TimelineElementCategory.SCHEDULE_REFINEMENT, NotificationStatus.PAID)
                .remainToStateWithWarning(TimelineElementCategory.SCHEDULE_DIGITAL_WORKFLOW, NotificationStatus.PAID)
                .remainToStateWithWarning(TimelineElementCategory.SCHEDULE_ANALOG_WORKFLOW, NotificationStatus.PAID)
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
