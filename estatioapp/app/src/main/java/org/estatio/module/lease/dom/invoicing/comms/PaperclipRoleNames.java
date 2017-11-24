/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.module.lease.dom.invoicing.comms;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelType;
import org.incode.module.communications.dom.impl.comms.Communication;
import org.incode.module.communications.dom.mixins.DocumentConstants;
import org.incode.module.communications.dom.mixins.Document_sendByEmail;
import org.incode.module.communications.dom.mixins.Document_sendByPost;
import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.module.invoice.dom.Invoice;

public final class PaperclipRoleNames {

    private PaperclipRoleNames(){}

    /**
     * for {@link Document}s attached to {@link CommunicationChannelType#EMAIL_ADDRESS email} / enclosed with {@link CommunicationChannelType#POSTAL_ADDRESS postal} {@link Communication}s
     */
    public static final String ATTACHMENT = DocumentConstants.PAPERCLIP_ROLE_ATTACHMENT;

    /**
     * for "primary" {@link Document}s attached to {@link CommunicationChannelType#EMAIL_ADDRESS email} {@link Communication}s.
     *
     * <p>
     *     These are the {@link Document}s that are sent either by {@link Document_sendByEmail email} or {@link Document_sendByPost post}.
     * </p>
     */
    public static final String PRIMARY = DocumentConstants.PAPERCLIP_ROLE_PRIMARY;

    /**
     * for {@link Document}s attached to {@link CommunicationChannelType#EMAIL_ADDRESS email} {@link Communication}s
     */
    public static final String COVER = DocumentConstants.PAPERCLIP_ROLE_COVER;



    /**
     * for supporting {@link Document}s attached to {@link Invoice}s.
     */
    public static final String SUPPORTING_DOCUMENT = "supporting";

    public static final String INVOICE_BUYER = "buyer";
    public static final String INVOICE_SELLER = "seller";


    /**
     * for invoice note attached to a supplier or tax receipt document (copied over from {@link Invoice}).
     */
    public static final String INVOICE_DOCUMENT_SUPPORTED_BY = "supports";


}
