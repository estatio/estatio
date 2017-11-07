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
package org.estatio.module.document.dom.binders;

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

import org.estatio.module.invoice.dom.DocumentTypeData;
import org.estatio.module.invoice.dom.Invoice;
import org.estatio.module.lease.dom.invoicing.comms.PaperclipRoleNames;

public class ForPrimaryDocOfInvoiceAttachToInvoiceAndAnyRelevantSupportingDocuments extends
        AttachmentAdvisorAbstract<Invoice> {

    public ForPrimaryDocOfInvoiceAttachToInvoiceAndAnyRelevantSupportingDocuments() {
        super(Invoice.class);
    }

    @Override
    protected List<AttachmentAdvisor.PaperclipSpec> doAdvise(
            final DocumentTemplate documentTemplate,
            final Invoice invoice,
            final Document createdDocument) {

        final List<PaperclipSpec> paperclipSpecs = Lists.newArrayList();

        paperclipSpecs.add(new PaperclipSpec(null, invoice, createdDocument));

        // not every supporting doc type supports each type of primary document
        // for example, TAX_REGISTER supports invoices (but not PLs), whereas CALCULATION is other way around.
        // we therefore need to filter the supporting documents that we find attached to the invoice.
        //
        // to start with, we get hold of the set of doc type (data)s that _do_ support the primary doc just created
        DocumentTypeData primaryDocTypeData = DocumentTypeData.docTypeDataFor(createdDocument);
        List<DocumentTypeData> supportingDocTypeDatasForThisPrimaryDoc = DocumentTypeData.supports(primaryDocTypeData);

        // copy over all relevant supporting attached to this primary document (invoice or prelim letter)
        final List<Paperclip> paperclips = paperclipRepository.findByAttachedTo(invoice);
        for (Paperclip paperclip : paperclips) {
            if(PaperclipRoleNames.SUPPORTING_DOCUMENT.equals(paperclip.getRoleName())) {
                final DocumentAbstract supportingDocAbs = paperclip.getDocument();
                if(supportingDocAbs instanceof Document) {

                    final Document supportingDoc = (Document) supportingDocAbs;
                    final DocumentTypeData supportingDocTypeData = DocumentTypeData.docTypeDataFor(supportingDoc);

                    // (and here is where filter out to only attach those that are relevant)
                    if(supportingDocTypeDatasForThisPrimaryDoc.contains(supportingDocTypeData)) {

                        // note that the supporting documents point to the primary doc, rather than the other way around
                        paperclipSpecs.add(
                                new PaperclipSpec(
                                        PaperclipRoleNames.INVOICE_DOCUMENT_SUPPORTED_BY,
                                        createdDocument,
                                        supportingDoc
                                )
                        );
                    }
                }
            }
        }

        return paperclipSpecs;
    }

    @Inject
    PaperclipRepository paperclipRepository;

}
