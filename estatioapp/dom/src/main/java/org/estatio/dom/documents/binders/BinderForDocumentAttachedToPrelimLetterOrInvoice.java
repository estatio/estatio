/*
 *
 *  Copyright 2012-2015 Eurocommercial Properties NV
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

package org.estatio.dom.documents.binders;

import java.util.Collections;

import javax.inject.Inject;

import org.incode.module.documents.dom.impl.applicability.Binder;
import org.incode.module.documents.dom.impl.docs.Document;
import org.incode.module.documents.dom.impl.docs.DocumentTemplate;
import org.incode.module.documents.dom.impl.paperclips.PaperclipRepository;

import org.estatio.dom.invoice.Invoice;

import lombok.Data;

/**
 * For use with email covering notes for invoices.
 */
public class BinderForDocumentAttachedToPrelimLetterOrInvoice implements Binder {

    @Override
    public Binding newBinding(
            final DocumentTemplate documentTemplate,
            final Object domainObject,
            final String additionalTextIfAny) {

        if(!(domainObject instanceof Document)) {
            throw new IllegalArgumentException("Domain object must be of type Document");
        }
        Document document = (Document) domainObject;

        final Invoice invoice = paperclipRepository.paperclipAttaches(document, Invoice.class);

        final DataModel dataModel = new DataModel();
        dataModel.setInvoice(invoice);
        dataModel.setDocument(document);
        dataModel.setAdditionalText(additionalTextIfAny);

        return new Binding(dataModel, Collections.singletonList(document));
    }

    @Data
    public static class DataModel {
        Invoice invoice;
        Document document;
        String additionalText;
    }

    @Inject
    PaperclipRepository paperclipRepository;
}
