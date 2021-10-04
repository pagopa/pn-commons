package it.pagopa.pn.commons.abstractions;

import java.io.InputStream;
import java.util.Map;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class FileData {
	private final String key;
	private final InputStream content;
	private final String contentType;
	private final long contentLength;
	private final String versionId;
	private final Map<String, String> metadata;
}