package it.pagopa.pn.commons.configs;

import it.pagopa.pn.commons.abstractions.impl.AbstractCachedSsmParameterConsumer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

public class IsMVPParameterConsumer {
    private final AbstractCachedSsmParameterConsumer abstractCachedSsmParameterConsumer;

    private static final String PARAMETER_STORE_MAP_PA_NAME = "MapPaMVP";

    public IsMVPParameterConsumer(AbstractCachedSsmParameterConsumer abstractCachedSsmParameterConsumer) {
        this.abstractCachedSsmParameterConsumer = abstractCachedSsmParameterConsumer;
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
        return false;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class PaTaxIdIsMVP {
        String paTaxId;
        Boolean isMVP;
    }
}
