package it.pagopa.pn.commons;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.core.io.ClassPathResource;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import java.io.IOException;
import java.time.Duration;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.DYNAMODB;

/**
 * Classe che permette di creare un container Docker di LocalStack.
 * Il container (e quindi la classe) può essere condivisa tra più classi di test.
 * Per utilizzare questa classe, le classi di test dovranno essere annotate con
 * @Import(LocalStackTestConfig.class)
 */
@TestConfiguration
@Slf4j
public class LocalStackTestConfig {
    static LocalStackContainer localStack =
            new LocalStackContainer(DockerImageName.parse("localstack/localstack:1.0.4").asCompatibleSubstituteFor("localstack/localstack"))
                    .withServices(DYNAMODB)
                    .withClasspathResourceMapping("testcontainers/initsh-for-testcontainer.sh",
                            "/docker-entrypoint-initaws.d/make-storages.sh", BindMode.READ_ONLY)
                    .withClasspathResourceMapping("testcontainers/credentials",
                            "/root/.aws/credentials", BindMode.READ_ONLY)
                    .withNetworkAliases("localstack")
                    .withNetwork(Network.builder().build())
                    .withStartupTimeout(Duration.ofSeconds(10))
                    .withStartupAttempts(3)
                    .waitingFor(Wait.forLogMessage(".*Initialization terminated.*", 1));

    static {
        localStack.start();
        System.setProperty("aws.endpoint-url", localStack.getEndpointOverride(DYNAMODB).toString());

        try {
            System.setProperty("aws.sharedCredentialsFile", new ClassPathResource("testcontainers/credentials").getFile().getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}