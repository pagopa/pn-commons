package it.pagopa.pn.commons.utils.qr;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.commons.abstractions.ParameterConsumer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QrUrlCodecService {
    private final QrUrlCodecRegistry qrUrlCodecRegistry;

    public QrUrlCodecService(ParameterConsumer parameterConsumer, ObjectMapper objectMapper) {
        this.qrUrlCodecRegistry = initializeRegistry(parameterConsumer, objectMapper);
    }

    private QrUrlCodecRegistry initializeRegistry(ParameterConsumer parameterConsumer, ObjectMapper objectMapper) {
        QrUrlCodecRegistry registry = new QrUrlCodecRegistry();
        // Qui vanno registrati i codec per le versioni supportate
        registry.register(new QrUrlCodecV1(parameterConsumer, objectMapper));
        //registry.register(new QrUrlCodecV2(parameterConsumer));
        return registry;
    }

    public String encode(String qrToken, UrlData urlData) {
        QrUrlCodec qrUrlCodec = qrUrlCodecRegistry.getDefaultCodec();
        log.debug("Using default QrUrlCodec: {}", qrUrlCodec.getVersion());
        return qrUrlCodec.encode(qrToken, urlData);
    }

    public String decode(String url) throws IllegalArgumentException {
        QrUrlCodec qrUrlCodec = qrUrlCodecRegistry.getDefaultCodec();
        log.debug("Using QrUrlCodec: {}", qrUrlCodec.getVersion());
        return qrUrlCodec.decode(url);
    }
}
