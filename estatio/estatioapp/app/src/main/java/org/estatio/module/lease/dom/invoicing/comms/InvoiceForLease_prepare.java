
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
import java.util.List;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.Mixin;

import org.incode.module.document.dom.impl.docs.DocumentTemplate;
import org.incode.module.document.dom.mixins.T_createAndAttachDocumentAndRender;

import org.estatio.module.invoice.dom.DocumentTypeData;
import org.estatio.module.invoice.dom.Invoice;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;

import static com.google.common.base.Predicates.not;

/**
 * TODO: REVIEW: this mixin could in theory be inlined, but it's a lot of functionality in its own right; and maybe we want to keep invoices and documents decoupled?
 */
@Mixin(method = "act")
public class InvoiceForLease_prepare extends T_createAndAttachDocumentAndRender<InvoiceForLease> {

    public InvoiceForLease_prepare(final InvoiceForLease invoiceForLease) {
        super(invoiceForLease);
    }

    public Object act(DocumentTemplate template) throws IOException {
        super.act(template);
        return domainObject;
    }

    @Override
    public List<DocumentTemplate> choices0Act() {
        List<DocumentTemplate> documentTemplates = super.choices0Act();
        if(Invoice.Predicates.isChangeable().apply(domainObject)) {
            // cannot send an invoice note
            documentTemplates = Lists.newArrayList(
                 FluentIterable.from(documentTemplates)
                               .filter(not(DocumentTypeData.INVOICE.ofTemplate())
                ).toList()
            );
        }
        return documentTemplates;
    }

    public DocumentTemplate default0Act() {
        final List<DocumentTemplate> documentTemplates = choices0Act();
        return documentTemplates.size() == 1 ? documentTemplates.get(0) : null;
    }

}
