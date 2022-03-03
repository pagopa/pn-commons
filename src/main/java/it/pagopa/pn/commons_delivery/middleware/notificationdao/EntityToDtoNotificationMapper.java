package it.pagopa.pn.commons_delivery.middleware.notificationdao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import it.pagopa.pn.api.dto.notification.*;
import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.commons_delivery.model.notification.cassandra.NotificationEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//@Mapper( componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
@Component
public class EntityToDtoNotificationMapper {

    private final ObjectReader recipientReader;

    public EntityToDtoNotificationMapper(ObjectMapper objMapper) {
        this.recipientReader = objMapper.readerFor( NotificationRecipient.class );
    }

    public Notification entity2Dto(NotificationEntity entity) {
    	if ( entity.getPhysicalCommunicationType() == null ) {
            throw new PnInternalException(" Notification entity with iun " + entity.getIun() + " hash invalid physicalCommunicationType value");
        }

        Notification.NotificationBuilder builder = Notification.builder()
                .iun( entity.getIun() )
                .subject( entity.getSubject() )
                .sentAt( entity.getSentAt() )
                .paNotificationId( entity.getPaNotificationId() )
                .cancelledByIun( entity.getCancelledByIun() )
                .cancelledIun( entity.getCancelledIun() )
                .physicalCommunicationType( entity.getPhysicalCommunicationType() )
                
                .sender( NotificationSender.builder()
                        .paId( entity.getSenderPaId() )
                        .build()
                    )
                .recipients( buildRecipientsList( entity ) )
                .documents( buildDocumentsList( entity ) );

        boolean anyPaymentFieldNotNull = Stream.of(
                    entity.getIuv(), entity.getNotificationFeePolicy(),
                    entity.getF24AnalogDigestSha256(), entity.getF24AnalogVersionId(),
                    entity.getF24DigitalDigestSha256(), entity.getF24DigitalVersionId(),
                    entity.getF24FlatRateDigestSha256(), entity.getF24FlatRateVersionId()
                )
                .anyMatch( Objects::nonNull );
        if ( anyPaymentFieldNotNull ) {
            builder.payment( buildNotificationPaymentInfo( entity ) );
        }

        return builder.build();
    }

    private NotificationPaymentInfo buildNotificationPaymentInfo(NotificationEntity entity) {

        NotificationPaymentInfo.NotificationPaymentInfoBuilder builder =  NotificationPaymentInfo.builder()
                .iuv( entity.getIuv() )
                .notificationFeePolicy( entity.getNotificationFeePolicy() )
                ;

        NotificationAttachment f24Digital = buildAttachment( entity.getF24DigitalKey(),
                             entity.getF24DigitalVersionId(), entity.getF24DigitalDigestSha256() );
        NotificationAttachment f24Analog = buildAttachment( entity.getF24AnalogKey(),
                             entity.getF24AnalogVersionId(), entity.getF24AnalogDigestSha256() );
        NotificationAttachment f24FlatRate = buildAttachment( entity.getF24FlatRateKey(),
                           entity.getF24FlatRateVersionId(), entity.getF24FlatRateDigestSha256() );

        boolean anyF24moduleNotNull = Stream.of( f24Analog, f24Digital, f24FlatRate )
                .anyMatch( Objects::nonNull );

        if( anyF24moduleNotNull ) {
            builder.f24( NotificationPaymentInfo.F24.builder()
                    .analog( f24Analog )
                    .digital( f24Digital )
                    .flatRate( f24FlatRate )
                    .build()
            );
        }

        return builder.build();
    }

    private NotificationAttachment buildAttachment(String key, String version, String sha256 ) {
        NotificationAttachment result;
        if ( StringUtils.isAllBlank( key, version, sha256 ) ) {
            result = null;
        }
        else if ( version != null && StringUtils.isNotBlank( sha256 ) && StringUtils.isNotBlank( key ) ) {
            result = NotificationAttachment.builder()
                    .ref( NotificationAttachment.Ref.builder()
                            .key( key )
                            .versionToken( version )
                            .build()
                    )
                    .digests( NotificationAttachment.Digests.builder()
                            .sha256( sha256 )
                            .build()
                    )
                    .build();
        }
        else {
            throw new PnInternalException( "Error key (" + key + ") version (" + version + ") and sha256 (" + sha256 + ") are both required or both blank" );
        }
        return result;
    }

    private NotificationAttachment buildAttachment(String key, String version, String sha256, String contentType, String title ) {
         NotificationAttachment notificationAttachment = buildAttachment(key, version, sha256);
         if ( notificationAttachment != null ){
             notificationAttachment = notificationAttachment.toBuilder()
                     .contentType(contentType)
                     .title( title )
                     .build();
         }
        return notificationAttachment;
    }

    private List<NotificationAttachment> buildDocumentsList( NotificationEntity entity ) {
        List<String> documentsDigestsSha256 = entity.getDocumentsDigestsSha256();
        List<String> documentsKeys = entity.getDocumentsKeys();
        List<String> documentsVersionIds = entity.getDocumentsVersionIds();
        List<String> documentsContentTypes = entity.getDocumentsContentTypes();
        List<String> documentsTitles = entity.getDocumentsTitles();

        int lengthShas = documentsDigestsSha256 == null ? 0 : documentsDigestsSha256.size();
        int lengthKeys = documentsKeys == null ? 0 : documentsKeys.size();
        int lengthVersionIds = documentsVersionIds == null ? 0 : documentsVersionIds.size();
        int lengthContentTypes = documentsContentTypes == null ? 0 : documentsContentTypes.size();
        int lengthTitles = documentsTitles == null ? 0 : documentsTitles.size();
        if ( lengthShas != lengthKeys || lengthKeys != lengthVersionIds || lengthVersionIds != lengthContentTypes || lengthTitles != lengthContentTypes  ) {
            throw new PnInternalException(" Notification entity with iun " + entity.getIun() +
                    " hash different quantity of document versions, sha256s, keys, content types and titles");
        }

        // - Three different list with one information each instead of a list of object:
        //   AWS keyspace do not support UDT
        List<NotificationAttachment> result = new ArrayList<>();
        for( int d = 0; d < lengthShas; d += 1 ) {
            NotificationAttachment notificationAttachment = buildAttachment(
                    documentsKeys.get( d ),
                    documentsVersionIds.get( d ),
                    documentsDigestsSha256.get( d ),
                    documentsContentTypes.get( d ),
                    documentsTitles.get( d )
                );
            result.add( notificationAttachment );
        }

        return result;
    }

    private List<NotificationRecipient> buildRecipientsList( NotificationEntity entity ) {
        Map<String, String> recipientsMetadata = entity.getRecipientsJson();

        return entity.getRecipientsOrder().stream()
                .map( recipientId -> parseRecipientJson( recipientsMetadata.get( recipientId)) )
                .collect(Collectors.toList());

    }

    private NotificationRecipient parseRecipientJson( String jsonString ) {
        try {
            return this.recipientReader.readValue( jsonString );
        } catch (JsonProcessingException exc) {
            throw new PnInternalException( "Parsing cassandra stored json", exc );
        }
    }
}
