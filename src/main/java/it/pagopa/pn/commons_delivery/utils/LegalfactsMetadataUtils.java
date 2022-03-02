package it.pagopa.pn.commons_delivery.utils;

import it.pagopa.pn.api.dto.legalfacts.LegalFactType;
import it.pagopa.pn.api.dto.legalfacts.LegalFactsListEntry;
import it.pagopa.pn.api.dto.legalfacts.LegalFactsListEntryId;
import it.pagopa.pn.api.dto.notification.NotificationAttachment;
import it.pagopa.pn.commons.abstractions.FileData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class LegalfactsMetadataUtils {

    public static final String TYPE_METADATA_FIELD = "type";
    public static final String TAXID_METADATA_FIELD = "taxid";
    public static final String LEGAL_FACT_ID_SEPARATOR = "~";


    public Map<String, String> buildMetadata(LegalFactType type, String taxId) {
        Map<String, String> metadata = new HashMap<>();

        if (type != null) {
            metadata.put(TYPE_METADATA_FIELD, type.name());
        }
        if (StringUtils.isNotBlank(taxId)) {
            metadata.put(TAXID_METADATA_FIELD, taxId);
        }

        return metadata;
    }

    public String baseKey( String iun ) {
        return iun + "/legalfacts/";
    }

    public String fullKey( String iun, String legalFactKey ) {
        return baseKey( iun ) + legalFactKey + ".pdf";
    }

    public LegalFactsListEntry fromFileData(FileData fd) {
        String versionId = fd.getVersionId();

        String fullFileKey = fd.getKey();
        String iun = fullFileKey.replaceFirst("/.*", "");
        String legalFactName = fullFileKey.replaceFirst("^.*/(.*)\\.pdf$", "$1");

        Map<String, String> metadata = fd.getMetadata();
        String legalfactTypeString = metadata.get(TYPE_METADATA_FIELD);
        String taxId = metadata.get(TAXID_METADATA_FIELD);
        if (StringUtils.isBlank(taxId)) {
            taxId = null;
        }

        return LegalFactsListEntry.builder()
                .iun( iun )
                .legalFactsId( LegalFactsListEntryId.builder()
                        .key( legalFactName + "~" + versionId )
                        .type( LegalFactType.valueOf(legalfactTypeString) )
                        .build() )
                .taxId(taxId)
                .build();
    }

    public NotificationAttachment.Ref fromIunAndLegalFactId( String iun, String legalFactId ) {
        String[] parts = legalFactId.split(LEGAL_FACT_ID_SEPARATOR, 2);
        String legalFactkey = parts[0];
        String versionId = parts[1];

        return buildRef( iun, legalFactkey, versionId );
    }

    private NotificationAttachment.Ref buildRef( String iun, String legalFactKey, String versionId ) {
        return NotificationAttachment.Ref.builder()
                .key( fullKey( iun, legalFactKey ))
                .versionToken( versionId )
                .build();
    }






}
