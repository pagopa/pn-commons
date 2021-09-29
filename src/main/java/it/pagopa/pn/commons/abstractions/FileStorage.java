package it.pagopa.pn.commons.abstractions;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface FileStorage {

    default String putFileVersion( FileData fileData ) {
        return this.putFileVersion( fileData.getKey(), fileData.getContent(), fileData.getContentLength(), fileData.getMetadata() );
    }

    String putFileVersion(String key, InputStream body, long contentLength, Map<String, String> metadata);

    FileData getFileVersion(String key, String versionId);

    List<FileData> getDocumentsListing(String prefix);
}
