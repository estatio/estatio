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

import org.estatio.module.lease.dom.invoicing.InvoiceForLease;

public abstract class InvoiceForLease_collectionAbstract<T extends DocAndCommAbstract<T>> {

    private final InvoiceForLease invoiceForLease;
    private DocAndCommAbstract.Factory.DncProvider<T> provider;

    public InvoiceForLease_collectionAbstract(final InvoiceForLease invoice, final DocAndCommAbstract.Factory.DncProvider<T> provider) {
        this.invoiceForLease = invoice;
        this.provider = provider;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @Collection()
    @CollectionLayout(defaultView = "table")
    public List<T> $$() {
        return docAndCommFactory.documentsAndCommunicationsFor(invoiceForLease, i -> provider.instantiate(i));
    }

    @Inject
    DocAndCommAbstract.Factory docAndCommFactory;
}
