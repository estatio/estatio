
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
package org.estatio.dom.invoice.dnc;

import java.io.IOException;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;

import org.estatio.dom.invoice.Constants;
import org.estatio.dom.invoice.Invoice;

@Mixin
public class Invoice_prepareInvoiceDoc extends Invoice_prepareAbstract{

    public Invoice_prepareInvoiceDoc(final Invoice invoice) {
        super(invoice, Constants.DOC_TYPE_REF_INVOICE);
    }


    @MemberOrder(name = "invoiceDocs", sequence = "2")
    public Invoice $$() throws IOException {
        return super.$$();
    }

    @Override public String disable$$() {
        final String reasonIfAny = super.disable$$();
        if(reasonIfAny != null) {
            return reasonIfAny;
        }
        if(Invoice.Predicates.isChangeable().apply(invoice)) {
            return "Invoice must be approved/invoiced first";
        }
        return null;
    }

}
