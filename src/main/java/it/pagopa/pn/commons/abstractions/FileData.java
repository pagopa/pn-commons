package it.pagopa.pn.commons.abstractions;

import java.io.InputStream;
import java.util.Map;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class FileData {
	private final InputStream content;
	private final long contentLength;
	private final Map<String, String> metadata;
	
}