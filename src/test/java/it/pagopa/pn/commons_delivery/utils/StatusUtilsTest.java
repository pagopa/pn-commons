package it.pagopa.pn.commons_delivery.utils;

import it.pagopa.pn.api.dto.notification.status.NotificationStatus;
import it.pagopa.pn.api.dto.notification.status.NotificationStatusHistoryElement;
import it.pagopa.pn.api.dto.notification.timeline.TimelineElement;
import it.pagopa.pn.api.dto.notification.timeline.TimelineElementCategory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.*;

class StatusUtilsTest {
    private StatusUtils statusUtils;


    @BeforeEach
    public void setup() {
        this.statusUtils = new StatusUtils();
    }

    @Test
    void getTimelineHistoryTest() {
        // creare TimelineElement
        TimelineElement timelineElement1 = TimelineElement.builder()
                .timestamp(Instant.parse("2021-09-16T15:24:00.00Z"))
                .category(TimelineElementCategory.RECEIVED_ACK)
                .build();
        TimelineElement timelineElement2 = TimelineElement.builder()
                .timestamp(Instant.parse("2021-09-16T15:25:00.00Z"))
                .category(TimelineElementCategory.NOTIFICATION_PATH_CHOOSE)
                .build();
        TimelineElement timelineElement3 = TimelineElement.builder()
                .timestamp(Instant.parse("2021-09-16T15:26:00.00Z"))
                .category(TimelineElementCategory.SEND_DIGITAL_DOMICILE)
                .build();
        TimelineElement timelineElement4 = TimelineElement.builder()
                .timestamp(Instant.parse("2021-09-16T15:27:00.00Z"))
                .category(TimelineElementCategory.SEND_DIGITAL_DOMICILE_FEEDBACK)
                .build();
        TimelineElement timelineElement5 = TimelineElement.builder()
                .timestamp(Instant.parse("2021-09-16T15:28:00.00Z"))
                .category(TimelineElementCategory.END_OF_DIGITAL_DELIVERY_WORKFLOW)
                .build();
        TimelineElement timelineElement6 = TimelineElement.builder()
                .timestamp(Instant.parse("2021-09-16T17:00:00.00Z"))
                .category(TimelineElementCategory.NOTIFICATION_VIEWED)
                .build();
        TimelineElement timelineElement7 = TimelineElement.builder()
                .timestamp(Instant.parse("2021-09-16T17:30:00.00Z"))
                .category(TimelineElementCategory.PAYMENT)
                .build();

        // creare List<TimelineElement>
        Set<TimelineElement> timelineElementList = Set.of(timelineElement1, timelineElement2, timelineElement3,
                timelineElement4, timelineElement5, timelineElement6, timelineElement7);

        // creare List<NotificationStatusHistoryElement>
        NotificationStatusHistoryElement historyElement1 = NotificationStatusHistoryElement.builder()
                .status(NotificationStatus.DELIVERING)
                .activeFrom(Instant.parse("2021-09-16T15:25:00.00Z"))
                .build();
        NotificationStatusHistoryElement historyElement4 = NotificationStatusHistoryElement.builder()
                .status(NotificationStatus.DELIVERED)
                .activeFrom(Instant.parse("2021-09-16T15:28:00.00Z"))
                .build();
        NotificationStatusHistoryElement historyElement5 = NotificationStatusHistoryElement.builder()
                .status(NotificationStatus.VIEWED)
                .activeFrom(Instant.parse("2021-09-16T17:00:00.00Z"))
                .build();
        NotificationStatusHistoryElement historyElement6 = NotificationStatusHistoryElement.builder()
                .status(NotificationStatus.PAID)
                .activeFrom(Instant.parse("2021-09-16T17:30:00.00Z"))
                .build();
        List<NotificationStatusHistoryElement> historyElementList = Arrays.asList(historyElement1,
                historyElement4, historyElement5, historyElement6);


        // chiamare metodo di test
        List<NotificationStatusHistoryElement> resHistoryElementList = statusUtils.getStatusHistory(
                timelineElementList, 1,
                Instant.now()
        );
        // verificare che è risultato atteso
        Assertions.assertEquals(historyElementList, resHistoryElementList);
    }

    @Test
    void getTimelineHistoryMoreRecipientTest() {
        // creare TimelineElement
        TimelineElement timelineElement1 = TimelineElement.builder()
                .timestamp(Instant.parse("2021-09-16T15:24:00.00Z"))
                .category(TimelineElementCategory.RECEIVED_ACK)
                .build();
        TimelineElement timelineElement2 = TimelineElement.builder()
                .timestamp(Instant.parse("2021-09-16T15:25:00.00Z"))
                .category(TimelineElementCategory.NOTIFICATION_PATH_CHOOSE)
                .build();
        TimelineElement timelineElement3 = TimelineElement.builder()
                .timestamp(Instant.parse("2021-09-16T15:26:00.00Z"))
                .category(TimelineElementCategory.SEND_DIGITAL_DOMICILE)
                .build();
        TimelineElement timelineElement4 = TimelineElement.builder()
                .timestamp(Instant.parse("2021-09-16T15:27:00.00Z"))
                .category(TimelineElementCategory.SEND_DIGITAL_DOMICILE_FEEDBACK)
                .build();
        TimelineElement timelineElement5 = TimelineElement.builder()
                .timestamp(Instant.parse("2021-09-16T15:28:00.00Z"))
                .category(TimelineElementCategory.END_OF_DIGITAL_DELIVERY_WORKFLOW)
                .build();
        TimelineElement timelineElement3_1 = TimelineElement.builder()
                .timestamp(Instant.parse("2021-09-16T15:29:00.00Z"))
                .category(TimelineElementCategory.SEND_DIGITAL_DOMICILE)
                .build();
        TimelineElement timelineElement4_1 = TimelineElement.builder()
                .timestamp(Instant.parse("2021-09-16T15:30:00.00Z"))
                .category(TimelineElementCategory.SEND_DIGITAL_DOMICILE_FEEDBACK)
                .build();
        TimelineElement timelineElement5_1 = TimelineElement.builder()
                .timestamp(Instant.parse("2021-09-16T15:31:00.00Z"))
                .category(TimelineElementCategory.END_OF_DIGITAL_DELIVERY_WORKFLOW)
                .build();
        TimelineElement timelineElement6 = TimelineElement.builder()
                .timestamp(Instant.parse("2021-09-16T17:00:00.00Z"))
                .category(TimelineElementCategory.NOTIFICATION_VIEWED)
                .build();
        TimelineElement timelineElement7 = TimelineElement.builder()
                .timestamp(Instant.parse("2021-09-16T17:30:00.00Z"))
                .category(TimelineElementCategory.PAYMENT)
                .build();

        // creare List<TimelineElement>
        Set<TimelineElement> timelineElementList = Set.of(timelineElement1, timelineElement2,
                timelineElement3, timelineElement4, timelineElement5, timelineElement3_1, timelineElement4_1,
                timelineElement5_1, timelineElement6, timelineElement7);

        // creare List<NotificationStatusHistoryElement>
        NotificationStatusHistoryElement historyElement1 = NotificationStatusHistoryElement.builder()
                .status(NotificationStatus.DELIVERING)
                .activeFrom(Instant.parse("2021-09-16T15:25:00.00Z"))
                .build();
        NotificationStatusHistoryElement historyElement4_1 = NotificationStatusHistoryElement.builder()
                .status(NotificationStatus.DELIVERED)
                .activeFrom(Instant.parse("2021-09-16T15:31:00.00Z"))
                .build();
        NotificationStatusHistoryElement historyElement5 = NotificationStatusHistoryElement.builder()
                .status(NotificationStatus.VIEWED)
                .activeFrom(Instant.parse("2021-09-16T17:00:00.00Z"))
                .build();
        NotificationStatusHistoryElement historyElement6 = NotificationStatusHistoryElement.builder()
                .status(NotificationStatus.PAID)
                .activeFrom(Instant.parse("2021-09-16T17:30:00.00Z"))
                .build();
        List<NotificationStatusHistoryElement> historyElementList = Arrays.asList(
                historyElement1, historyElement4_1, historyElement5, historyElement6);

        // chiamare metodo di test
        List<NotificationStatusHistoryElement> resHistoryElementList = statusUtils.getStatusHistory(
                timelineElementList, 2,
                Instant.now()
        );
        // verificare che è risultato atteso
        Assertions.assertEquals(historyElementList, resHistoryElementList);
    }

    @Test
    void getTimelineHistoryErrorTest() {
        // creare TimelineElement
        TimelineElement timelineElement1 = TimelineElement.builder()
                .timestamp(Instant.parse("2021-09-16T15:24:00.00Z"))
                .category(TimelineElementCategory.RECEIVED_ACK)
                .build();
        TimelineElement timelineElement2 = TimelineElement.builder()
                .timestamp(Instant.parse("2021-09-16T15:25:00.00Z"))
                .category(TimelineElementCategory.NOTIFICATION_VIEWED)
                .build();
        TimelineElement timelineElement3 = TimelineElement.builder()
                .timestamp(Instant.parse("2021-09-16T15:26:00.00Z"))
                .category(TimelineElementCategory.PAYMENT)
                .build();

        Set<TimelineElement> timelineElementList = Set.of(timelineElement1,
                timelineElement2, timelineElement3);

        // creare List<NotificationStatusHistoryElement>
        NotificationStatusHistoryElement historyElement1 = NotificationStatusHistoryElement.builder()
                .status(NotificationStatus.RECEIVED)
                .activeFrom(Instant.parse("2021-09-16T15:25:00.00Z"))
                .build();

        List<NotificationStatusHistoryElement> historyElementList = Collections.emptyList();

        // chiamare metodo di test
        List<NotificationStatusHistoryElement> resHistoryElementList = statusUtils.getStatusHistory(
                timelineElementList, 2,
                Instant.now()
        );
        // verificare che è risultato atteso
        Assertions.assertEquals(historyElementList, resHistoryElementList);
    }

    @Test
    void emptyTimelineInitialStateTest() {
        //
        Assertions.assertEquals(NotificationStatus.RECEIVED, statusUtils.getCurrentStatus(Collections.emptyList()));
    }

    @Test
    void getCurrentStatusTest() {
        TimelineElement timelineElement1 = TimelineElement.builder()
                .timestamp(Instant.parse("2021-09-16T15:24:00.00Z"))
                .category(TimelineElementCategory.RECEIVED_ACK)
                .build();
        TimelineElement timelineElement2 = TimelineElement.builder()
                .timestamp(Instant.parse("2021-09-16T15:25:00.00Z"))
                .category(TimelineElementCategory.NOTIFICATION_PATH_CHOOSE)
                .build();
        TimelineElement timelineElement3 = TimelineElement.builder()
                .timestamp(Instant.parse("2021-09-16T15:26:00.00Z"))
                .category(TimelineElementCategory.SEND_DIGITAL_DOMICILE)
                .build();

        Set<TimelineElement> timelineElementList = Set.of(timelineElement1,
                timelineElement2, timelineElement3);

        List<NotificationStatusHistoryElement> resHistoryElementList = statusUtils.getStatusHistory(
                timelineElementList, 1, Instant.now());


        Assertions.assertEquals(NotificationStatus.DELIVERING, statusUtils.getCurrentStatus(resHistoryElementList));
    }


}
