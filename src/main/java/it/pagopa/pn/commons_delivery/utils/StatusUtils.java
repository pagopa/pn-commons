package it.pagopa.pn.commons_delivery.utils;

import it.pagopa.pn.api.dto.notification.status.NotificationStatus;
import it.pagopa.pn.api.dto.notification.status.NotificationStatusHistoryElement;
import it.pagopa.pn.api.dto.notification.timeline.TimelineElement;
import it.pagopa.pn.api.dto.notification.timeline.TimelineElementCategory;

import java.time.Instant;
import java.util.*;

public class StatusUtils {

    private final StateMap stateMap = new StateMap();

    public NotificationStatus getCurrentStatus(List<NotificationStatusHistoryElement> statusHistory) {
        if (!statusHistory.isEmpty()) {
            return statusHistory.get(statusHistory.size() - 1).getStatus();
        } else {
            return NotificationStatus.RECEIVED;
        }
    }

    public List<NotificationStatusHistoryElement> getTimelineHistory( //
                                                                      List<TimelineElement> timelineElementList, //
                                                                      int numberOfRecipients, //
                                                                      Instant creationNotificationTimestamp //
    ) {
        List<NotificationStatusHistoryElement> timelineHistory = new ArrayList<>();
        NotificationStatus currentState = NotificationStatus.RECEIVED;
        List<TimelineElement> partialTimelineElementList = new ArrayList<>();

        for (TimelineElement timelineElement : timelineElementList) {
            partialTimelineElementList.add(timelineElement);
            TimelineElementCategory category = timelineElement.getCategory();
            NotificationStatus nextState = getNotificationStatus(currentState, category, partialTimelineElementList, numberOfRecipients);

            if (!Objects.equals(currentState, nextState)) {
                NotificationStatusHistoryElement statusHistoryElement = new NotificationStatusHistoryElement(nextState, timelineElement.getTimestamp());
                timelineHistory.add(statusHistoryElement);
            }
            currentState = nextState;
        }

        return timelineHistory;
    }

    private NotificationStatus getNotificationStatus(  //
                                                       NotificationStatus currentState, //
                                                       TimelineElementCategory timelineElementCategory, //
                                                       List<TimelineElement> timelineElementSubList, //
                                                       int numberOfRecipients //
    ) {
        NotificationStatus nextState;
        if (currentState.equals(NotificationStatus.DELIVERING) && timelineElementCategory.equals(TimelineElementCategory.END_OF_DIGITAL_DELIVERY_WORKFLOW)) {
            nextState = computeTransitionToDelivered(currentState, timelineElementCategory, timelineElementSubList, numberOfRecipients, stateMap);
        } else {
            nextState = stateMap.getStateTransition(currentState, timelineElementCategory);
        }
        return nextState;

    }

    private NotificationStatus computeTransitionToDelivered(NotificationStatus currentState, TimelineElementCategory timelineElementCategory, List<TimelineElement> timelineElementSubList, int numberOfRecipients, StateMap stateMap) {
        NotificationStatus nextState;
        long numberOfEndedDelivery = timelineElementSubList
                .stream()
                .filter(tl -> TimelineElementCategory.END_OF_DIGITAL_DELIVERY_WORKFLOW.equals(tl.getCategory()))
                .count();
        if (numberOfEndedDelivery == numberOfRecipients) {
            nextState = stateMap.getStateTransition(currentState, timelineElementCategory);
        } else {
            nextState = NotificationStatus.DELIVERING;
        }
        return nextState;
    }

}
