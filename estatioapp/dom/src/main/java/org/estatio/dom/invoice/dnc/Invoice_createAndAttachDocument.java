
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
import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.Mixin;

import org.incode.module.document.dom.impl.docs.DocumentTemplate;
import org.incode.module.document.dom.mixins.T_createAndAttachDocumentAndRender;

import org.estatio.dom.invoice.Constants;
import org.estatio.dom.invoice.Invoice;

@Mixin
public class Invoice_createAndAttachDocument extends T_createAndAttachDocumentAndRender<Invoice> {

    public Invoice_createAndAttachDocument(final Invoice domainObject) {
        super(domainObject);
    }

    public Object $$(DocumentTemplate template) throws IOException {
        super.$$(template);
        return domainObject;
    }

    @Override
    public List<DocumentTemplate> choices0$$() {
        List<DocumentTemplate> documentTemplates = super.choices0$$();
        if(Invoice.Predicates.isChangeable().apply(domainObject)) {
            // cannot send an invoice note
            documentTemplates = Lists.newArrayList(
                 FluentIterable.from(documentTemplates)
                               .filter(Predicates.not(hasDocumentType(Constants.DOC_TYPE_REF_INVOICE))
                ).toList()
            );
        }
        return documentTemplates;
    }

    public DocumentTemplate default0$$() {
        final List<DocumentTemplate> documentTemplates = choices0$$();
        return documentTemplates.size() == 1 ? documentTemplates.get(0) : null;
    }


    public static Predicate<DocumentTemplate> hasDocumentType(final String docTypeRefInvoice) {
        return template -> template.getType().getReference().equals(docTypeRefInvoice);
    }



}
