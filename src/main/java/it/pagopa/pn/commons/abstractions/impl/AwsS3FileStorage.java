package it.pagopa.pn.commons.abstractions.impl;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import it.pagopa.pn.api.dto.notification.NotificationAttachment;
import org.apache.commons.lang3.StringUtils;

import it.pagopa.pn.commons.abstractions.FileData;
import it.pagopa.pn.commons.abstractions.FileStorage;
import it.pagopa.pn.commons.configs.RuntimeMode;
import it.pagopa.pn.commons.configs.aws.AwsConfigs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
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
    public String putFileVersion(String key, InputStream body, long contentLength, String contentType, Map<String, String> metadata) {
        String bucketName = getBucketName();

        PutObjectRequest putObjRequest = PutObjectRequest.builder()
                .bucket( bucketName )
                .key( key )
                .contentType( contentType )
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
    public FileData getFileVersion(String key, String versionId) {
        GetObjectRequest s3ObjectRequest = GetObjectRequest.builder()
                .bucket( getBucketName() )
                .key( key )
                .versionId( StringUtils.isNotBlank( versionId) ? versionId : null )
                .build();

        ResponseInputStream<GetObjectResponse> s3Object = s3.getObject( s3ObjectRequest );

        GetObjectResponse response = s3Object.response();

        return FileData.builder()
                .key( key )
                .versionId(response.versionId() )
                .content( s3Object )
                .contentLength( response.contentLength() )
                .contentType( response.contentType() )
                .metadata ( response.metadata() )
                .build();
    }
    
    @Override
    public List<FileData> getDocumentsListing(String prefix) {
    	ListObjectsV2Response result = s3.listObjectsV2(ListObjectsV2Request.builder()
        													.bucket( getBucketName() )
        													.prefix( prefix )
        													.build());
        return result.contents().stream().map( s3Obj -> {
                HeadObjectResponse head = loadMetadata( s3Obj );
                return FileData.builder()
                    .key( s3Obj.key() )
                    .versionId( head.versionId() )
                    .contentLength(head.contentLength() )
                    .contentType(head.contentType() )
                    .metadata( head.metadata() )
                    .build();
            }).collect(Collectors.toList());

    }

    @Override
    public HttpHeaders headers() {
            HttpHeaders headers = new HttpHeaders();
            headers.add( "Cache-Control", "no-cache, no-store, must-revalidate" );
            headers.add( "Pragma", "no-cache" );
            headers.add( "Expires", "0" );
            return headers;
    }

    public ResponseEntity<Resource> loadAttachment(NotificationAttachment.Ref attachmentRef) {
        String attachmentKey = attachmentRef.getKey();
        String savedVersionId = attachmentRef.getVersionToken();

        FileData fileData = this.getFileVersion( attachmentKey, savedVersionId );

        ResponseEntity<Resource> response = ResponseEntity.ok()
                .headers( this.headers() )
                .contentLength( fileData.getContentLength() )
                .contentType( extractMediaType( fileData.getContentType() ) )
                .body( new InputStreamResource(fileData.getContent() ) );

        log.debug("AttachmentKey: response={}", response);
        return response;
    }

    private MediaType extractMediaType(String contentType ) {
        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;

        try {
            if ( StringUtils.isNotBlank( contentType ) ) {
                mediaType = MediaType.parseMediaType( contentType );
            }
        } catch (InvalidMediaTypeException exc)  {
            // using default
        }
        return mediaType;
    }

    private HeadObjectResponse loadMetadata( S3Object s3Obj ) {
        HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                .bucket( this.getBucketName() )
                .key(s3Obj.key() )
                .build();

        return s3.headObject(headObjectRequest);
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

}
