package it.pagopa.pn.commons.abstractions;

import java.io.InputStream;
import java.util.Map;

public interface FileStorage {

    String putFileVersion(String key, InputStream body, long contentLength, Map<String, String> metadata);

    InputStream getFileVersionBody(String key, String versionId);

    Map<String, String> getFileVersionMetadata(String key, String versionId);
}
