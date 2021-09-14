package it.pagopa.pn.commons.abstractions;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Object;

public interface FileStorage {

    String putFileVersion(String key, InputStream body, long contentLength, Map<String, String> metadata);

    InputStream getFileVersionBody(String key, String versionId);

    Map<String, String> getFileVersionMetadata(String key, String versionId);
    
    List<S3Object> getFilesByKeyPrefix(String keyPrefix);
    
    ResponseInputStream<GetObjectResponse> getFileByKey(String key);
}
