package it.pagopa.pn.commons.utils.qr;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.commons.abstractions.ParameterConsumer;
import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.commons.utils.qr.models.QrUrlConfigs;
import it.pagopa.pn.commons.utils.qr.models.RecipientTypeInt;
import it.pagopa.pn.commons.utils.qr.models.UrlData;
import it.pagopa.pn.commons.utils.qr.models.Version;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class QrUrlCodecV1 implements QrUrlCodec {
    ParameterConsumer parameterConsumer;
    ObjectMapper objectMapper;

    public QrUrlCodecV1(ParameterConsumer parameterConsumer, ObjectMapper objectMapper) {
        this.parameterConsumer = parameterConsumer;
        this.objectMapper = objectMapper;
    }

    @Override
    public String encode(String qrToken, UrlData urlData) {
        QrUrlConfigs urlConfigs = retrieveConfigs();

        String baseUrl;
        if (RecipientTypeInt.PF.equals(urlData.getRecipientType())) {
            baseUrl = urlConfigs.getDirectAccessUrlTemplatePhysical();
        } else if (RecipientTypeInt.PG.equals(urlData.getRecipientType())) {
            baseUrl = urlConfigs.getDirectAccessUrlTemplateLegal();
        } else {
            throw new IllegalArgumentException("RecipientType non valido");
        }

        String suffix = urlConfigs.getQuickAccessUrlAarDetailSuffix();

        return baseUrl + suffix + "=" + qrToken;
    }

    public String decode(String url) {
        QrUrlConfigs urlConfigs = retrieveConfigs();

        boolean isPfUrl = url.startsWith(urlConfigs.getDirectAccessUrlTemplatePhysical());
        boolean isPgUrl = url.startsWith(urlConfigs.getDirectAccessUrlTemplateLegal());
        if (!isPfUrl && !isPgUrl) {
            throw new IllegalArgumentException("Invalid url");
        }
        String prefix = isPfUrl ? urlConfigs.getDirectAccessUrlTemplatePhysical() : urlConfigs.getDirectAccessUrlTemplateLegal();

        String urlWithSuffix = prefix + urlConfigs.getQuickAccessUrlAarDetailSuffix();

        if (!url.startsWith(urlWithSuffix)) {
            throw new IllegalArgumentException("URL does not match expected pattern");
        }
        return url.substring(urlWithSuffix.length()+1); // +1 to remove the '=' character
    }

    /**
     * Recupera le configurazioni per la versione corrente del QR URL Codec.
     *
     * @return QrUrlConfigs per la versione corrente.
     * @throws IllegalArgumentException se il parametro non è trovato o se non esiste una configurazione per la versione corrente.
     * @throws PnInternalException se si verifica un errore durante la deserializzazione del parametro.
     *
     **/
    private QrUrlConfigs retrieveConfigs() {
        Optional<String> paramValue = parameterConsumer.getParameterValue(PARAMETER_NAME, String.class);
        Map<String, QrUrlConfigs> configsMap;
        try{
            if (paramValue.isEmpty()) {
                throw new IllegalArgumentException("Parameter AARQrUrlConfigs not found");
            } else {
                configsMap = objectMapper.readValue(
                        paramValue.get(),
                        new TypeReference<>() {}
                );
            }
        } catch (IOException e) {
            throw new PnInternalException("Error deserializing QR URL config", "DESERIALIZATION_ERROR", e);
        }


        QrUrlConfigs urlConfigs = configsMap.get(this.getVersion().toString());
        if (urlConfigs == null) {
            throw new IllegalArgumentException("No config found for version: " + this.getVersion());
        }

        return urlConfigs;
    }

    @Override
    public Version getVersion() {
        return new Version(1, 0, 0);
    }

    @Override
    public boolean canHandle(String url) {
        return url != null && url.contains(VERSION_MARKER) && VERSION_PATTERN.matcher(url).find();
    }
}
