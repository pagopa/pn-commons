package it.pagopa.pn.commons.configs.listener;

import it.pagopa.pn.commons.TestCommonsApplication;
import it.pagopa.pn.commons.abstractions.impl.AbstractCachedSsmParameterConsumer;
import it.pagopa.pn.commons.configs.MVPParameterConsumer;
import it.pagopa.pn.commons.configs.MVPParameterConsumerTestActivator;
import it.pagopa.pn.commons.configs.listeners.TaskIdApplicationListener;
import it.pagopa.pn.commons.utils.ValidateUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = { "pn.commons.features.is-mvp-default-value=true",
        "pn.env.runtime=DEVELOPMENT"})
@SpringBootTest
class TaskIdApplicationListenerTest {

    @Autowired
    private MVPParameterConsumerTestActivator isMVPParameterConsumer;

    @ExtendWith(MockitoExtension.class)
    @Test
    void onApplicationEventTest() {
        String ecsAgentUri = "http://example.com/task/12345-taskid";
        System.setProperty("ECS_AGENT_URI", ecsAgentUri);
        ApplicationStartingEvent event = Mockito.mock(ApplicationStartingEvent.class);
        TaskIdApplicationListener listener = new TaskIdApplicationListener();
        listener.onApplicationEvent(event);
        Assertions.assertEquals("local", System.getProperty("TASK_ID"));
        System.clearProperty("ECS_AGENT_URI");
    }

}
