package it.pagopa.pn.commons.configs.lollipop;

import it.pagopa.pn.commons.lollipop.LollipopWebFilter;
import it.pagopa.tech.lollipop.consumer.assertion.AssertionServiceFactory;
import it.pagopa.tech.lollipop.consumer.assertion.client.simple.AssertionSimpleClientConfig;
import it.pagopa.tech.lollipop.consumer.assertion.storage.StorageConfig;
import it.pagopa.tech.lollipop.consumer.command.LollipopConsumerCommandBuilder;
import it.pagopa.tech.lollipop.consumer.exception.LollipopVerifierException;
import it.pagopa.tech.lollipop.consumer.helper.LollipopConsumerFactoryHelper;
import it.pagopa.tech.lollipop.consumer.http_verifier.HttpMessageVerifierFactory;
import it.pagopa.tech.lollipop.consumer.idp.IdpCertProviderFactory;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.IdpCertSimpleClientConfig;
import it.pagopa.tech.lollipop.consumer.logger.LollipopLoggerServiceFactory;
import it.pagopa.tech.lollipop.consumer.service.LollipopConsumerRequestValidationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;


class PNHttpVerifierConfigurationTest {

    @Mock
    LollipopConsumerCommandBuilder lollipopConsumerCommandBuilder;

    @Mock
    LollipopConsumerFactoryHelper lollipopConsumerFactoryHelper;

    @Mock
    SpringLollipopConsumerRequestConfig springLollipopConsumerRequestConfig;

    @Mock
    LollipopLoggerServiceFactory lollipopLoggerServiceFactory;

    @Mock
    HttpMessageVerifierFactory httpMessageVerifierFactory;

    @Mock
    IdpCertProviderFactory idpCertProviderFactory;

    @Mock
    AssertionServiceFactory assertionServiceFactory;

    @Mock
    LollipopConsumerRequestValidationService lollipopConsumerRequestValidationService;


    private PNHttpVerifierConfiguration pnHttpVerifierConfiguration;

    @BeforeEach
    void setup() { pnHttpVerifierConfiguration = new PNHttpVerifierConfiguration(); }

    @Test
    void lollipopWebFilter() {
        LollipopWebFilter lollipopWebFilter = pnHttpVerifierConfiguration.lollipopWebFilter(lollipopConsumerCommandBuilder);
        Assertions.assertNotNull(lollipopWebFilter);
    }

    @Test
    void lollipopLoggerServiceFactory() {
        LollipopLoggerServiceFactory lollipopLoggerServiceFactory = pnHttpVerifierConfiguration.lollipopLoggerServiceFactory();
        Assertions.assertNotNull(lollipopLoggerServiceFactory);
    }

    @Test
    void verifierConfiguration() {
        SpringLollipopConsumerRequestConfig springLollipopConsumerRequestConfig = pnHttpVerifierConfiguration.verifierConfiguration();
        Assertions.assertNotNull(springLollipopConsumerRequestConfig);
    }

    @Test
    void httpMessageVerifierFactory() throws LollipopVerifierException {
        HttpMessageVerifierFactory httpMessageVerifierFactory = pnHttpVerifierConfiguration.httpMessageVerifierFactory();
        Assertions.assertNotNull(httpMessageVerifierFactory);
    }

    @Test
    void idpCertProviderFactory() {
        IdpCertProviderFactory idpCertProviderFactory = pnHttpVerifierConfiguration.idpCertProviderFactory();
        Assertions.assertNotNull(idpCertProviderFactory);
    }

    @Test
    void assertionServiceFactory() {
        AssertionServiceFactory assertionServiceFactory = pnHttpVerifierConfiguration.assertionServiceFactory(new AssertionSimpleClientConfig());
        Assertions.assertNotNull(assertionServiceFactory);
    }

    @Test
    void storageConfig() {
        StorageConfig storageConfig = pnHttpVerifierConfiguration.storageConfig();
        Assertions.assertNotNull(storageConfig);
    }

    @Test
    void idpCertSimpleClientConfig() {
        IdpCertSimpleClientConfig idpCertSimpleClientConfig = pnHttpVerifierConfiguration.idpCertSimpleClientConfig();
        Assertions.assertNotNull(idpCertSimpleClientConfig);
    }

    @Test
    void lollipopConsumerFactoryHelper() {
        LollipopConsumerFactoryHelper lollipopConsumerFactoryHelper = pnHttpVerifierConfiguration.lollipopConsumerFactoryHelper(
                lollipopLoggerServiceFactory,
                httpMessageVerifierFactory,
                idpCertProviderFactory,
                assertionServiceFactory,
                lollipopConsumerRequestValidationService,
                springLollipopConsumerRequestConfig
        );
        Assertions.assertNotNull(lollipopConsumerFactoryHelper);
    }

    //@Test
    void getLollipopConsumerRequestValidationService() {
        LollipopConsumerRequestValidationService lollipopConsumerRequestValidationService = pnHttpVerifierConfiguration.getLollipopConsumerRequestValidationService(springLollipopConsumerRequestConfig);
        Assertions.assertNotNull(lollipopConsumerRequestValidationService);
    }

    @Test
    void lollipopConsumerCommandBuilder() {
        LollipopConsumerCommandBuilder lollipopConsumerCommandBuilder1 = pnHttpVerifierConfiguration.lollipopConsumerCommandBuilder(lollipopConsumerFactoryHelper);
        Assertions.assertNotNull(lollipopConsumerCommandBuilder1);
    }
}