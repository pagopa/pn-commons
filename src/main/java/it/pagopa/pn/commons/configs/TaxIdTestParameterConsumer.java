package it.pagopa.pn.commons.configs;

import it.pagopa.pn.commons.abstractions.ParameterConsumer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import static it.pagopa.pn.commons.utils.LogUtils.maskTaxId;

@Slf4j
public class TaxIdTestParameterConsumer {

    private final ParameterConsumer parameterConsumer;

    private static final String PARAMETER_STORE_MAP_TAX_ID_TEST_NAME = "MapTaxIdTest";

    public TaxIdTestParameterConsumer(ParameterConsumer parameterConsumer) {
        this.parameterConsumer = parameterConsumer;
    }

    public Boolean isInWhiteList( String taxId ) {
        String maskTaxId = maskTaxId(taxId);
        log.debug( "Start isInWhiteList for taxId={}", maskTaxId);
        Optional<TaxIdIsInWhiteList[]> optionalTaxIdIsInWhiteList = parameterConsumer.getParameterValue(PARAMETER_STORE_MAP_TAX_ID_TEST_NAME, TaxIdIsInWhiteList[].class);
        if (optionalTaxIdIsInWhiteList.isPresent()) {
            TaxIdIsInWhiteList[] taxIdIsInWhiteListList = optionalTaxIdIsInWhiteList.get();
            for (TaxIdIsInWhiteList taxIdIsInWhiteList : taxIdIsInWhiteListList) {
                if ( taxIdIsInWhiteList.taxId.equals( taxId ) ) {
                    Boolean isInWhiteList = taxIdIsInWhiteList.isInWhiteList;
                    log.debug("taxId={} isInWhiteList={}", maskTaxId, isInWhiteList );
                    return isInWhiteList;
                }
            }
        }
        log.debug("taxId={} not found in white list", maskTaxId);
        return false;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class TaxIdIsInWhiteList {
        String taxId;
        Boolean isInWhiteList;
    }
}
