package it.pagopa.pn.commons.configs;

public class EnvironmentConfig {
    public static final String METRIC_FORMAT_TYPE = "METRIC_FORMAT_TYPE";

    private EnvironmentConfig() {}

    public static String getMetricFormatType() {
        return System.getenv(METRIC_FORMAT_TYPE);
    }
}
