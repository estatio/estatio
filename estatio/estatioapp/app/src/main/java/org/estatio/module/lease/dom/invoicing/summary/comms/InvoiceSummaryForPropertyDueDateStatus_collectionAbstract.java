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
package org.estatio.module.lease.dom.invoicing.summary.comms;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.invoice.dom.Invoice;
import org.estatio.module.lease.dom.invoicing.summary.InvoiceSummaryForPropertyDueDateStatus;

public abstract class InvoiceSummaryForPropertyDueDateStatus_collectionAbstract<T extends DocAndCommAbstract<T>> {

    private final InvoiceSummaryForPropertyDueDateStatus invoiceSummary;
    private final DocAndCommAbstract.Factory.DncProvider<T> provider;

    public InvoiceSummaryForPropertyDueDateStatus_collectionAbstract(
            final InvoiceSummaryForPropertyDueDateStatus invoiceSummary,
            final DocAndCommAbstract.Factory.DncProvider<T> provider) {
        this.invoiceSummary = invoiceSummary;
        this.provider = provider;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @Collection()
    @CollectionLayout(defaultView = "table")
    public List<T> $$() {
        final List<Invoice<?>> invoices = (List)invoiceSummary.getInvoices();
        return docAndCommFactory.documentsAndCommunicationsFor(invoices, provider);
    }

    @Inject
    DocAndCommForPrelimLetter.Factory docAndCommFactory;
}
