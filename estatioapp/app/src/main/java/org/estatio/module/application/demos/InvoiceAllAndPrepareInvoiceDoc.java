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
package org.estatio.module.application.demos;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import com.google.common.io.Resources;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.fixturescripts.DiscoverableFixtureScript;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.fakedata.dom.FakeDataService;

import org.incode.module.base.dom.MimeTypes;

import org.estatio.module.bankmandate.dom.Scheme;
import org.estatio.module.bankmandate.dom.SequenceType;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.financial.dom.BankAccountRepository;
import org.estatio.module.invoice.dom.InvoiceRepository;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;
import org.estatio.module.lease.dom.invoicing.comms.InvoiceForLease_attachSupportingDocument;
import org.estatio.module.lease.dom.invoicing.comms.InvoiceForLease_prepareInvoiceDoc;

@DomainObject(
        objectType = "org.estatio.module.application.demos.InvoiceAllAndPrepareInvoiceDoc"
)
public class InvoiceAllAndPrepareInvoiceDoc extends DiscoverableFixtureScript {

    public InvoiceAllAndPrepareInvoiceDoc() {
        super(null, null);
    }

    @Override
    protected void execute(ExecutionContext executionContext) {

        final List<Blob> blobs = Stream.of("PR20180109UK.pdf", "PR20180305UK.pdf", "PR20182109UK.pdf").map(resourceName -> {
            final URL url = Resources.getResource(getClass(), resourceName);
            try {
                return new Blob(resourceName, MimeTypes.APPLICATION_PDF, Resources.toByteArray(url));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());

        invoiceRepository.allInvoices().stream().filter(InvoiceForLease.class::isInstance).map(InvoiceForLease.class::cast).forEach(invoiceForLease -> {

            final Lease lease = invoiceForLease.getLease();
            if(lease.getPaidBy() == null) {
                final List<BankAccount> bankAccounts = lease.choices0NewMandate();
                if(!bankAccounts.isEmpty()) {
                    final BankAccount bankAccount = bankAccounts.get(0);
                    final LocalDate startDate = lease.default2NewMandate();
                    final LocalDate endDate = lease.default3NewMandate();
                    final LocalDate signatureDate = lease.default2NewMandate();
                    wrapperFactory.wrapTry(lease).newMandate(
                            bankAccount, lease.getReference(), startDate, endDate,
                            SequenceType.RECURRENT, Scheme.CORE, signatureDate);
                }
            }
            wrapperFactory.wrapTry(mixin(InvoiceForLease._approve.class, invoiceForLease)).$$();
            wrapperFactory.wrapTry(mixin(InvoiceForLease._invoice.class, invoiceForLease)).$$(invoiceForLease.getDueDate());
            try {
                wrapperFactory.wrapTry(mixin(InvoiceForLease_prepareInvoiceDoc.class, invoiceForLease)).$$();
            } catch (IOException e) {
                // ignore
            }

            blobs.forEach(blob -> attachDoc(invoiceForLease, blob));

        });
    }

    @Inject
    BankAccountRepository bankAccountRepository;

    private void attachDoc(final InvoiceForLease invoiceForLease, final Blob blob) {
        final InvoiceForLease_attachSupportingDocument mixin =
                mixin(InvoiceForLease_attachSupportingDocument.class, invoiceForLease);
        try {
            wrapperFactory.wrapTry(mixin).$$(
                                    randomOf(mixin.choices0$$()),
                                    blob,
                                    blob.getName(),
                                    randomOf(mixin.choices3$$()));
        } catch (IOException e) {
            // ignore
        }
    }

    private <T> T randomOf(final List<T> documentTypes) {
        return fakeDataService.collections().anyOf(documentTypes);
    }

    @Inject
    InvoiceRepository invoiceRepository;
    @Inject
    FakeDataService fakeDataService;


}
