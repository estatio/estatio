package org.estatio.module.application.app;

import org.apache.isis.applib.annotation.Programmatic;

/**
 * Optional SPI.
 */
public interface SlackService {

    @Programmatic
    boolean isConfigured();

    @Programmatic
    void sendMessage(String message);
}
