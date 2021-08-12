package it.pagopa.pn.commons.abstractions.impl;

import it.pagopa.pn.commons.abstractions.FileStorage;
import it.pagopa.pn.commons.configs.RuntimeMode;
import it.pagopa.pn.commons.configs.aws.AwsConfigs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;

import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
//@ConditionalOnBean(S3Client.class) // FIXME rentrodurre
public class AwsS3FileStorage implements FileStorage {

    private final S3Client s3;
    private final AwsConfigs cfgs;

    public AwsS3FileStorage(S3Client s3, AwsConfigs cfgs, RuntimeMode runtimeMode) {
        this.s3 = s3;
        this.cfgs = cfgs;

        log.info("Starting {} service for bucker {} with runtime mode {}", this.getClass(),getBucketName(), runtimeMode );
        if( RuntimeMode.DEVELOPMENT.equals( runtimeMode ) ) {
            try {
                createBucket();
            } catch (RuntimeException exc ) {
                log.warn( "Creating development bucket", exc);
            }
        }
    }

    @Override
    public String putFileVersion(String key, InputStream body, long contentLength, Map<String, String> metadata) {
        String bucketName = getBucketName();

        PutObjectRequest putObjRequest = PutObjectRequest.builder()
                .bucket( bucketName )
                .key( key )
                .metadata( metadata )
                .build();

        PutObjectResponse response = s3.putObject(
                putObjRequest,
                RequestBody.fromInputStream( body, contentLength )
            );

        String versionId =  response.versionId();
        if( versionId == null ) {
            versionId = "";
        }
        return versionId;
    }

    @Override
    public InputStream getFileVersionBody(String key, String versionId) {
        throw new UnsupportedOperationException("NOT YET IMPL");
    }

    @Override
    public Map<String, String> getFileVersionMetadata(String key, String versionId) {
        throw new UnsupportedOperationException("NOT YET IMPL");
    }

    private void createBucket() {
        String bucketName = getBucketName();
        log.info("Creating bucket {}", bucketName);

        // - Require bucket creation
        CreateBucketRequest bucketRequest = CreateBucketRequest.builder()
                .bucket( bucketName )
                .objectLockEnabledForBucket( true )
                .build();
        s3.createBucket( bucketRequest );

        // - wait for creation end
        HeadBucketRequest bucketRequestWait = HeadBucketRequest.builder()
                .bucket( bucketName )
                .build();

        S3Waiter s3Waiter = s3.waiter();
        WaiterResponse<HeadBucketResponse> waiterResponse = s3Waiter.waitUntilBucketExists( bucketRequestWait );

        Optional<Throwable> bucketCreationException = waiterResponse.matched().exception();
        if( bucketCreationException.isPresent() ) {
            throw new IllegalStateException( bucketCreationException.get() );
        }
    }

    private String getBucketName() {
        String bucketName = this.cfgs.getBucketName();
        return bucketName;
    }

}
