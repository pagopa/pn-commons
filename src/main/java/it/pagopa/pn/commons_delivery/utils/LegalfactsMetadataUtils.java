package it.pagopa.pn.commons_delivery.utils;

import it.pagopa.pn.api.dto.legalfacts.LegalFactType;
import it.pagopa.pn.api.dto.legalfacts.LegalFactsListEntry;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class LegalfactsMetadataUtils {

    public static final String TYPE_METADATA_FIELD = "type";
    public static final String TAXID_METADATA_FIELD = "taxid";


    public Map<String, String> buildMetadata(LegalFactType type, String taxId) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", "application/pdf");

        if (type != null) {
            metadata.put(TYPE_METADATA_FIELD, type.name());
        }
        if (StringUtils.isNotBlank(taxId)) {
            metadata.put(TAXID_METADATA_FIELD, taxId);
        }

        return metadata;
    }


    public LegalFactsListEntry fromMetadata(String id, Map<String, String> metadata) {
        String legalfactTypeString = metadata.get(TYPE_METADATA_FIELD);

        String taxId = metadata.get(TAXID_METADATA_FIELD);
        if (StringUtils.isBlank(taxId)) {
            taxId = null;
        }

        return LegalFactsListEntry.builder()
                .id(id.replaceFirst(".*/([^/]*)", "$1"))
                .taxId(taxId)
                .type(LegalFactType.valueOf(legalfactTypeString))
                .build();
    }

}
