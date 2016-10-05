/*
 *  Copyright 2016 Eurocommercial Properties NV
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
package org.estatio.fixture.documents;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.services.clock.ClockService;

import org.incode.module.documents.dom.impl.docs.Document;
import org.incode.module.documents.dom.impl.docs.DocumentTemplate;
import org.incode.module.documents.dom.impl.rendering.RenderingStrategy;
import org.incode.module.documents.dom.impl.rendering.RenderingStrategyRepository;
import org.incode.module.documents.dom.impl.types.DocumentType;
import org.incode.module.documents.dom.impl.types.DocumentTypeRepository;
import org.incode.module.documents.fixture.DocumentTemplateFSAbstract;

import org.estatio.dom.documents.binders.BinderForDocumentAttachedToInvoice;
import org.estatio.dom.invoice.Constants;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForGlobal;

public class DocumentTypeAndTemplateFSForInvoiceEmailCoverNote extends DocumentTemplateFSAbstract {

    public static final String TYPE_REF = Constants.EMAIL_COVER_NOTE_DOCUMENT_TYPE;

    public static final String AT_PATH = ApplicationTenancyForGlobal.PATH;

    public static final String TEMPLATE_NAME = "Invoice Email Cover Note";
    public static final String TEMPLATE_MIME_TYPE = "text/plain";
    public static final String FILE_SUFFIX = ".txt";

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, new ApplicationTenancyForGlobal());
        executionContext.executeChild(this, new RenderingStrategies());

        upsertType(TYPE_REF, "Invoice Email Cover Note", executionContext);

        final DocumentType documentType = documentTypeRepository.findByReference(TYPE_REF);
        final RenderingStrategy freemarkerRenderingStrategy =
                renderingStrategyRepository.findByReference(RenderingStrategies.REF_FMK);
        final LocalDate date = clockService.now();

        final DocumentTemplate documentTemplate = upsertDocumentTextTemplate(
                documentType, date, AT_PATH, FILE_SUFFIX, false, TEMPLATE_NAME, TEMPLATE_MIME_TYPE,
                "${invoice.lease.reference}: invoice ${invoice.number} cover note",
                freemarkerRenderingStrategy,
                "Dear Sir/Madam\n"
                + "With respect to your lease ${invoice.lease.reference}, please find enclosed invoice ${invoice.number}, due on ${invoice.dueDate}.\n"
                + "${additionalText}\n"
                + "Best Regards",
                freemarkerRenderingStrategy,
                executionContext);

        documentTemplate.applicable(Document.class, BinderForDocumentAttachedToInvoice.class);

        executionContext.addResult(this, documentTemplate);
    }


    @Inject
    ClockService clockService;

    @Inject
    DocumentTypeRepository documentTypeRepository;

    @Inject
    RenderingStrategyRepository renderingStrategyRepository;


}
