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
package org.estatio.dom.document.documents.binders;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.incode.module.document.dom.impl.applicability.AttachmentAdvisor;
import org.incode.module.document.dom.impl.applicability.AttachmentAdvisorAbstract;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentAbstract;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.lease.invoicing.dnc.PaperclipRoleNames;

public class ForInvoiceDocOfInvoiceAttachToInvoiceAndAnyReceipts extends
        AttachmentAdvisorAbstract<Invoice> {

    public ForInvoiceDocOfInvoiceAttachToInvoiceAndAnyReceipts() {
        super(Invoice.class);
    }

    @Override
    protected List<AttachmentAdvisor.PaperclipSpec> doAdvise(
            final DocumentTemplate documentTemplate,
            final Invoice invoice,
            final Document createdDocument) {

        final List<PaperclipSpec> paperclipSpecs = Lists.newArrayList();

        paperclipSpecs.add(new PaperclipSpec(null, invoice, createdDocument));

        // also, copy over any receipts attached to the invoice (but with those receipts attached to the
        // new invoice note, rather than the other way around)
        final List<Paperclip> paperclips = paperclipRepository.findByAttachedTo(invoice);
        for (Paperclip paperclip : paperclips) {
            if(PaperclipRoleNames.INVOICE_RECEIPT.equals(paperclip.getRoleName())) {
                final DocumentAbstract paperclipDocAbs = paperclip.getDocument();
                if(paperclipDocAbs instanceof Document) {
                    Document paperclipDocument = (Document) paperclipDocAbs;
                    paperclipSpecs.add(
                            new PaperclipSpec(
                                    PaperclipRoleNames.INVOICE_DOCUMENT_SUPPORTED_BY,
                                    createdDocument,
                                    paperclipDocument
));

                }
            }
        }

        return paperclipSpecs;
    }

    @Inject
    PaperclipRepository paperclipRepository;

}
