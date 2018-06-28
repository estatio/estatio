package org.estatio.module.coda.contributions;

import java.sql.Timestamp;
import java.util.UUID;

import org.isisaddons.module.publishmq.dom.jdo.status.StatusMessage;

import lombok.Getter;
import lombok.Setter;

public class StatusMessageSummary {

    /**
     * To lookup the actual {@link StatusMessage}
     */
    @Getter @Setter
    private UUID transactionId;

    /**
     * To lookup the actual {@link StatusMessage}
     */
    @Getter @Setter
    private Timestamp timestamp;

    /**
     * To display in the UI.
     */
    @Getter @Setter
    private String message;
}
