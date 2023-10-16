package it.pagopa.pn.commons.configs.listeners;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationListener;

import java.util.Objects;

public class ApplicationConfigurationListener implements ApplicationListener<ApplicationStartingEvent> {

    @Override
    public void onApplicationEvent(@NotNull ApplicationStartingEvent event) {
        String taskId = System.getenv("ECS_AGENT_URI");
        if(taskId != null) {
            System.setProperty("TASK_ID", Objects.requireNonNullElse(taskId.split("/")[4].split("-")[0], "local"));
        }
    }

}
