package it.pagopa.pn.commons.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

@Slf4j
public class LogUtils {

    private LogUtils(){}

    public static String maskEmailAddress(String strEmail) {
        if (strEmail == null)
            return "null";

        String[] parts = strEmail.split("@");

        //mask first part
        String strId;
        if(parts[0].length() < 4)
            strId = "***";
        else
            strId = maskString(parts[0], 1, parts[0].length()-1, '*');

        //now append the domain part to the masked id part
        return strId + "@" + parts[1];
    }

    public static String maskNumber(String number) {
        try {
            if (number == null)
                return "null";
            return maskString(number, 1, number.length() - 3, '*');
        } catch (Exception e) {
            log.error("cannot mask number", e);
            return "***";
        }
    }


    public static String maskGeneric(String generic) {
        try {
            if (generic == null)
                return "null";

            return generic.length()>=5?maskString(generic, 1, generic.length() - 3, '*'):"***";
        } catch (Exception e) {
            log.error("cannot mask generic", e);
            return "***";
        }
    }


    public static String maskTaxId(String cf) {
        try {
            if (cf == null)
                return "null";
            return maskString(cf, 1, cf.length() - 3, '*');
        } catch (Exception e) {
            log.error("cannot mask cf", e);
            return "***";
        }
    }

    public static String maskString(String strText, int start, int end, char maskChar) {
        if(strText == null)
            return null;
        if(strText.equals(""))
            return "";

        if(start < 0)
            start = 0;

        if( end > strText.length() )
            end = strText.length();


        int maskLength = end - start;

        if(maskLength == 0)
            return strText;

        String sbMaskString = String.valueOf(maskChar).repeat(Math.max(0, maskLength));

        return strText.substring(0, start)
                + sbMaskString
                + strText.substring(start + maskLength);
    }

    public static String createAuditLogMessageForDownloadDocument(@NotNull String filename, @Nullable String url, @Nullable String retryAfter) {
        String message = String.format("filename=%s, ", filename);
        String safeUrl = StringUtils.hasText( url )? url.split("\\?")[0] : null;
        if (StringUtils.hasText( safeUrl ) ) {
            message += String.format("url=%s", safeUrl);
        }
        if ( StringUtils.hasText( retryAfter ) ) {
            message += String.format("retryAfter=%s", retryAfter);
        }
        return message;
    }
}
