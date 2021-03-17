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
package org.estatio.module.lease.restapi;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.Paperclip;

import org.estatio.module.invoice.dom.DocumentTypeData;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;
import org.estatio.module.lease.dom.invoicing.InvoiceForLeaseRepository;
import org.estatio.module.lease.dom.paperclips.PaperclipsForInvoiceForLeaseRepository;
import org.estatio.module.party.dom.Party;

@DomainService(
        nature = NatureOfService.VIEW_REST_ONLY,
        objectType = "lease.SupportingDocumentService"
)
public class SupportingDocumentService {

    @Action(semantics = SemanticsOf.SAFE)
    public List<InvoiceForLease> findInvoicesWithSupportingDocuments(
            final int year,
            @Nullable
            @ParameterLayout(describedAs = "in Coda, corresponds to cmpCode")
            final String sellerReference
    ) {
        final List<InvoiceForLease> invoices = paperclipsForInvoiceForLeaseRepository
                .findInvoicesByYearWithSupportingDocuments(year).stream()
                .filter(invoice -> sellerReference == null || equalsRef(sellerReference, invoice.getSeller()))
                .collect(Collectors.toList());
        return invoices;
    }

    @Action(semantics = SemanticsOf.SAFE)
    public List<Document> findSupportingDocuments(
            final String invoiceNumber,
            final int year,
            @ParameterLayout(describedAs = "in Coda, corresponds to cmpCode")
            final String sellerReference,
            @ParameterLayout(describedAs = "in Coda, corresponds to el6 of the summary line")
            final String buyerReference) {
        final Optional<InvoiceForLease> invoiceIfAny =
                invoiceForLeaseRepository.findInvoiceByInvoiceNumber(invoiceNumber, year)
                .filter(invoiceForLease ->
                            equalsRef(sellerReference, invoiceForLease.getSeller()) &&
                            equalsRef(buyerReference, invoiceForLease.getBuyer()));
        return paperclipsForInvoiceForLeaseRepository.streamPaperclips(invoiceIfAny)
                .map(Paperclip::getDocument)
                .filter(Document.class::isInstance)
                .map(Document.class::cast)
                .filter(SupportingDocumentService::supportsInvoice)
                .collect(Collectors.toList());
    }

    static boolean equalsRef(final String ref, final Party party) {
        return ref != null && party != null && Objects.equals(ref, party.getReference());
    }

    private static boolean supportsInvoice(final Document document) {
        final DocumentTypeData documentTypeData = DocumentTypeData.docTypeDataFor(document);
        return documentTypeData.getSupports() == DocumentTypeData.INVOICE;
    }

    @Inject
    InvoiceForLeaseRepository invoiceForLeaseRepository;

    @Inject
    PaperclipsForInvoiceForLeaseRepository paperclipsForInvoiceForLeaseRepository;


}
