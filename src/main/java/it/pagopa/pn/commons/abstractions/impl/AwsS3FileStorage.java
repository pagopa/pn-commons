package it.pagopa.pn.commons.abstractions.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

import org.springframework.core.io.InputStreamResource;

import it.pagopa.pn.commons.abstractions.FileData;
import it.pagopa.pn.commons.abstractions.FileStorage;
import it.pagopa.pn.commons.configs.RuntimeMode;
import it.pagopa.pn.commons.configs.aws.AwsConfigs;
import it.pagopa.pn.commons.exceptions.PnInternalException;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.Response;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;

@Slf4j
public class AwsS3FileStorage implements FileStorage {

    private final S3Client s3;
    private final AwsConfigs cfgs;

    public AwsS3FileStorage(S3Client s3, AwsConfigs cfgs, RuntimeMode runtimeMode) {
        this.s3 = s3;
        this.cfgs = cfgs;

        log.info("Starting {} service for bucket {} with runtime mode {}", this.getClass(),getBucketName(), runtimeMode );
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
        return this.cfgs.getBucketName();
    }

	@Override
	public FileData getFileByKey(String key) {
		GetObjectRequest s3ObjectRequest = GetObjectRequest.builder()
				.bucket( getBucketName() )
				.key( key )
				.build();

		ResponseInputStream<GetObjectResponse> s3Object = s3.getObject( s3ObjectRequest );
	    
	    GetObjectResponse response = s3Object.response();
	    
	    return FileData.builder()
	    				.content( s3Object )
	    				.contentLength( response.contentLength() )
	    				.metadata ( response.metadata() )
	    				.build();
	}

}
