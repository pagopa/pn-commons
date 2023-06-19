/* (C)2023 */
package it.pagopa.pn.commons.configs.lollipop;

import it.pagopa.pn.commons.lollipop.LollipopWebFilter;
import it.pagopa.tech.lollipop.consumer.assertion.AssertionServiceFactory;
import it.pagopa.tech.lollipop.consumer.assertion.client.simple.AssertionSimpleClientConfig;
import it.pagopa.tech.lollipop.consumer.assertion.client.simple.AssertionSimpleClientProvider;
import it.pagopa.tech.lollipop.consumer.assertion.impl.AssertionServiceFactoryImpl;
import it.pagopa.tech.lollipop.consumer.assertion.storage.SimpleAssertionStorageProvider;
import it.pagopa.tech.lollipop.consumer.assertion.storage.StorageConfig;
import it.pagopa.tech.lollipop.consumer.command.LollipopConsumerCommandBuilder;
import it.pagopa.tech.lollipop.consumer.command.impl.LollipopConsumerCommandBuilderImpl;
import it.pagopa.tech.lollipop.consumer.exception.LollipopVerifierException;
import it.pagopa.tech.lollipop.consumer.helper.LollipopConsumerFactoryHelper;
import it.pagopa.tech.lollipop.consumer.http_verifier.HttpMessageVerifierFactory;
import it.pagopa.tech.lollipop.consumer.http_verifier.visma.VismaHttpMessageVerifierFactory;
import it.pagopa.tech.lollipop.consumer.idp.IdpCertProviderFactory;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.IdpCertSimpleClientConfig;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.IdpCertSimpleClientProvider;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.storage.SimpleIdpCertStorageProvider;
import it.pagopa.tech.lollipop.consumer.idp.impl.IdpCertProviderFactoryImpl;
import it.pagopa.tech.lollipop.consumer.idp.storage.IdpCertStorageConfig;
import it.pagopa.tech.lollipop.consumer.logger.LollipopLoggerServiceFactory;
import it.pagopa.tech.lollipop.consumer.logger.impl.LollipopLogbackLoggerServiceFactory;
import it.pagopa.tech.lollipop.consumer.service.LollipopConsumerRequestValidationService;
import it.pagopa.tech.lollipop.consumer.service.impl.LollipopConsumerRequestValidationServiceImpl;
import org.springframework.context.annotation.Bean;

/**
 * Instance of Spring configuration of the core elements, the implementations of the related
 * services are delegated to external configurations
 */
public class PNHttpVerifierConfiguration {

    @Bean
    public LollipopWebFilter lollipopWebFilter(
            LollipopConsumerCommandBuilder lollipopConsumerCommandBuilder) {
        return new LollipopWebFilter(lollipopConsumerCommandBuilder);
    }

    @Bean
    public LollipopLoggerServiceFactory lollipopLoggerServiceFactory() {
        return new LollipopLogbackLoggerServiceFactory();
    }

    @Bean
    public SpringLollipopConsumerRequestConfig verifierConfiguration() {
        return new SpringLollipopConsumerRequestConfig();
    }

    @Bean
    public HttpMessageVerifierFactory httpMessageVerifierFactory() throws LollipopVerifierException {
        return new VismaHttpMessageVerifierFactory("UTF-8", verifierConfiguration());
    }

    @Bean
    public IdpCertProviderFactory idpCertProviderFactory() {
        return new IdpCertProviderFactoryImpl(
                new IdpCertSimpleClientProvider(
                        idpCertSimpleClientConfig(),
                        new SimpleIdpCertStorageProvider(),
                        new IdpCertStorageConfig()));
    }

    @Bean
    public AssertionServiceFactory assertionServiceFactory( AssertionSimpleClientConfig assertionSimpleClientConfig ) {
        return new AssertionServiceFactoryImpl(
                new SimpleAssertionStorageProvider(),
                new AssertionSimpleClientProvider( assertionSimpleClientConfig ),
                storageConfig());
    }

    @Bean
    public StorageConfig storageConfig() {
        return new StorageConfig();
    }

    @Bean
    public IdpCertSimpleClientConfig idpCertSimpleClientConfig() {
        return new SpringIdpCertSimpleClientConfig();
    }

    @Bean
    public LollipopConsumerFactoryHelper lollipopConsumerFactoryHelper(
            LollipopLoggerServiceFactory lollipopLoggerServiceFactory,
            HttpMessageVerifierFactory httpMessageVerifierFactory,
            IdpCertProviderFactory idpCertProviderFactory,
            AssertionServiceFactory assertionServiceFactory,
            LollipopConsumerRequestValidationService lollipopConsumerRequestValidationService,
            SpringLollipopConsumerRequestConfig springLollipopConsumerRequestConfig) {
        return new LollipopConsumerFactoryHelper(
                lollipopLoggerServiceFactory,
                httpMessageVerifierFactory,
                idpCertProviderFactory,
                assertionServiceFactory,
                lollipopConsumerRequestValidationService,
                springLollipopConsumerRequestConfig);
    }

    @Bean
    public LollipopConsumerRequestValidationService getLollipopConsumerRequestValidationService(
            SpringLollipopConsumerRequestConfig springLollipopConsumerRequestConfig) {
        return new LollipopConsumerRequestValidationServiceImpl(
                springLollipopConsumerRequestConfig);
    }

    @Bean
    public LollipopConsumerCommandBuilder lollipopConsumerCommandBuilder(
            LollipopConsumerFactoryHelper lollipopConsumerFactoryHelper) {
        return new LollipopConsumerCommandBuilderImpl(lollipopConsumerFactoryHelper);
    }
}
