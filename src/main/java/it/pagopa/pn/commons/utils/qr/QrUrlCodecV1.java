package it.pagopa.pn.commons.utils.qr;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.commons.abstractions.ParameterConsumer;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class QrUrlCodecV1 implements QrUrlCodec {
    ParameterConsumer parameterConsumer;
    QrUrlConfigs urlConfigs;
    ObjectMapper objectMapper;

    public QrUrlCodecV1(ParameterConsumer parameterConsumer, ObjectMapper objectMapper) {
        this.parameterConsumer = parameterConsumer;
        this.objectMapper = objectMapper;
    }

    @Override
    public String encode(String qrToken, UrlData urlData) {
        Optional<String> paramValue = parameterConsumer.getParameterValue("AARQrUrlConfigs",String.class);

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
        } catch (
            IOException e) {
        throw new RuntimeException("Errore nella deserializzazione della configurazione QR URL", e);
        }

        QrUrlConfigs urlConfigs = configsMap.get(this.getVersion().toString());

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
        // 1. Leggi il parametro dal parameter store e deserializza in una mappa
        Optional<String> paramValue = parameterConsumer.getParameterValue("AARQrUrlConfigs",String.class);
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
        } catch (
                IOException e) {
            throw new RuntimeException("Errore nella deserializzazione della configurazione QR URL", e);
        }

        // 2. Recupera la configurazione per la versione corrente
        QrUrlConfigs urlConfigs = configsMap.get(this.getVersion().toString());
        if (urlConfigs == null) {
            throw new IllegalArgumentException("No config found for version: " + this.getVersion());
        }

        // 3. Controlla se la URL è per PF o PG
        boolean isPfUrl = url.startsWith(urlConfigs.getDirectAccessUrlTemplatePhysical());
        boolean isPgUrl = url.startsWith(urlConfigs.getDirectAccessUrlTemplateLegal());
        if (!isPfUrl && !isPgUrl) {
            throw new IllegalArgumentException("Invalid url");
        }
        String prefix = isPfUrl ? urlConfigs.getDirectAccessUrlTemplatePhysical() : urlConfigs.getDirectAccessUrlTemplateLegal();

        // 4. Concatena il suffisso
        String urlWithSuffix = prefix + urlConfigs.getQuickAccessUrlAarDetailSuffix();

        // 5. Rimuovi la parte iniziale dalla url
        if (!url.startsWith(urlWithSuffix)) {
            throw new IllegalArgumentException("URL does not match expected pattern");
        }
        return url.substring(urlWithSuffix.length()+1); // +1 to remove the '=' character
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
