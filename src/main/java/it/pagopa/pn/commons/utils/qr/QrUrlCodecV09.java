package it.pagopa.pn.commons.utils.qr;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.commons.abstractions.ParameterConsumer;
import it.pagopa.pn.commons.utils.qr.models.Version;

public class QrUrlCodecV09 extends VersionedQrUrlCodec {
    public QrUrlCodecV09(ParameterConsumer parameterConsumer, ObjectMapper objectMapper) {
        super(parameterConsumer, objectMapper);
    }

    @Override
    public Version getVersion() {
        return new Version(0, 9, 0);
    }
}
