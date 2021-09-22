package it.pagopa.pn.commons.abstractions;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface FileStorage {

    String putFileVersion(String key, InputStream body, long contentLength, Map<String, String> metadata);

    FileData getFileVersion(String key, String versionId);

    List<String> getDocumentsByPrefix(String prefix);
}
