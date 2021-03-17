package org.incode.module.communications.dom.spi;

import java.util.Set;

import com.google.common.collect.Sets;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannel;

import lombok.Getter;
import lombok.Setter;

public abstract class CommHeaderAbstract<T extends CommunicationChannel> {

    @Getter @Setter
    private T toDefault;

    @Getter
    private final Set<T> toChoices = Sets.newTreeSet();

    /**
     * Reason, if any, why the email could not be sent.
     */
    @Getter @Setter
    private String disabledReason;
}
