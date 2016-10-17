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
package org.estatio.dom.invoice.viewmodel.dnc;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import com.google.common.base.Predicate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.factory.FactoryService;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelType;
import org.incode.module.communications.dom.impl.commchannel.EmailAddress;
import org.incode.module.communications.dom.mixins.Document_email;
import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.dom.invoice.viewmodel.InvoiceSummaryForPropertyDueDateStatus;

public abstract class InvoiceSummaryForPropertyDueDateStatus_sendByEmailAbstract extends InvoiceSummaryForPropertyDueDateStatus_sendAbstract {

    public InvoiceSummaryForPropertyDueDateStatus_sendByEmailAbstract(
            final InvoiceSummaryForPropertyDueDateStatus invoiceSummary,
            final String documentTypeReference) {
        super(invoiceSummary, documentTypeReference, CommunicationChannelType.EMAIL_ADDRESS);
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public InvoiceSummaryForPropertyDueDateStatus $$() throws IOException {

        for (Document document : documentsToSend()) {

            final Document_email emailMixin = emailMixin(document);
            final EmailAddress emailAddress = emailMixin.default0$$();

            final String cc = emailMixin.default1$$();
            final String bcc = emailMixin.default2$$();
            final String message = emailMixin.default3$$();
            emailMixin.$$(emailAddress, cc, bcc, message);
        }

        return this.invoiceSummary;
    }

    public String disable$$() {
        return documentsToSend().isEmpty()? "No documents available to be send by email": null;
    }

    @Override
    List<Document> documentsToSend() {
        return documentsToSend(canBeSentByEmail());
    }

    private Predicate<Document> canBeSentByEmail() {
        return doc -> {
            final Document_email emailMixin = emailMixin(doc);
            final EmailAddress emailAddress = emailMixin.default0$$();
            return emailAddress != null;
        };
    }

    private Document_email emailMixin(final Document document) {
        return factoryService.mixin(Document_email.class, document);
    }

    @Inject
    FactoryService factoryService;
}
