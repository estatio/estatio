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

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.InvokeOn;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.scratchpad.Scratchpad;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.pdfbox.dom.service.PdfBoxService;

import org.incode.module.base.dom.MimeTypes;
import org.incode.module.communications.dom.mixins.DocumentPredicates;
import org.incode.module.communications.dom.mixins.Document_communicationAttachments;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentSort;
import org.incode.module.document.dom.impl.docs.DocumentState;

import org.estatio.module.invoice.dom.DocumentTypeData;


/**
 * It doesn't seem to be possible to combine this with {@link DocAndCommAbstract_downloadSelected};
 * the bulk action must return a java.lang.Object whereas this must return a Blob.
 */
public abstract class DocAndCommAbstract_download<T extends DocAndCommAbstract<T>> extends DocAndCommAbstract_abstract<T> {

    final String fileName;

    public DocAndCommAbstract_download(final T docAndComm, final DocumentTypeData documentTypeData) {
        super(docAndComm, documentTypeData);
        this.fileName = documentTypeData.getMergedFileName();
    }

    @Action(
            semantics = SemanticsOf.SAFE,
            invokeOn = InvokeOn.OBJECT_ONLY
    )
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public Blob act() throws IOException {

        final List<byte[]> pdfBytes = Lists.newArrayList();

        final Document document = getDocument();
        appendBytes(document, pdfBytes);

        final List<Document> supportingDocs = attachmentsProvider.attachmentsFor(document);
        for (Document supportingDoc : supportingDocs) {
            appendBytes(supportingDoc, pdfBytes);
        }

        final byte[] mergedBytes = pdfBoxService.merge(pdfBytes.toArray(new byte[][] {}));
        return new Blob(fileName, MimeTypes.APPLICATION_PDF, mergedBytes);
    }

    public String disableAct() {
        // guard for when invoked on single DnC; ignored if invoke in bulk
        return getDocument() == null || getDocument().getState() != DocumentState.RENDERED
                ? "No document has been prepared & rendered"
                : null;
    }

    private static void appendBytes(final Document document, final List<byte[]> pdfBytes) {
        if (document.getState() != DocumentState.RENDERED ||
            !DocumentPredicates.isPdfAndBlob().apply(document)) {
            return;
        }

        final DocumentSort documentSort = document.getSort();
        final byte[] bytes = documentSort.asBytes(document);
        pdfBytes.add(bytes);
    }


    @Inject
    Document_communicationAttachments.Provider attachmentsProvider;

    @Inject
    PdfBoxService pdfBoxService;

    @Inject
    Scratchpad scratchpad;

}
