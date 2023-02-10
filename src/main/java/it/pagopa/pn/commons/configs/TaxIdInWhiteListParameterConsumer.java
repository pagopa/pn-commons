package it.pagopa.pn.commons.configs;

import it.pagopa.pn.commons.abstractions.ParameterConsumer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static it.pagopa.pn.commons.utils.LogUtils.maskTaxId;

@Slf4j
@Component
public class TaxIdInWhiteListParameterConsumer {

    private final ParameterConsumer parameterConsumer;

    private static final String PARAMETER_STORE_MAP_TAX_ID_WHITE_LIST_NAME = "MapTaxIdWhiteList";

    public TaxIdInWhiteListParameterConsumer(ParameterConsumer parameterConsumer) {
        this.parameterConsumer = parameterConsumer;
    }

    public Boolean isInWhiteList( String taxId ) {
        String maskedTaxId = maskTaxId(taxId);
        log.debug( "Start isInWhiteList for taxId={}", maskedTaxId);
        Optional<TaxIdInWhiteList[]> optionalTaxIdIsInWhiteList = parameterConsumer.getParameterValue(PARAMETER_STORE_MAP_TAX_ID_WHITE_LIST_NAME, TaxIdInWhiteList[].class);
        if (optionalTaxIdIsInWhiteList.isPresent()) {
            TaxIdInWhiteList[] taxIdInWhiteListList = optionalTaxIdIsInWhiteList.get();
            for (TaxIdInWhiteList taxIdInWhiteList : taxIdInWhiteListList) {
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
    static class TaxIdInWhiteList {
        String taxId;
    }
}
