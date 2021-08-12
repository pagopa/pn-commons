package it.pagopa.pn.commons.abstractions;

import java.time.Duration;
import java.util.List;

public interface MomConsumer<T> {

    List<T> poll(Duration maxPollTime);

}
