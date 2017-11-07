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
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.email.EmailService;
import org.apache.isis.applib.services.eventbus.ActionDomainEvent;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannel;
import org.incode.module.communications.dom.impl.commchannel.EmailAddress;
import org.incode.module.communications.dom.impl.comms.Communication;
import org.incode.module.communications.dom.mixins.Document_sendByEmail;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentAbstract;
import org.incode.module.document.dom.impl.docs.DocumentState;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

import org.estatio.dom.invoice.DocumentTypeData;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;

/**
 * Provides the ability to send an email.
 *
 * TODO: REVIEW: this mixin could in theory be inlined, but maybe we want to keep invoices and documents decoupled?
 */
@Mixin
public class InvoiceForLease_sendByEmail extends InvoiceForLease_sendAbstract {

    public InvoiceForLease_sendByEmail(final InvoiceForLease invoice) {
        super(invoice);
    }

    public static class DomainEvent extends ActionDomainEvent<InvoiceForLease_sendByEmail> { }

    @Action(
            semantics = SemanticsOf.NON_IDEMPOTENT,
            domainEvent = DomainEvent.class
    )
    @ActionLayout(
            cssClassFa = "at",
            contributed = Contributed.AS_ACTION
    )
    @MemberOrder(name = "documents", sequence = "4.1")
    public Communication $$(
            final Document document,
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

        final Communication communication = createEmailCommunication(document, toChannel, cc, cc2, cc3, bcc, bcc2);

        return communication;
    }

    public String disable$$() {
        if (emailService == null || !emailService.isConfigured()) {
            return "Email service not configured";
        }
        if(choices0$$().isEmpty()) {
            return "No documents available to send";
        }
        return null;
    }

    public Document default0$$() {
        final List<Document> documents = choices0$$();
        return documents.size() == 1 ? documents.get(0): null;
    }

    public List<Document> choices0$$() {
        final List<Paperclip> paperclips = paperclipRepository.findByAttachedTo(invoice);
        final List<Document> documents = Lists.newArrayList();
        for (Paperclip paperclip : paperclips) {
            final DocumentAbstract documentAbs = paperclip.getDocument();
            if (!(documentAbs instanceof Document)) {
                continue;
            }
            final Document document = (Document) documentAbs;
            if (document.getState() != DocumentState.RENDERED) {
                continue;
            }
            if (!DocumentTypeData.isPrimaryType(document)) {
                continue;
            }
            final Document_sendByEmail document_email = document_sendByEmail(document);
            if(document_email.disableAct() != null) {
                continue;
            }
            documents.add(document);
        }
        return documents;
    }

    public Set<EmailAddress> choices1$$(final Document document) {
        return document == null ? Collections.emptySet() : document_sendByEmail(document).choices0Act();
    }

    // TODO: currently not properly supported by Isis, but does no harm
    @Programmatic
    public EmailAddress default1$$(final Document document) {
        return document == null ? null : document_sendByEmail(document).default0Act();
    }

    // TODO: currently not properly supported by Isis, but does no harm
    @Programmatic
    public String default2$$(final Document document) {
        return document == null ? null : document_sendByEmail(document).default1Act();
    }

    // TODO: currently not properly supported by Isis, but does no harm
    @Programmatic
    public String default5$$(final Document document) {
        return document == null ? null : document_sendByEmail(document).default4Act();
    }

    @Inject
    PaperclipRepository paperclipRepository;

    @Inject
    EmailService emailService;

}
