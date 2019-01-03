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
package org.estatio.module.lease.contentmapping;

import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.canonical.invoicenumbers.v2.InvoiceNumberType;
import org.estatio.canonical.invoicenumbers.v2.InvoiceNumbersDto;
import org.estatio.module.base.platform.applib.DtoFactoryAbstract;
import org.estatio.module.invoice.dom.Invoice;
import org.estatio.module.party.dom.Party;

@DomainService(nature = NatureOfService.DOMAIN)
public class InvoiceNumbersDtoFactory extends DtoFactoryAbstract<List, InvoiceNumbersDto> {

    public InvoiceNumbersDtoFactory() {
        super(List.class, InvoiceNumbersDto.class);
    }

    @Override
    protected InvoiceNumbersDto newDto(final List documents) {
        return internalNewDto(documents);
    }

    InvoiceNumbersDto internalNewDto(final List<Invoice> invoices) {
        final InvoiceNumbersDto invoiceNumbers = new InvoiceNumbersDto();
        invoiceNumbers.setMajorVersion("2");
        invoiceNumbers.setMinorVersion("1");

        invoices.forEach(invoice -> invoiceNumbers.getInvoiceNumbers().add(newDto(invoice)));
        return invoiceNumbers;
    }

    private InvoiceNumberType newDto(final Invoice invoice) {
        final InvoiceNumberType invoiceDto = new InvoiceNumberType();
        invoiceDto.setSelf(mappingHelper.oidDtoFor(invoice));
        invoiceDto.setInvoiceDate(asXMLGregorianCalendar(invoice.getInvoiceDate()));
        invoiceDto.setInvoiceNumber(invoice.getInvoiceNumber());
        invoiceDto.setInvoiceYear(invoiceYearOf(invoice));
        invoiceDto.setSellerReference(referenceOf(invoice.getSeller()));
        invoiceDto.setBuyerReference(referenceOf(invoice.getBuyer()));
        return invoiceDto;
    }

    private static int invoiceYearOf(final Invoice invoice) {
        final LocalDate date = invoice.getInvoiceDate();
        return date != null ? date.getYear() : 0;
    }

    private static String referenceOf(final Party seller) {
        return seller != null ? seller.getReference() : null;
    }

}
