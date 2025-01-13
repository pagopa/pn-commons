package it.pagopa.pn.commons.utils;

import it.pagopa.pn.commons.configs.TaxIdInBlackListParameterConsumer;
import it.pagopa.pn.commons.configs.TaxIdInWhiteListParameterConsumer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValidateUtils {

    private final TaxIdInWhiteListParameterConsumer taxIdInWhiteListParameterConsumer;
    private final TaxIdInBlackListParameterConsumer taxIdInBlackListParameterConsumer;

    public ValidateUtils(TaxIdInWhiteListParameterConsumer taxIdInWhiteListParameterConsumer,
                         TaxIdInBlackListParameterConsumer taxIdInBlackListParameterConsumer) {
        this.taxIdInWhiteListParameterConsumer = taxIdInWhiteListParameterConsumer;
        this.taxIdInBlackListParameterConsumer = taxIdInBlackListParameterConsumer;
    }

    /**
     * Verifica la presenza di un taxId come PF (ovvero deve essere un CF 16 cifre)
     * o come PG (che può essere sia CF sia P.IVA) in white list
     *
     * @param taxId da validare
     * @return true se il taxId è presente in white list
     */
    public boolean taxIdIsInWhiteList(String taxId) {
        return taxIdInWhiteListParameterConsumer.isInWhiteList( taxId );
    }

    /**
     * Valida un taxId come PF (ovvero deve essere un CF 16 cifre)
     * o come PG (che può essere sia CF sia P.IVA)
     *
     * @param taxId da validare
     * @param skipCheckWhiteList true se si vuole evitare il check della presenza del taxId in white list
     * @return true se il taxId è valido
     */
    public boolean validate(String taxId, boolean skipCheckWhiteList) {
        return validate( taxId, false, skipCheckWhiteList );
    }

    /**
     * Valida un taxId come PF (ovvero deve essere un CF 16 cifre)
     * o come PG (che può essere sia CF sia P.IVA)
     *
     * @param taxId da validare
     * @param isPf true se si vuole validare espressamente SOLO un CF a 16 cifre
     * @return true se il taxId è valido
     */
    public boolean validate(String taxId, boolean isPf, boolean skipCheckWhiteList){
        taxId = normalize(taxId);
        if( taxId.length() == 0 ){
            return false;
        }
        if (!skipCheckWhiteList &&
                (Boolean.TRUE.equals(taxIdInWhiteListParameterConsumer.isInWhiteList(taxId)))) {
                return true;
        }

        if(!isPf && taxId.length() == 11 ){
            return validateIva(taxId);
        }
        else if( taxId.length() == 16 ){
            return validateCf(taxId);
        }
        return false;
    }

    public boolean validate(String taxId, boolean isPf, boolean skipCheckWhiteList, boolean skipCheckBlackList){
        taxId = normalize(taxId);
        if( taxId.length() == 0 ){
            return false;
        }
        if (!skipCheckWhiteList &&
                (Boolean.TRUE.equals(taxIdInWhiteListParameterConsumer.isInWhiteList(taxId)))) {
            return true;
        }
        if (!skipCheckBlackList &&
                (Boolean.TRUE.equals(taxIdInBlackListParameterConsumer.isInBlackList(taxId)))) {
            return false;
        }

        if(!isPf && taxId.length() == 11 ){
            return validateIva(taxId);
        }
        else if( taxId.length() == 16 ){
            return validateCf(taxId);
        }
        return false;
    }

    public boolean validate(String taxId){
        // non passare isPF sottointende validare sia PF che PG
        return validate(taxId, false, false);
    }

    private static String normalize(String cf) {
        cf = cf.replaceAll("[ \t\r\n]", "");
        cf = cf.toUpperCase();
        return cf;
    }

    private boolean validateCf(String cf) {
        if(!cf.matches("^[A-Z]{6}[0-9LMNPQRSTUV]{2}[ABCDEHLMPRST][0-9LMNPQRSTUV]{2}[A-Z][0-9LMNPQRSTUV]{3}[A-Z]$") )
            return false;
        int s = 0;
        String evenMap = "BAFHJNPRTVCESULDGIMOQKWZYX";
        for(int i = 0; i < 15; i++){
            int c = cf.charAt(i);
            int n;
            if( '0' <= c && c <= '9' )
                n = c - '0';
            else
                n = c - 'A';
            if( (i & 1) == 0 )
                n = evenMap.charAt(n) - 'A';
            s += n;
        }
        return s % 26 + 'A' == cf.charAt(15);
    }

    private boolean validateIva(String iva){
        if(!iva.matches("^\\d{11}$") )
            return false;
        int s = 0;
        for(int i = 0; i < 11; i++){
            int n = iva.charAt(i) - '0';
            if( (i & 1) == 1 ){
                n *= 2;
                if( n > 9 )
                    n -= 9;
            }
            s += n;
        }
        return s % 10 == 0;
    }



}
