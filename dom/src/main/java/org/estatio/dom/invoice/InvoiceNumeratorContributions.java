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
package org.estatio.dom.invoice;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.NotContributed;
import org.apache.isis.applib.annotation.NotContributed.As;
import org.apache.isis.applib.annotation.NotInServiceMenu;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.asset.Property;
import org.estatio.dom.numerator.Numerator;

@Hidden
public class InvoiceNumeratorContributions extends EstatioDomainService<Invoice> {

    public InvoiceNumeratorContributions() {
        super(InvoiceNumeratorContributions.class, Invoice.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.IDEMPOTENT)
    @NotInServiceMenu
    @NotContributed(As.ACTION)
    public Numerator lastInvoiceNumber(
            final Property property) {
        return invoices.findInvoiceNumberNumerator(property);
    }

    // //////////////////////////////////////

    private Invoices invoices;

    public void injectInvoices(final Invoices invoices) {
        this.invoices = invoices;
    }

}
