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
package org.estatio.dom.invoice.dnc;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannel;
import org.incode.module.communications.dom.impl.commchannel.EmailAddress;
import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.dom.EstatioDomainModule;
import org.estatio.dom.invoice.Invoice;

public abstract class Invoice_emailPrelimLetterOrInvoiceDocAbstract extends Invoice_sendPrelimLetterOrInvoiceDocAbstract {

    public Invoice_emailPrelimLetterOrInvoiceDocAbstract(final Invoice invoice, final String documentTypeReference) {
        super(invoice, documentTypeReference);
    }

    public static class ActionDomainEvent extends EstatioDomainModule.ActionDomainEvent<Invoice_emailPrelimLetterOrInvoiceDocAbstract> { }

    @Action(
            semantics = SemanticsOf.NON_IDEMPOTENT,
            domainEvent = ActionDomainEvent.class
    )
    @ActionLayout(
            contributed = Contributed.AS_ACTION
    )
    public Invoice $$(
            @ParameterLayout(named = "to:")
            final EmailAddress toChannel,
            @Parameter(
                    optionality = Optionality.OPTIONAL,
                    maxLength = CommunicationChannel.EmailType.MAX_LEN,
                    regexPattern = CommunicationChannel.EmailType.REGEX,
                    regexPatternReplacement = CommunicationChannel.EmailType.REGEX_DESC)
            @ParameterLayout(named = "cc:")
            final String cc,
            @Parameter(
                    optionality = Optionality.OPTIONAL,
                    maxLength = CommunicationChannel.EmailType.MAX_LEN,
                    regexPattern = CommunicationChannel.EmailType.REGEX,
                    regexPatternReplacement = CommunicationChannel.EmailType.REGEX_DESC)
            @ParameterLayout(named = "bcc:")
            final String bcc) throws IOException {

        final Document document = findDocument();
        createCommunication(document, toChannel, cc, bcc);

        return invoice;
    }

    public String disable$$() {
        final Document document = findDocument();
        if (document == null) {
            return "No document available to send";
        }
        return document_email(document).disable$$();
    }

    public Set<EmailAddress> choices0$$() {
        final Document document = findDocument();
        return document == null ? Collections.emptySet() : document_email(document).choices0$$();
    }

    public EmailAddress default0$$() {
        final Document document = findDocument();
        return document == null ? null : document_email(document).default0$$();
    }

    public String default1$$() {
        final Document document = findDocument();
        return document == null ? null : document_email(document).default1$$();
    }

    public String default2$$() {
        final Document document = findDocument();
        return document == null ? null :document_email(document).default2$$();
    }


}
