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

package org.estatio.document.dom.documents.binders;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.incode.module.communications.dom.mixins.DocumentConstants;
import org.incode.module.document.dom.impl.applicability.AttachmentAdvisorAbstract;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

import org.estatio.invoice.dom.Constants;

public class ForEmailCoverOfPrelimLetterOrInvoiceDocAttachToSame extends
        AttachmentAdvisorAbstract<Document> {

    public ForEmailCoverOfPrelimLetterOrInvoiceDocAttachToSame() {
        super(Document.class);
    }

    @Override
    protected List<PaperclipSpec> doAdvise(
            final DocumentTemplate documentTemplate,
            final Document prelimLetterOrInvoiceNoteDoc) {

        final String docTypeRef = prelimLetterOrInvoiceNoteDoc.getType().getReference();
        if (!Constants.DOC_TYPE_REF_PRELIM.equals(docTypeRef) && !Constants.DOC_TYPE_REF_INVOICE.equals(docTypeRef)) {
            throw new IllegalArgumentException(
                    String.format("Document must be a prelim letter or invoice note (provided document' type is '%s')", docTypeRef));
        }

        return Collections.singletonList(
                new PaperclipSpec(DocumentConstants.PAPERCLIP_ROLE_ATTACHMENT, prelimLetterOrInvoiceNoteDoc));

    }

    @Inject
    PaperclipRepository paperclipRepository;
}
