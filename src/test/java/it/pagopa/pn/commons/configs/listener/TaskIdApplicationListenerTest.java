package it.pagopa.pn.commons.configs.listener;

import it.pagopa.pn.commons.abstractions.impl.AbstractCachedSsmParameterConsumer;
import it.pagopa.pn.commons.configs.listeners.TaskIdApplicationListener;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = { "pn.commons.features.is-mvp-default-value=true",
        "pn.env.runtime=DEVELOPMENT"})
@SpringBootTest
class TaskIdApplicationListenerTest {


    @MockBean
    private AbstractCachedSsmParameterConsumer abstractCachedSsmParameterConsumer;


    @Test
    void onApplicationEventTest() {
        ApplicationStartingEvent event = Mockito.mock(ApplicationStartingEvent.class);
        TaskIdApplicationListener listener = new TaskIdApplicationListener();
        listener.onApplicationEvent(event);
        Assertions.assertEquals("local", System.getProperty("TASK_ID"));
    }

}
