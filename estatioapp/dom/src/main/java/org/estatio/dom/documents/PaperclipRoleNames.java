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
package org.estatio.dom.documents;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelType;
import org.incode.module.communications.dom.impl.comms.Communication;
import org.incode.module.communications.dom.mixins.DocumentConstants;
import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.dom.invoice.Invoice;

public final class PaperclipRoleNames {

    private PaperclipRoleNames(){}

    /**
     * for {@link Document}s attached to {@link CommunicationChannelType#EMAIL_ADDRESS email} {@link Communication}s
     */
    public static final String ATTACHMENT = DocumentConstants.PAPERCLIP_ROLE_ATTACHMENT;

    /**
     * for {@link Document}s attached to {@link CommunicationChannelType#EMAIL_ADDRESS email} {@link Communication}s
     */
    public static final String COVER = DocumentConstants.PAPERCLIP_ROLE_COVER;

    /**
     * for {@link Document}s attached to {@link CommunicationChannelType#POSTAL_ADDRESS postal} {@link Communication}s
     */
    public static final String ENCLOSED = DocumentConstants.PAPERCLIP_ROLE_ENCLOSED;


    /**
     * for supplier or tax receipt {@link Document}s attached to {@link Invoice}s.
     */
    public static final String INVOICE_RECEIPT = "receipt";

    public static final String INVOICE_BUYER = "buyer";
    public static final String INVOICE_SELLER = "seller";


    /**
     * for invoice note attached to a supplier or tax receipt document (copied over from {@link Invoice}).
     */
    public static final String INVOICE_DOCUMENT_SUPPORTED_BY = "supported by";


}
