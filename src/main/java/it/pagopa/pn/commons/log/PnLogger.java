package it.pagopa.pn.commons.log;



import org.slf4j.Logger;
import org.slf4j.Marker;

public interface PnLogger extends Logger {

    public static PnLogger getLogger(String name){
        return new PnLoggerImpl(name);
    }

    // viene volutamente scritta "male", per essere più facilmente ricercabile nei log
    public static final String ALARM_LOG = "ALLARM!";


    public static final class EXTERNAL_SERVICES {

        private EXTERNAL_SERVICES(){}

        public static final String PN_NATIONAL_REGISTRIES = "pn-national-registries";
        public static final String PN_USER_ATTRIBUTES= "pn-user-attributes";
        public static final String PN_MANDATE = "pn-mandate";
        public static final String PN_DELIVERY = "pn-delivery";
        public static final String PN_DELIVERY_PUSH = "pn-delivery-push";
        public static final String PN_EXTERNAL_REGISTRIES = "pn-external-registries";
        public static final String PN_EXTERNAL_CHANNELS = "pn-external-channels";
        public static final String PN_PAPER_CHANNEL = "pn-paper-channel";
        public static final String PN_DATA_VAULT = "pn-data-vault";
        public static final String PN_LOGEXTRACTOR_BE = "pn-logextractor-be";
        public static final String PN_APIKEY_MANAGER = "pn-apikey-manager";
        public static final String PN_ADDRESS_MANAGER = "pn-address-manager";
        public static final String PN_SAFE_STORAGE = "pn-safe-storage";
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

    void fatal(Marker var1, String var2);

    void fatal(Marker var1, String var2, Object var3);

    void fatal(Marker var1, String var2, Object var3, Object var4);

    void fatal(Marker var1, String var2, Object... var3);

    void fatal(Marker var1, String var2, Throwable var3);

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
     * metodo per loggare l'invocazione a un servizio esterno
     *
     * @param service nome servizio, possibilmente usare quelli definiti in PnLogger.EXTERNAL_SERVICES
     * @param process descrizione
     * @param correlationId correlationId
     */
    void logInvokingAsyncExternalService(String service, String process, String correlationId);

}
