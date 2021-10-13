package it.pagopa.pn.commons.abstractions.impl;

import it.pagopa.pn.commons.abstractions.FileData;
import it.pagopa.pn.commons.abstractions.FileStorage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

@SpringBootTest()
@ActiveProfiles("test")
class AwsS3FileStorageTestIT {

    private final static String TEST_KEY = "testKey";

    @Autowired
    private FileStorage fileStore;

    @Test
    void loadDownloadAndCompare() throws IOException {
        InputStream inputStream = getInputStream();

        String versionId = fileStore.putFileVersion(
                TEST_KEY,
                inputStream,
                517514l,
                "image/jpeg",
                Collections.emptyMap()
            );

        FileData download = fileStore.getFileVersion( TEST_KEY, versionId );

        byte[] expected = StreamUtils.copyToByteArray( getInputStream() );
        byte[] actual = StreamUtils.copyToByteArray( download.getContent() );

        Assertions.assertArrayEquals( expected, actual );
    }

    private InputStream getInputStream() {
        InputStream inputStream = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream("autunno.jpg");
        return inputStream;
    }

}
