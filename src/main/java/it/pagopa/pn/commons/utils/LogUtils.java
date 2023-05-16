package it.pagopa.pn.commons.utils;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItem;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

@Slf4j
public class LogUtils {

    private LogUtils(){}

    // viene volutamente scritta "male", per essere più facilmente ricercabile nei log
    private static final String ALARM_LOG = "ALLARM!";

    public static void logAlarm( org.slf4j.Logger logger, String message, Object ...parameters) {
        try {
            Marker alarmMarker = MarkerFactory.getMarker(ALARM_LOG);
            String finalMessage =  ALARM_LOG + ": " + (message==null?"errore grave":message);
            logger.error(alarmMarker, finalMessage, parameters);
        } catch (Exception e) {
            Marker alarmMarker = MarkerFactory.getMarker(ALARM_LOG);
            String finalMessage =  ALARM_LOG + ": errore grave";
            logger.error(alarmMarker, finalMessage, e);
        }
    }

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
        String message = String.format("filename=%s", filename);
        String safeUrl = StringUtils.hasText( url )? url.split("\\?")[0] : null;
        if (StringUtils.hasText( safeUrl ) ) {
            message += String.format(", url=%s", safeUrl);
        }
        if ( StringUtils.hasText( retryAfter ) ) {
            message += String.format(", retryAfter=%s", retryAfter);
        }
        return message;
    }

    public static <T> void logPuttingDynamoDBEntity(String tableName, T entity) {
        log.info("Putting data in DynamoDb table: {}, entity: {}", tableName, entity);
    }

    public static <T> void logGetDynamoDBEntity(String tableName, T entity) {
        log.info("Get data in DynamoDb table: {}, entity: {}", tableName, entity);
    }

    public static <T> void logDeleteDynamoDBEntity(String tableName, T entity) {
        log.info("Delete data in DynamoDb table: {}, entity: {}", tableName, entity);
    }

    public static <T> void logUpdateDynamoDBEntity(String tableName, T entity) {
        log.info("Update data in DynamoDb table: {}, entity: {}", tableName, entity);
    }

    public static void logTransactionDynamoDBEntity(TransactWriteItem transactWriteItem) {
        if(transactWriteItem.put() != null) {
            logTransactionDynamoDBEntity("Put", transactWriteItem.put().tableName());
        }
        else if(transactWriteItem.delete() != null) {
            logTransactionDynamoDBEntity("Delete", transactWriteItem.delete().tableName());
        }
        else if(transactWriteItem.update() != null) {
            logTransactionDynamoDBEntity("Update", transactWriteItem.update().tableName());
        }

    }

    private static void logTransactionDynamoDBEntity(String action, String tableName) {
        log.info("{} Transaction in DynamoDb table: {}", action, tableName);
    }

}
