package it.pagopa.pn.commons.log;



import it.pagopa.pn.commons.log.dto.metrics.GeneralMetric;
import org.slf4j.Logger;
import org.slf4j.Marker;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItem;

import java.util.List;

public interface PnLogger extends Logger {

    public static PnLogger getLogger(String name){
        return new PnLoggerImpl(name);
    }

    // viene volutamente scritta "male", per essere più facilmente ricercabile nei log
    // aggiunto * FATAL * per essere ricercato dalle sonde
    public static final String ALARM_LOG = "* FATAL * ALLARM!";


    public final class EXTERNAL_SERVICES {

        private EXTERNAL_SERVICES(){}

        public static final String PN_NATIONAL_REGISTRIES = "pn-national-registries";
        public static final String PN_USER_ATTRIBUTES= "pn-user-attributes";
        public static final String PN_MANDATE = "pn-mandate";
        public static final String PN_DELIVERY = "pn-delivery";
        public static final String PN_DELIVERY_PUSH = "pn-delivery-push";
        public static final String PN_F24 = "pn-f24";
        public static final String PN_EXTERNAL_REGISTRIES = "pn-external-registries";
        public static final String PN_EXTERNAL_CHANNELS = "pn-external-channels";
        public static final String PN_PAPER_CHANNEL = "pn-paper-channel";
        public static final String PN_DATA_VAULT = "pn-data-vault";
        public static final String PN_LOGEXTRACTOR_BE = "pn-logextractor-be";
        public static final String PN_APIKEY_MANAGER = "pn-apikey-manager";
        public static final String PN_ADDRESS_MANAGER = "pn-address-manager";
        public static final String PN_ACTION_MANAGER = "pn-action-manager";
        public static final String PN_SAFE_STORAGE = "pn-safe-storage";
        public static final String CHECKOUT = "Checkout";
        public static final String SELFCARE_PG = "SelfcarePG";
        public static final String SELFCARE_PA = "SelfcarePA";
        public static final String IO = "IO";
        public static final String ONE_TRUST = "OneTrust";
        public static final String ARUBA = "Aruba";
        public static final String CONSOLIDATORE = "Consolidatore";
        public static final String AMAZON = "Amazon";
        public static final String INTEROP = "Interoperabilita";
        public static final String PDV = "PersonalDataVault";
        public static final String ADE = "AdE";
        public static final String INAD = "INAD";
        public static final String ANPR = "ANPR";
        public static final String INFO_CAMERE = "InfoCamere";
        public static final String IPA = "IPA";
        public static final String PDND = "PDND";
        public static final String GPD = "GPD";
        public static final String EMD_CORE = "EMD_CORE";
        public static final String MIL_AUTH = "MIL_AUTH";
        public static final String PN_EMD_INTEGRATION = "PN_EMD_INTEGRATION";
        public static final String PN_TIMELINE_SERVICE = "pn-timeline-service";
    }

    /**
     * Ritorna il logger sottostante
     * @return logger
     */
    Logger getSlf4jLogger();

    // fatal methods

    boolean isFatalEnabled();

    void fatal(String var1);

    void fatal(String var1, Object var2);

    void fatal(String var1, Object var2, Object var3);

    void fatal(String var1, Object... var2);

    void fatal(String var1, Throwable var2);

    boolean isFatalEnabled(Marker var1);


    // metodi shortcut applicativi, l'idea è farli iniziare tutti con il prefisso log*, così son più facili da recuperare

    /**
     * Logga a info lo start di uno step logico
     * @param process nome processo
     */
    void logStartingProcess(String process);

    /**
     * Logga a info la fine di uno step logico
     * @param process nome processo
     */
    void logEndingProcess(String process);
    /**
     * Logga a info (warn nel caso di fallimento) la fine di uno step logico
     * @param process nome processo
     * @param success indica l'esito del processo
     * @param description eventuale descrizione nel caso di success negativo
     */
    void logEndingProcess(String process, boolean success, String description);

    /**
     * Logga a info lo start della validazione di uno step logico
     * @param process nome processo
     */
    void logChecking(String process);

    /**
     * Logga a info (warn nel caso di fallimento) la fine della validazione di uno step logico
     * @param process nome processo
     * @param success indica se la validazione è stata superata o meno
     */
    void logCheckingOutcome(String process, boolean success);

    /**
     * Logga a info (warn nel caso di fallimento) la fine della validazione di uno step logico, con causale
     *
     * @param process nome processo
     * @param success indica se la validazione è stata superata o meno
     * @param description causale sul mancato superamento
     */
    void logCheckingOutcome(String process, boolean success, String description);

    /**
     * metodo per loggare l'invocazione a un servizio esterno
     *
     * @param service nome servizio, possibilmente usare quelli definiti in PnLogger.EXTERNAL_SERVICES
     * @param process descrizione
     */
    void logInvokingExternalService(String service, String process);

    /**
     * metodo per loggare l'invocazione a un servizio esterno di downstream
     *
     * @param service nome servizio, possibilmente usare quelli definiti in PnLogger.EXTERNAL_SERVICES
     * @param process descrizione
     */
    void logInvokingExternalDownstreamService(String service, String process);

    /**
     * metodo per loggare l'invocazione a un servizio esterno
     *
     * @param service nome servizio, possibilmente usare quelli definiti in PnLogger.EXTERNAL_SERVICES
     * @param process descrizione
     * @param correlationId correlationId
     */
    void logInvokingAsyncExternalService(String service, String process, String correlationId);

    /**
     * metodo per loggare l'invocazione a un servizio esterno di downstream
     *
     * @param service nome servizio, possibilmente usare quelli definiti in PnLogger.EXTERNAL_SERVICES
     * @param process descrizione
     * @param correlationId correlationId
     */
    void logInvokingAsyncExternalDownstreamService(String service, String process, String correlationId);

    /**
     * metodo per loggare l'errore di un invocazione ad un servizio di downstream
     *
     * @param service     nome servizio, possibilmente usare quelli definiti in PnLogger.EXTERNAL_SERVICES
     * @param description eventuale descrizione dell'errore
     */
    void logInvokationResultDownstreamFailed(String service, String description);

    /**
     * metodo per loggare l'esito di not found per una invocazione ad un servizio di downstream
     *
     * @param service     nome servizio, possibilmente usare quelli definiti in PnLogger.EXTERNAL_SERVICES
     * @param description eventuale descrizione dell'errore
     */
    void logInvokationResultDownstreamNotFound(String service, String description);

    /**
     * metodo per loggare la PUT item su dynamoDB. Indica quale record e su quale tabella si effettuerà la PUT
     *
     * @param tableName nome della tabella dynamoDB
     * @param entity il dato da salvare su dynamoDB
     */
    <T> void logPuttingDynamoDBEntity(String tableName, T entity);

    /**
     * metodo per loggare la fine di una PUT item su dynamoDB
     *
     * @param tableName nome della tabella dynamoDB
     */
    void logPutDoneDynamoDBEntity(String tableName);

    /**
     * metodo per loggare la GET item su dynamoDB
     *
     * @param tableName nome della tabella dynamoDB
     * @param key la chiave di tipo {@link Key} della tabella dynamodb su cui fare la GET item.
     *            Stampa partitionKey ed eventuale sortKey
     * @param entity il dato restituito da dynamoDB
     */
    <T> void logGetDynamoDBEntity(String tableName, Key key, T entity);

    /**
     * metodo per loggare la GET item su dynamoDB
     *
     * @param tableName nome della tabella dynamoDB
     * @param key la chiave di tipo {@link K} della tabella dynamodb su cui fare la GET item.
     *            Stampa la chiave data in input per effettuare la GET
     * @param entity il dato restituito da dynamoDB
     */
    <T, K> void logGetDynamoDBEntity(String tableName, K key, T entity);

    /**
     * metodo per loggare la DELETE item su dynamoDB
     *
     * @param tableName nome della tabella dynamoDB
     * @param key la chiave di tipo {@link Key} della tabella dynamoDB su cui effettuare la delete.
     *            Stampa partitionKey ed eventuale sortKey
     * @param entity il dato eliminato
     */
    <T> void logDeleteDynamoDBEntity(String tableName, Key key, T entity);

    /**
     * metodo per loggare la DELETE item su dynamoDB
     *
     * @param tableName nome della tabella dynamoDB
     * @param key la chiave di tipo {@link K} della tabella dynamoDB su cui effettuare la delete.
     *            Stampa la chiave data in input per effettuare la DELETE
     * @param entity il dato eliminato
     */
    <T, K> void logDeleteDynamoDBEntity(String tableName, K key, T entity);

    /**
     * metodo per loggare la UPDATE item su dynamoDB
     *
     * @param tableName nome della tabella dynamoDB
     * @param entity il dato modificato restituito da dynamoDB
     */
    <T> void logUpdateDynamoDBEntity(String tableName, T entity);

    /**
     * metodo per loggare la tabella su una transactWriteItem di dynamoDB
     *
     * @param transactWriteItem la transactionWrite item inserita su dynamoDB
     */
    void logTransactionDynamoDBEntity(TransactWriteItem transactWriteItem);

    /**
     * metodo per loggare una lista di metriche
     *
     * @param metricsArray la lista di metriche
     */
    void logMetric(List<GeneralMetric> metricsArray, String message);

    void logMetric(List<GeneralMetric> metricsArray, String message, String metricFormatType);
}
