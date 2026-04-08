package it.pagopa.pn.commons.utils.qr;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.commons.abstractions.ParameterConsumer;
import it.pagopa.pn.commons.utils.qr.models.Version;

public class QrUrlCodecV1 extends VersionedQrUrlCodec {
    public QrUrlCodecV1(ParameterConsumer parameterConsumer, ObjectMapper objectMapper) {
        super(parameterConsumer, objectMapper);
    }

    @Override
    public Version getVersion() {
        return new Version(1, 0, 0);
    }
}
