package it.pagopa.pn.commons_delivery.middleware.notificationdao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import it.pagopa.pn.api.dto.notification.Notification;
import it.pagopa.pn.api.dto.notification.NotificationAttachment;
import it.pagopa.pn.api.dto.notification.NotificationPaymentInfo;
import it.pagopa.pn.api.dto.notification.NotificationRecipient;
import it.pagopa.pn.commons_delivery.model.notification.cassandra.NotificationEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

// FIXME: MapStruct do not play well with lombok. We have to find a solution.
//@Mapper( componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
@Component
public class DtoToEntityNotificationMapper {

    private final ObjectWriter recipientWriter;

    public DtoToEntityNotificationMapper(ObjectMapper objMapper) {
        this.recipientWriter = objMapper.writerFor(NotificationRecipient.class);
    }

    // FIXME: MapStruct do not play well with lombok. We have to find a solution.
    //@Mapping( target = "iun", source = "iun")
    //@Mapping( target = "paNotificationId", source = "paNotificationId")
    //@Mapping( target = "subject", source = "subject")
    //@Mapping( target = "cancelledIun", source = "cancelledIun")
    //@Mapping( target = "cancelledByIun", source = "cancelledByIun")
    //@Mapping( target = "senderPaId", source = "sender.paId")
    public NotificationEntity dto2Entity(Notification dto) {
        NotificationEntity.NotificationEntityBuilder builder = NotificationEntity.builder()
                .iun( dto.getIun() )
                .paNotificationId( dto.getPaNotificationId())
                .subject( dto.getSubject() )
                .sentAt( dto.getSentAt() )
                .cancelledIun( dto.getCancelledIun() )
                .cancelledByIun( dto.getCancelledByIun() )
                .senderPaId( dto.getSender().getPaId() )
                .recipientsJson( recipientList2json( dto.getRecipients() ))
                .recipientsOrder( dto.getRecipients().stream()
                        .map( NotificationRecipient::getTaxId )
                        .collect(Collectors.toList())
                    )
                .documentsKeys( listDocumentsKeys( dto.getDocuments() ))
                .documentsDigestsSha256( listDocumentsSha256( dto.getDocuments() ))
                .documentsVersionIds( listDocumentsVersionIds( dto.getDocuments() ))
                .documentsContentTypes( listDocumentsContentTypes( dto.getDocuments() ) )
                .documentsTitles( listDocumentsTitles( dto.getDocuments() ))
                .physicalCommunicationType (dto.getPhysicalCommunicationType() )
            ;

        NotificationPaymentInfo paymentInfo = dto.getPayment();
        fillBuilderWithPaymentInfo(builder, paymentInfo);

        return builder.build();
    }

    private List<String> listDocumentsContentTypes(List<NotificationAttachment> documents) {
        return documents.stream()
                .map(NotificationAttachment::getContentType)
                .collect( Collectors.toList() );
    }

    private List<String> listDocumentsKeys(List<NotificationAttachment> documents) {
        return documents.stream()
                .map( doc -> doc.getRef().getKey() )
                .collect(Collectors.toList());
    }

    private List<String> listDocumentsSha256(List<NotificationAttachment> documents) {
        return documents.stream()
                .map( doc -> doc.getDigests().getSha256() )
                .collect(Collectors.toList());
    }

    private List<String> listDocumentsVersionIds(List<NotificationAttachment> documents) {
        return documents.stream()
                .map( attachment -> attachment.getRef().getVersionToken() )
                .collect(Collectors.toList());
    }

    private List<String> listDocumentsTitles(List<NotificationAttachment> documents) {
        return documents.stream()
                .map( attachment -> attachment.getTitle() )
                .collect(Collectors.toList());
    }

    private void fillBuilderWithPaymentInfo(NotificationEntity.NotificationEntityBuilder builder, NotificationPaymentInfo paymentInfo) {
        if( paymentInfo != null ) {
            builder
                .iuv( paymentInfo.getIuv() )
                .notificationFeePolicy( paymentInfo.getNotificationFeePolicy() );

            if( paymentInfo.getF24() != null ) {

                NotificationAttachment flatRateF24 = paymentInfo.getF24().getFlatRate();
                if( flatRateF24 != null ) {
                    builder
                            .f24FlatRateDigestSha256( flatRateF24.getDigests().getSha256() )
                            .f24FlatRateKey( flatRateF24.getRef().getKey() )
                            .f24FlatRateVersionId( flatRateF24.getRef().getVersionToken() );
                }

                NotificationAttachment digitalF24 = paymentInfo.getF24().getDigital();
                if( digitalF24 != null ) {
                    builder
                            .f24DigitalDigestSha256( digitalF24.getDigests().getSha256() )
                            .f24DigitalKey( digitalF24.getRef().getKey() )
                            .f24DigitalVersionId( digitalF24.getRef().getVersionToken() );
                }

                NotificationAttachment analogF24 = paymentInfo.getF24().getAnalog();
                if( analogF24 != null ) {
                    builder
                            .f24AnalogDigestSha256( analogF24.getDigests().getSha256() )
                            .f24AnalogKey( analogF24.getRef().getKey() )
                            .f24AnalogVersionId( analogF24.getRef().getVersionToken() );
                }
            }
        }
    }

    private Map<String, String> recipientList2json(List<NotificationRecipient> recipients) {
        Map<String, String> result = new ConcurrentHashMap<>();
        recipients.forEach( recipient ->
            result.put( recipient.getTaxId(), recipient2JsonString( recipient ))
        );
        return result;
    }

    private String recipient2JsonString( NotificationRecipient recipient) {
        try {
            return recipientWriter.writeValueAsString( recipient );
        } catch (JsonProcessingException exc) {
            throw new IllegalStateException( exc );
        }
    }

}
