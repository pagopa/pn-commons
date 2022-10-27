package it.pagopa.pn.commons.configs;

import it.pagopa.pn.commons.abstractions.ParameterConsumer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.util.Optional;

public class IsMVPParameterConsumer {

    @Value("${pn.commons.features.is-mvp-default-value}")
    private Boolean isMVPDefaultValue;

    private final ParameterConsumer abstractCachedSsmParameterConsumer;

    private static final String PARAMETER_STORE_MAP_PA_NAME = "MapPaMVP";

    public IsMVPParameterConsumer(ParameterConsumer parameterConsumer) {
        this.abstractCachedSsmParameterConsumer = parameterConsumer;
    }

    public Boolean isMvp( String paTaxId ) {
        Optional<PaTaxIdIsMVP[]> optionalPaTaxIdIsMVPList = abstractCachedSsmParameterConsumer.getParameterValue(
                PARAMETER_STORE_MAP_PA_NAME, PaTaxIdIsMVP[].class );
        if (optionalPaTaxIdIsMVPList.isPresent() ) {
            PaTaxIdIsMVP[] paTaxIdIsMVPS = optionalPaTaxIdIsMVPList.get();
            for (PaTaxIdIsMVP paTaxIdIsMVP : paTaxIdIsMVPS) {
                if (paTaxIdIsMVP.paTaxId.equals(paTaxId)) {
                    return paTaxIdIsMVP.isMVP;
                }
            }
        }
        return isMVPDefaultValue;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class PaTaxIdIsMVP {
        String paTaxId;
        Boolean isMVP;
    }
}
