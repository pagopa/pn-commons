package it.pagopa.pn.commons_delivery.middleware.notificationdao;

import it.pagopa.pn.api.dto.notification.*;
import it.pagopa.pn.api.dto.notification.address.DigitalAddress;
import it.pagopa.pn.api.dto.notification.address.DigitalAddressType;
import it.pagopa.pn.commons.abstractions.IdConflictException;
import it.pagopa.pn.commons_delivery.middleware.NotificationDao;
import org.junit.jupiter.api.Assertions;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

abstract class AbstractNotificationDaoTest {

    protected NotificationDao dao;

    abstract void instantiateDao();

    void insertSuccessWithoutPayments() throws IdConflictException {

        // GIVEN
        Notification notification = newNotificationWithoutPayments( );

        // WHEN
        this.dao.addNotification( notification );

        // THEN
        Optional<Notification> saved = this.dao.getNotificationByIun( notification.getIun() );
        Assertions.assertTrue( saved.isPresent() );
        Assertions.assertEquals( notification, saved.get() );
    }

    void insertSuccessWithPaymentsDeliveryMode() throws IdConflictException {

        // GIVEN
        Notification notification = newNotificationWithPaymentsDeliveryMode( true );

        // WHEN
        this.dao.addNotification( notification );

        // THEN
        Optional<Notification> saved = this.dao.getNotificationByIun( notification.getIun() );
        Assertions.assertTrue( saved.isPresent() );
        Assertions.assertEquals( notification, saved.get() );
    }

    void insertSuccessWithPaymentsFlat() throws IdConflictException {

        // GIVEN
        Notification notification = newNotificationWithPaymentsFlat( );

        // WHEN
        this.dao.addNotification( notification );

        // THEN
        Optional<Notification> saved = this.dao.getNotificationByIun( notification.getIun() );
        Assertions.assertTrue( saved.isPresent() );
        Assertions.assertEquals( notification, saved.get() );
    }

    void insertSuccessWithPaymentsIuvOnly() throws IdConflictException {

        // GIVEN
        Notification notification = newNotificationWithPaymentsIuvOnly( );

        // WHEN
        this.dao.addNotification( notification );

        // THEN
        Optional<Notification> saved = this.dao.getNotificationByIun( notification.getIun() );
        Assertions.assertTrue( saved.isPresent() );
        Assertions.assertEquals( notification, saved.get() );
    }

    void insertSuccessWithPaymentsNoIuv() throws IdConflictException {

        // GIVEN
        Notification notification = newNotificationWithPaymentsDeliveryMode( false );

        // WHEN
        this.dao.addNotification( notification );

        // THEN
        Optional<Notification> saved = this.dao.getNotificationByIun( notification.getIun() );
        Assertions.assertTrue( saved.isPresent() );
        Assertions.assertEquals( notification, saved.get() );
    }



    void insertFailForIunConflict() throws IdConflictException {

        // GIVEN
        Notification notification = newNotificationWithoutPayments( );

        // WHEN
        this.dao.addNotification( notification );

        // THEN
        Assertions.assertThrows( IdConflictException.class, () ->
            this.dao.addNotification( notification )
        );
    }



    private Notification newNotificationWithoutPayments() {
        return Notification.builder()
                .iun("IUN_01")
                .paNotificationId("protocol_01")
                .subject("Subject 01")
                .cancelledByIun("IUN_05")
                .cancelledIun("IUN_00")
                .sender(NotificationSender.builder()
                        .paId(" pa_02")
                        .build()
                )
                .recipients( Collections.singletonList(
                        NotificationRecipient.builder()
                                .taxId("Codice Fiscale 01")
                                .denomination("Nome Cognome/Ragione Sociale")
                                .digitalDomicile(DigitalAddress.builder()
                                        .type(DigitalAddressType.PEC)
                                        .address("account@dominio.it")
                                        .build())
                                .build()
                ))
                .documents(Arrays.asList(
                        NotificationAttachment.builder()
                                .ref( NotificationAttachment.Ref.builder()
                                        .key("key_doc00")
                                        .versionToken("v01_doc00")
                                        .build()
                                )
                                .digests(NotificationAttachment.Digests.builder()
                                        .sha256("sha256_doc00")
                                        .build()
                                )
                                .build(),
                        NotificationAttachment.builder()
                                .ref( NotificationAttachment.Ref.builder()
                                        .key("key_doc01")
                                        .versionToken("v01_doc01")
                                        .build()
                                )
                                .digests(NotificationAttachment.Digests.builder()
                                        .sha256("sha256_doc01")
                                        .build()
                                )
                                .build()
                ))
                .build();
    }

    private Notification newNotificationWithPaymentsDeliveryMode( boolean withIuv ) {
        return newNotificationWithoutPayments().toBuilder()
                .payment( NotificationPaymentInfo.builder()
                        .iuv( withIuv ? "iuv01" : null )
                        .notificationFeePolicy( NotificationPaymentInfoFeePolicies.DELIVERY_MODE )
                        .f24( NotificationPaymentInfo.F24.builder()
                                .digital( NotificationAttachment.builder()
                                        .ref( NotificationAttachment.Ref.builder()
                                                .key("key_F24dig")
                                                .versionToken("v01_F24dig")
                                                .build()
                                        )
                                        .digests( NotificationAttachment.Digests.builder()
                                                .sha256("sha__F24dig")
                                                .build()
                                        )
                                        .build()
                                )
                                .analog( NotificationAttachment.builder()
                                        .ref( NotificationAttachment.Ref.builder()
                                                .key("key_F24anag")
                                                .versionToken("v01_F24anag")
                                                .build()
                                        )
                                        .digests( NotificationAttachment.Digests.builder()
                                                .sha256("sha__F24anag")
                                                .build()
                                        )
                                        .build()
                                )
                                .build()
                        )
                        .build()
                )
                .build();
    }

    private Notification newNotificationWithPaymentsFlat() {
        return newNotificationWithoutPayments( ).toBuilder()
                .payment( NotificationPaymentInfo.builder()
                        .iuv( "IUV_01" )
                        .notificationFeePolicy( NotificationPaymentInfoFeePolicies.FLAT_RATE )
                        .f24( NotificationPaymentInfo.F24.builder()
                                .flatRate( NotificationAttachment.builder()
                                        .ref( NotificationAttachment.Ref.builder()
                                                .key("key_F24flat")
                                                .versionToken("v01_F24flat")
                                                .build()
                                        )
                                        .digests( NotificationAttachment.Digests.builder()
                                                .sha256("sha__F24flat")
                                                .build()
                                        )
                                        .build()
                                )
                                .build()
                        )
                        .build()
                )
                .build();
    }

    private Notification newNotificationWithPaymentsIuvOnly() {
        return newNotificationWithoutPayments().toBuilder()
                .payment( NotificationPaymentInfo.builder()
                        .iuv( "IUV_01" )
                        .build()
                )
                .build();
    }

}
