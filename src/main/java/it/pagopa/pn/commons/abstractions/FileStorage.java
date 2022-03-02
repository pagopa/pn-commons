package it.pagopa.pn.commons.abstractions;

import it.pagopa.pn.api.dto.notification.NotificationAttachment;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface FileStorage {

    default String putFileVersion( FileData fileData ) {
        return this.putFileVersion(
                fileData.getKey(),
                fileData.getContent(),
                fileData.getContentLength(),
                fileData.getContentType(),
                fileData.getMetadata()
            );
    }

    String putFileVersion(String key, InputStream body, long contentLength, String contentType, Map<String, String> metadata);

    FileData getFileVersion(String key, String versionId);

    List<FileData> getDocumentsListing(String prefix);

    ResponseEntity<Resource> loadAttachment(NotificationAttachment.Ref attachmentRef);

    HttpHeaders headers();
}
