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

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.eventbus.ActionDomainEvent;
import org.apache.isis.applib.value.Blob;

import org.incode.module.communications.dom.impl.commchannel.PostalAddress;
import org.incode.module.communications.dom.mixins.DocumentConstants;
import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.dom.invoice.DocumentTypeData;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;

/**
 * Provides the ability to send an print.
 */
public abstract class InvoiceForLease_sendByPostPrelimLetterOrInvoiceDocAbstract extends
        InvoiceForLease_sendPrelimLetterOrInvoiceDocAbstract {

    public InvoiceForLease_sendByPostPrelimLetterOrInvoiceDocAbstract(final InvoiceForLease invoice, final DocumentTypeData documentTypeData) {
        super(invoice, documentTypeData);
    }

    public static class DomainEvent extends ActionDomainEvent<InvoiceForLease_sendByPostPrelimLetterOrInvoiceDocAbstract> { }

    @Action(
            semantics = SemanticsOf.NON_IDEMPOTENT,
            domainEvent = DomainEvent.class
    )
    @ActionLayout(
            contributed = Contributed.AS_ACTION
    )
    public Blob $$(
            @ParameterLayout(named = "to:")
            final PostalAddress toChannel) throws IOException {

        final Document document = findDocument();

        createPostalCommunicationAsSent(document, toChannel);

        final byte[] mergedBytes = mergePdfBytes(document);

        final String fileName = document.getName();
        return new Blob(fileName, DocumentConstants.MIME_TYPE_APPLICATION_PDF, mergedBytes);
    }

    public String disable$$() {
        final Document document = findDocument();
        if (document == null) {
            return "No document available to send";
        }
        return document_sendByPost(document).disableAct();
    }

    public Set<PostalAddress> choices0$$() {
        final Document document = findDocument();
        return document == null ? Collections.emptySet() : document_sendByPost(document).choices0Act();
    }

    public PostalAddress default0$$() {
        final Document document = findDocument();
        return document == null ? null : document_sendByPost(document).default0Act();
    }




}
