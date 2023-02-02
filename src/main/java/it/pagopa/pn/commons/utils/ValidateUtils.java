package it.pagopa.pn.commons.utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValidateUtils {

    private ValidateUtils() {
    }

    public static boolean validate(String taxId){
        taxId = normalize(taxId);
        if( taxId.length() == 0 ){
            return false;
        }
        else if( taxId.length() == 11 ){
            return validateIva(taxId);
        }
        else if( taxId.length() == 16 ){
            return validateCf(taxId);
        }
        return false;
    }

    private static String normalize(String cf) {
        cf = cf.replaceAll("[ \t\r\n]", "");
        cf = cf.toUpperCase();
        return cf;
    }

    private static boolean validateCf(String cf) {
        if(!cf.matches("^[A-Z]{6}[0-9LMNPQRSTUV]{2}[ABCDEHLMPRST][0-9LMNPQRSTUV]{2}[A-Z][0-9LMNPQRSTUV]{3}[A-Z]$") )
            return false;
        int s = 0;
        String even_map = "BAFHJNPRTVCESULDGIMOQKWZYX";
        for(int i = 0; i < 15; i++){
            int c = cf.charAt(i);
            int n;
            if( '0' <= c && c <= '9' )
                n = c - '0';
            else
                n = c - 'A';
            if( (i & 1) == 0 )
                n = even_map.charAt(n) - 'A';
            s += n;
        }
        return s % 26 + 'A' == cf.charAt(15);
    }

    private static boolean validateIva(String iva){
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
