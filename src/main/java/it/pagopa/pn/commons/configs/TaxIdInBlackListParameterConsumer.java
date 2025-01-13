package it.pagopa.pn.commons.configs;

import it.pagopa.pn.commons.abstractions.ParameterConsumer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import static it.pagopa.pn.commons.utils.LogUtils.maskTaxId;

@Slf4j
public class TaxIdInBlackListParameterConsumer {

    private final ParameterConsumer parameterConsumer;

    private static final String PARAMETER_STORE_MAP_TAX_ID_BLACK_LIST_NAME = "MapTaxIdBlackList";

    public TaxIdInBlackListParameterConsumer(ParameterConsumer parameterConsumer) {
        this.parameterConsumer = parameterConsumer;
    }

    public Boolean isInBlackList( String taxId ) {
        String maskedTaxId = maskTaxId(taxId);
        log.debug( "Start isInWhiteList for taxId={}", maskedTaxId);
        Optional<TaxIdInBlackList[]> optionalTaxIdIsInWhiteList = parameterConsumer.getParameterValue(PARAMETER_STORE_MAP_TAX_ID_BLACK_LIST_NAME, TaxIdInBlackList[].class);
        if (optionalTaxIdIsInWhiteList.isPresent()) {
            TaxIdInBlackList[] taxIdInWhiteListList = optionalTaxIdIsInWhiteList.get();
            for (TaxIdInBlackList taxIdInWhiteList : taxIdInWhiteListList) {
                if ( taxIdInWhiteList.taxId.equals( taxId ) ) {
                    log.debug("taxId={} is in white list", maskedTaxId );
                    return true;
                }
            }
        }
        log.debug("taxId={} not found in white list", maskedTaxId);
        return false;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class TaxIdInBlackList {
        String taxId;
    }
}
