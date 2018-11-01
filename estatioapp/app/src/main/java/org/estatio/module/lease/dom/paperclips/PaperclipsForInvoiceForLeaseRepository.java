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

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

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

    @Inject
    PaperclipRepository paperclipRepository;

}
