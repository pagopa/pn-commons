package it.pagopa.pn.commons.log;

import it.pagopa.pn.commons.log.dto.metrics.GeneralMetric;
import it.pagopa.pn.commons.utils.MetricUtils;
import org.slf4j.*;
import org.springframework.util.CollectionUtils;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItem;

import java.util.List;
import java.util.Map;

class PnLoggerImpl implements PnLogger {

    Logger log;

    private static void logAlarm( org.slf4j.Logger logger, String message, Object ...parameters) {
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


    public Logger getSlf4jLogger(){
        return this.log;
    }

    protected PnLoggerImpl(String name) {
        this.log = LoggerFactory.getLogger(name);
    }


    @Override
    public void logStartingProcess(String process) {
        log.info("Starting process {}", process);
    }

    @Override
    public void logEndingProcess(String process) {
        logEndingProcess(process, true, null);
    }

    @Override
    public void logEndingProcess(String process, boolean success, String description) {
        if (success)
            log.info("Ending process {}", process);
        else
            log.warn("Ending process {} with errors={}", process, description==null?"<not specified>":description);
    }

    @Override
    public void logChecking(String process) {
        log.info("Checking {}", process);
    }


    public void logCheckingOutcome(String process, boolean success) {
        logCheckingOutcome(process, success, null);
    }

    public void logCheckingOutcome(String process, boolean success, String description) {
        if (success)
            log.info("Checking {} passed", process);
        else
            log.warn("Checking {} failed reason={}", process, description==null?"<not specified>":description);
    }

    @Override
    public void logInvokingExternalService(String service, String process) {
        log.info("Invoking external service {} {}. Waiting Sync response.", service, process);
    }

    @Override
    public void logInvokingExternalDownstreamService(String service, String process) {
        log.info("[DOWNSTREAM] Invoking external service {} {}. Waiting Sync response.", service, process);
    }

    @Override
    public void logInvokingAsyncExternalService(String service, String process, String correlationId) {
        log.info("Invoking external service {} {}. {} for Async response.", service, process, correlationId);
    }

    @Override
    public void logInvokingAsyncExternalDownstreamService(String service, String process, String correlationId) {
        log.info("[DOWNSTREAM] Invoking external service {} {}. {} for Async response.", service, process, correlationId);
    }

    @Override
    public void logInvokationResultDownstreamFailed(String service, String description) {
        log.error("[DOWNSTREAM] Service {} returned errors={}", service, description==null?"<not specified>":description);
    }

    @Override
    public void logInvokationResultDownstreamNotFound(String service, String description) {
        log.info("[DOWNSTREAM] Service {} returned errors={}", service, description==null?"<not specified>":description);
    }

    @Override
    public <T> void logPuttingDynamoDBEntity(String tableName, T entity) {
        log.debug("Putting data in DynamoDb table: {}, entity: {}", tableName, entity);
    }

    @Override
    public void logPutDoneDynamoDBEntity(String tableName) {
        log.debug("Put data done in DynamoDb table: {}", tableName);
    }

    @Override
    public <T> void logGetDynamoDBEntity(String tableName, Key key, T entity) {
        log.debug("Get data in DynamoDb table: {}, partitionKey: {}, sortKey: {}, entity: {}", tableName, key.partitionKeyValue(), key.sortKeyValue(), entity);
    }

    @Override
    public <T, K> void logGetDynamoDBEntity(String tableName, K key, T entity) {
        log.debug("Get data in DynamoDb table: {}, key: {}, entity: {}", tableName, key, entity);
    }

    @Override
    public <T> void logDeleteDynamoDBEntity(String tableName, Key key, T entity) {
        log.debug("Delete data in DynamoDb table: {}, partitionKey: {}, sortKey: {}, entity: {}", tableName, key.partitionKeyValue(), key.sortKeyValue(), entity);
    }

    @Override
    public <T, K> void logDeleteDynamoDBEntity(String tableName, K key, T entity) {
        log.debug("Delete data in DynamoDb table: {}, key: {}, entity: {}", tableName, key, entity);
    }

    @Override
    public <T> void logUpdateDynamoDBEntity(String tableName, T entity) {
        log.debug("Update data in DynamoDb table: {}, entity: {}", tableName, entity);
    }

    @Override
    public void logTransactionDynamoDBEntity(TransactWriteItem transactWriteItem) {
        if(transactWriteItem.put() != null) {
            logTransactionDynamoDBEntity("Put", transactWriteItem.put().tableName(), transactWriteItem.put().item());
        }
        else if(transactWriteItem.delete() != null) {
            logTransactionDynamoDBEntity("Delete", transactWriteItem.delete().tableName(), transactWriteItem.delete().key());
        }
        else if(transactWriteItem.update() != null) {
            logTransactionDynamoDBEntity("Update", transactWriteItem.update().tableName(), transactWriteItem.update().key());
        }

    }

    @Override
    public void logMetric(List<GeneralMetric> metricsArray, String metricFormatType) {
        if (CollectionUtils.isEmpty(metricsArray)) {
            return;
        }
        String jsonMetric;

        if(metricFormatType.equals(PnAuditLogMetricFormatType.PNF.name())) {
            jsonMetric = String.format("{\"PNApplicationMetrics\":%s}", MetricUtils.generateJsonPNFMetric(metricsArray));
            log.info(jsonMetric);
        } else if (metricFormatType.equals(PnAuditLogMetricFormatType.EMF.name())) {
            String emfParameters = MetricUtils.generateJsonEMFMetricParameters(metricsArray).keySet().stream().map(
                    key -> String.format("\"%s\":\"%s\"", key, MetricUtils.generateJsonEMFMetricParameters(metricsArray).get(key))
            ).reduce((a, b) -> a + "," + b).orElse("");
            jsonMetric = String.format("{\"_aws\":%s,%s}", MetricUtils.generateJsonEMFMetric(metricsArray), emfParameters);
            log.info(jsonMetric);
        }
    }

    private void logTransactionDynamoDBEntity(String action, String tableName, Map<String, AttributeValue> keyOrItem) {
        log.debug("{} Transaction in DynamoDb table: {}, keyOrItem: {}", action, tableName, keyOrItem);
    }


    @Override
    public boolean isFatalEnabled() {
        return true;
    }

    @Override
    public void fatal(String s) {
        logAlarm(log, s);
    }

    @Override
    public void fatal(String s, Object o) {
        logAlarm(log, s, o);
    }

    @Override
    public void fatal(String s, Object var2, Object var3) {
        logAlarm(log, s, var2, var3);
    }

    @Override
    public void fatal(String var1, Object... var2) {
        logAlarm(log, var1, var2);
    }

    @Override
    public void fatal(String var1, Throwable var2) {
        logAlarm(log, var1, var2);
    }

    @Override
    public boolean isFatalEnabled(Marker var1) {
        return true;
    }



    @Override
    public String getName() {
        return log.getName();
    }

    @Override
    public boolean isTraceEnabled() {
        return log.isTraceEnabled();
    }

    @Override
    public void trace(String s) {
        log.trace(s);
    }

    @Override
    public void trace(String s, Object o) {
        log.trace(s, o);
    }

    @Override
    public void trace(String s, Object o, Object o1) {
        log.trace(s, o, o1);
    }

    @Override
    public void trace(String s, Object... objects) {
        log.trace(s, objects);
    }

    @Override
    public void trace(String s, Throwable throwable) {
        log.trace(s, throwable);
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return log.isTraceEnabled(marker);
    }

    @Override
    public void trace(Marker marker, String s) {
        log.trace(marker, s);
    }

    @Override
    public void trace(Marker marker, String s, Object o) {
        log.trace(marker, s, o);
    }

    @Override
    public void trace(Marker marker, String s, Object o, Object o1) {
        log.trace(marker, s, o, o1);
    }

    @Override
    public void trace(Marker marker, String s, Object... objects) {
        log.trace(marker, s, objects);
    }

    @Override
    public void trace(Marker marker, String s, Throwable throwable) {
        log.trace(marker, s, throwable);
    }

    @Override
    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    @Override
    public void debug(String s) {
        log.debug(s);
    }

    @Override
    public void debug(String s, Object o) {
        log.debug(s, o);
    }

    @Override
    public void debug(String s, Object o, Object o1) {
        log.debug(s, o, o1);
    }

    @Override
    public void debug(String s, Object... objects) {
        log.debug(s, objects);
    }

    @Override
    public void debug(String s, Throwable throwable) {
        log.debug(s, throwable);
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return log.isDebugEnabled(marker);
    }

    @Override
    public void debug(Marker marker, String s) {
        log.debug(marker, s);
    }

    @Override
    public void debug(Marker marker, String s, Object o) {
        log.debug(marker, s, o);
    }

    @Override
    public void debug(Marker marker, String s, Object o, Object o1) {
        log.debug(marker, s, o, o1);
    }

    @Override
    public void debug(Marker marker, String s, Object... objects) {
        log.debug(marker, s, objects);
    }

    @Override
    public void debug(Marker marker, String s, Throwable throwable) {
        log.debug(marker, s, throwable);
    }

    @Override
    public boolean isInfoEnabled() {
        return log.isInfoEnabled();
    }

    @Override
    public void info(String s) {
        log.info(s);
    }

    @Override
    public void info(String s, Object o) {
        log.info(s, o);
    }

    @Override
    public void info(String s, Object o, Object o1) {
        log.info(s, o, o1);
    }

    @Override
    public void info(String s, Object... objects) {
        log.info(s, objects);
    }

    @Override
    public void info(String s, Throwable throwable) {
        log.info(s, throwable);
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return log.isInfoEnabled(marker);
    }

    @Override
    public void info(Marker marker, String s) {
        log.info(marker, s);
    }

    @Override
    public void info(Marker marker, String s, Object o) {
        log.info(marker, s, o);
    }

    @Override
    public void info(Marker marker, String s, Object o, Object o1) {
        log.info(marker, s, o, o1);
    }

    @Override
    public void info(Marker marker, String s, Object... objects) {
        log.info(marker, s, objects);
    }

    @Override
    public void info(Marker marker, String s, Throwable throwable) {
        log.info(marker, s, throwable);
    }

    @Override
    public boolean isWarnEnabled() {
        return log.isWarnEnabled();
    }

    @Override
    public void warn(String s) {
        log.warn(s);
    }

    @Override
    public void warn(String s, Object o) {
        log.warn(s, o);
    }

    @Override
    public void warn(String s, Object... objects) {
        log.warn(s, objects);
    }

    @Override
    public void warn(String s, Object o, Object o1) {
        log.warn(s, o, o1);
    }

    @Override
    public void warn(String s, Throwable throwable) {
        log.warn(s, throwable);
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return log.isWarnEnabled(marker);
    }

    @Override
    public void warn(Marker marker, String s) {
        log.warn(marker, s);
    }

    @Override
    public void warn(Marker marker, String s, Object o) {
        log.warn(marker, s, o);
    }

    @Override
    public void warn(Marker marker, String s, Object o, Object o1) {
        log.warn(marker, s, o, o1);
    }

    @Override
    public void warn(Marker marker, String s, Object... objects) {
        log.warn(marker, s, objects);
    }

    @Override
    public void warn(Marker marker, String s, Throwable throwable) {
        log.warn(marker, s, throwable);
    }

    @Override
    public boolean isErrorEnabled() {
        return log.isErrorEnabled();
    }

    @Override
    public void error(String s) {
        log.error(s);
    }

    @Override
    public void error(String s, Object o) {
        log.error(s, o);
    }

    @Override
    public void error(String s, Object o, Object o1) {
        log.error(s, o, o1);
    }

    @Override
    public void error(String s, Object... objects) {
        log.error(s, objects);
    }

    @Override
    public void error(String s, Throwable throwable) {
        log.error(s, throwable);
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return log.isErrorEnabled(marker);
    }

    @Override
    public void error(Marker marker, String s) {
        log.error(marker, s);
    }

    @Override
    public void error(Marker marker, String s, Object o) {
        log.error(marker, s, o);
    }

    @Override
    public void error(Marker marker, String s, Object o, Object o1) {
        log.error(marker, s, o, o1);
    }

    @Override
    public void error(Marker marker, String s, Object... objects) {
        log.error(marker, s, objects);
    }

    @Override
    public void error(Marker marker, String s, Throwable throwable) {
        log.error(marker, s, throwable);
    }
}
