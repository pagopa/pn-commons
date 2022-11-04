package it.pagopa.pn.commons.configs;

import it.pagopa.pn.commons.abstractions.ParameterConsumer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.Optional;

@Slf4j
public class MVPParameterConsumer {

    @Value("${pn.commons.features.is-mvp-default-value}")
    private Boolean isMVPDefaultValue;

    private final ParameterConsumer parameterConsumer;

    private static final String PARAMETER_STORE_MAP_PA_NAME = "MapPaMVP";

    public MVPParameterConsumer(ParameterConsumer parameterConsumer) {
        this.parameterConsumer = parameterConsumer;
    }

    public Boolean isMvp( String paTaxId ) {
        log.debug("Start isMvp for paTaxId={}", paTaxId);
        
        Optional<PaTaxIdIsMVP[]> optionalPaTaxIdIsMVPList = parameterConsumer.getParameterValue(
                PARAMETER_STORE_MAP_PA_NAME, PaTaxIdIsMVP[].class );
        if (optionalPaTaxIdIsMVPList.isPresent() ) {
            PaTaxIdIsMVP[] paTaxIdIsMVPS = optionalPaTaxIdIsMVPList.get();
            for (PaTaxIdIsMVP paTaxIdIsMVP : paTaxIdIsMVPS) {
                if (paTaxIdIsMVP.paTaxId.equals(paTaxId)) {
                    Boolean isMVP = paTaxIdIsMVP.isMVP;
                    log.debug("paTaxId={} isMVP={}", paTaxId, isMVP);
                    return isMVP;
                }
            }
        }

        log.debug("paTaxId={} configuration not found, isMVPDefaultValue={}", paTaxId, isMVPDefaultValue);
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
