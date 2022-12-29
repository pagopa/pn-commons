package it.pagopa.pn.commons.utils;

import com.amazonaws.Request;
import com.amazonaws.Response;
import com.amazonaws.handlers.RequestHandler2;
import lombok.extern.slf4j.Slf4j;

/**
 * Questa Handler cattura tutte le richieste, risposte ed errori di oggetti di AWS come
 * com.amazonaws.services.sqs.AmazonSQSAsync.
 * È possibile catturare gli eventi sovrascrivendo i metodi di {@link RequestHandler2}.
 * <p>
 * È possibile associare l'Handler durante la creazione dell'oggetto AWS, ad esempio:
 * <p>
 * AmazonSQSAsyncClientBuilder.standard()
 *             .withRequestHandlers(new CustomAWSRequestHandler())
 *             .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(awsConfigs.getEndpointUrl(), awsConfigs.getRegionCode()))
 *             .build();
 *
 */
@Slf4j
public class CustomAWSRequestHandler extends RequestHandler2 {

    @Override
    public void afterError(Request<?> request, Response<?> response, Exception e) {
        log.error("Exception for: " + request.getOriginalRequestObject().toString(), e);
    }
}
