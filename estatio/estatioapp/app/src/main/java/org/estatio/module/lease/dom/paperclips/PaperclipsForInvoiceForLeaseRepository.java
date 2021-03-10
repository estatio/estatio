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
package org.estatio.module.lease.dom.paperclips;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.jdosupport.IsisJdoSupport;

import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

import org.estatio.module.invoice.dom.paperclips.PaperclipForInvoice;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class PaperclipsForInvoiceForLeaseRepository {

    public Stream<PaperclipForInvoice> streamPaperclips(final Optional<InvoiceForLease> invoiceIfAny) {
        return asStream(invoiceIfAny)
                .flatMap(invoice -> {
                    final List<Paperclip> paperclips = paperclipRepository.findByAttachedTo(invoice);
                    return paperclips.stream()
                            .filter(PaperclipForInvoice.class::isInstance)
                            .map(PaperclipForInvoice.class::cast);
                });
    }

    static <T> Stream<T> asStream(final Optional<T> optional) {
        return optional.map(Stream::of).orElseGet(Stream::empty);
    }

    /**
     * It's necessary to use the low-level API because the candidate class ({@link PaperclipForInvoice}) and the
     * result class ({@link InvoiceForLease}) are different.
     */
    @Programmatic
    public List<InvoiceForLease> findInvoicesByYearWithSupportingDocuments(final int year) {

        final PersistenceManager jdoPersistenceManager = isisJdoSupport.getJdoPersistenceManager();
        final Query query = jdoPersistenceManager.newNamedQuery(
                PaperclipForInvoice.class,
                "findInvoicesByInvoiceDateBetweenWithSupportingDocuments");
        query.setResultClass(InvoiceForLease.class);

        try {
            final LocalDate invoiceDateFrom = new LocalDate(year,1,1);
            final LocalDate invoiceDateTo = invoiceDateFrom.plusYears(1);

            final List results = (List) query.executeWithMap(ImmutableMap.of(
                    "invoiceDateFrom", invoiceDateFrom,
                    "invoiceDateTo", invoiceDateTo
            ));
            return Lists.newArrayList(results);
        } finally {
            query.closeAll();
        }
    }

    @Inject
    IsisJdoSupport isisJdoSupport;

    @Inject
    PaperclipRepository paperclipRepository;

}
