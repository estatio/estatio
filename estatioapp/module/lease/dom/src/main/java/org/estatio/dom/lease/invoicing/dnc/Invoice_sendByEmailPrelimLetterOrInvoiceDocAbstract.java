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
package org.estatio.dom.lease.invoicing.dnc;

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
import org.apache.isis.applib.services.eventbus.ActionDomainEvent;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannel;
import org.incode.module.communications.dom.impl.commchannel.EmailAddress;
import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.dom.invoice.DocumentTypeData;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.lease.invoicing.InvoiceForLease;

public abstract class Invoice_sendByEmailPrelimLetterOrInvoiceDocAbstract extends Invoice_sendPrelimLetterOrInvoiceDocAbstract {

    public Invoice_sendByEmailPrelimLetterOrInvoiceDocAbstract(final InvoiceForLease invoice, final DocumentTypeData documentTypeData) {
        super(invoice, documentTypeData);
    }

    public static class DomainEvent extends ActionDomainEvent<Invoice_sendByEmailPrelimLetterOrInvoiceDocAbstract> { }

    @Action(
            semantics = SemanticsOf.NON_IDEMPOTENT,
            domainEvent = DomainEvent.class
    )
    @ActionLayout(
            cssClassFa = "at",
            contributed = Contributed.AS_ACTION
    )
    public Invoice $$(
            @ParameterLayout(named = "to:")
            final EmailAddress toChannel,
            @Parameter(
                    optionality = Optionality.OPTIONAL,
                    maxLength = CommunicationChannel.EmailType.Meta.MAX_LEN,
                    regexPattern = CommunicationChannel.EmailType.Meta.REGEX,
                    regexPatternReplacement = CommunicationChannel.EmailType.Meta.REGEX_DESC)
            @ParameterLayout(named = "cc:")
            final String cc,
            @Parameter(
                    optionality = Optionality.OPTIONAL,
                    maxLength = CommunicationChannel.EmailType.Meta.MAX_LEN,
                    regexPattern = CommunicationChannel.EmailType.Meta.REGEX,
                    regexPatternReplacement = CommunicationChannel.EmailType.Meta.REGEX_DESC)
            @ParameterLayout(named = "cc (2):")
            final String cc2,
            @Parameter(
                    optionality = Optionality.OPTIONAL,
                    maxLength = CommunicationChannel.EmailType.Meta.MAX_LEN,
                    regexPattern = CommunicationChannel.EmailType.Meta.REGEX,
                    regexPatternReplacement = CommunicationChannel.EmailType.Meta.REGEX_DESC)
            @ParameterLayout(named = "cc (3):")
            final String cc3,
            @Parameter(
                    optionality = Optionality.OPTIONAL,
                    maxLength = CommunicationChannel.EmailType.Meta.MAX_LEN,
                    regexPattern = CommunicationChannel.EmailType.Meta.REGEX,
                    regexPatternReplacement = CommunicationChannel.EmailType.Meta.REGEX_DESC)
            @ParameterLayout(named = "bcc:")
            final String bcc,
            @Parameter(
                    optionality = Optionality.OPTIONAL,
                    maxLength = CommunicationChannel.EmailType.Meta.MAX_LEN,
                    regexPattern = CommunicationChannel.EmailType.Meta.REGEX,
                    regexPatternReplacement = CommunicationChannel.EmailType.Meta.REGEX_DESC)
            @ParameterLayout(named = "bcc (2):")
            final String bcc2
            ) throws IOException {

        final Document document = findDocument();
        createEmailCommunication(document, toChannel, cc, cc2, cc3, bcc, bcc2);

        return invoice;
    }

    public String disable$$() {
        final Document document = findDocument();
        if (document == null) {
            return "No document available to send";
        }
        return document_sendByEmail(document).disableAct();
    }

    public Set<EmailAddress> choices0$$() {
        final Document document = findDocument();
        return document == null ? Collections.emptySet() : document_sendByEmail(document).choices0Act();
    }

    public EmailAddress default0$$() {
        final Document document = findDocument();
        return document == null ? null : document_sendByEmail(document).default0Act();
    }

    public String default1$$() {
        final Document document = findDocument();
        return document == null ? null : document_sendByEmail(document).default1Act();
    }

    public String default4$$() {
        final Document document = findDocument();
        return document == null ? null : document_sendByEmail(document).default4Act();
    }


}
