package org.incode.module.communications.dom.mixins;

import org.incode.module.communications.dom.impl.comms.Communication;
import org.incode.module.document.dom.impl.docs.Document;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelType;

public final class DocumentConstants {

    private DocumentConstants(){}

    public static final String MIME_TYPE_APPLICATION_PDF = "application/pdf";

    /**
     * for "primary" {@link Document}s attached to {@link CommunicationChannelType#EMAIL_ADDRESS email} {@link Communication}s.
     *
     * <p>
     *     These are the {@link Document}s that are sent either by {@link Document_sendByEmail email} or {@link Document_sendByPost post}.
     * </p>
     */
    public static final String PAPERCLIP_ROLE_PRIMARY = "primary";

    /**
     * for other {@link Document}s attached to {@link CommunicationChannelType#EMAIL_ADDRESS email} / enclosed in {@link CommunicationChannelType#POSTAL_ADDRESS postal} {@link Communication}s
     */
    public static final String PAPERCLIP_ROLE_ATTACHMENT = "attachment";

    /**
     * for {@link Document}s attached to {@link CommunicationChannelType#EMAIL_ADDRESS email} {@link Communication}s
     */
    public static final String PAPERCLIP_ROLE_COVER = "cover";

}
