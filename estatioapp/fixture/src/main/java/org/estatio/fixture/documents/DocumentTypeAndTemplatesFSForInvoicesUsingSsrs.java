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
package org.estatio.fixture.documents;

import java.io.IOException;
import java.net.URL;

import javax.inject.Inject;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import org.joda.time.LocalDate;

import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.value.Clob;

import org.incode.module.document.dom.impl.applicability.AttachmentAdvisor;
import org.incode.module.document.dom.impl.applicability.RendererModelFactory;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;
import org.incode.module.document.dom.impl.rendering.RenderingStrategy;
import org.incode.module.document.dom.impl.rendering.RenderingStrategyRepository;
import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.fixture.DocumentTemplateFSAbstract;

import org.estatio.dom.document.documents.binders.AttachToNone;
import org.estatio.dom.document.documents.binders.ForInvoiceDocOfInvoiceAttachToInvoiceAndAnyReceipts;
import org.estatio.dom.document.documents.binders.ForPrelimLetterOfInvoiceAttachToSame;
import org.estatio.dom.document.documents.binders.FreemarkerModelOfPrelimLetterOrInvoiceDocForEmailCover;
import org.estatio.dom.document.documents.binders.StringInterpolatorToSsrsUrlOfInvoice;
import org.estatio.dom.document.documents.binders.StringInterpolatorToSsrsUrlOfInvoiceSummary;
import org.estatio.dom.invoice.DocumentTypeData;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.lease.invoicing.viewmodel.InvoiceSummaryForPropertyDueDateStatus;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForGlobal;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForIt;

public class DocumentTypeAndTemplatesFSForInvoicesUsingSsrs extends DocumentTemplateFSAbstract {


    public static final String URL = "${reportServerBaseUrl}";

    public static final String NAME_TEXT_PRELIM_LETTER_GLOBAL = loadResource("PrelimLetterName.txt");
    public static final String NAME_TEXT_PRELIM_LETTER_ITA = loadResource("PrelimLetterName-ITA.txt");

    public static final String NAME_TEXT_INVOICE_ITA = loadResource("InvoiceName.txt");
    public static final String NAME_TEXT_INVOICE_GLOBAL = loadResource("InvoiceName-ITA.txt");


    protected DocumentType upsertType(
            DocumentTypeData documentTypeData,
            ExecutionContext executionContext) {

        return upsertType(documentTypeData.getRef(), documentTypeData.getName(), executionContext);
    }


    @Override
    protected void execute(final ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, new ApplicationTenancyForGlobal());
        executionContext.executeChild(this, new ApplicationTenancyForIt());
        executionContext.executeChild(this, new RenderingStrategies());

        upsertTemplatesForInvoice(executionContext);

        upsertTemplatesForInvoiceSummaryForPropertyDueDateStatus(executionContext);
    }

    private void upsertTemplatesForInvoice(final ExecutionContext executionContext) {

        final String url = "${reportServerBaseUrl}";


        final RenderingStrategy fmkRenderingStrategy =
                renderingStrategyRepository.findByReference(RenderingStrategies.REF_FMK);
        final RenderingStrategy sipcRenderingStrategy =
                renderingStrategyRepository.findByReference(RenderingStrategies.REF_SIPC);
        final RenderingStrategy siRenderingStrategy =
                renderingStrategyRepository.findByReference(RenderingStrategies.REF_SI);


        //
        // prelim letter
        //

        // template for PL cover note
        final DocumentType docTypeForPrelimCoverNote =
                upsertType(DocumentTypeData.COVER_NOTE_PRELIM_LETTER, executionContext);

        String contentText = loadResource("PrelimLetterEmailCoverNote.html");
        upsertDocumentTemplateForTextHtmlWithApplicability(
                docTypeForPrelimCoverNote,
                ApplicationTenancyForGlobal.PATH, null,
                contentText, fmkRenderingStrategy,
                NAME_TEXT_PRELIM_LETTER_GLOBAL, fmkRenderingStrategy,
                Document.class,
                FreemarkerModelOfPrelimLetterOrInvoiceDocForEmailCover.class,
                AttachToNone.class,
                executionContext);

        contentText = loadResource("PrelimLetterEmailCoverNote-ITA.html");
        upsertDocumentTemplateForTextHtmlWithApplicability(
                docTypeForPrelimCoverNote,
                ApplicationTenancyForIt.PATH, " (Italy)",
                contentText, fmkRenderingStrategy,
                NAME_TEXT_PRELIM_LETTER_ITA, fmkRenderingStrategy,
                Document.class,
                FreemarkerModelOfPrelimLetterOrInvoiceDocForEmailCover.class,
                AttachToNone.class,
                executionContext);


        // template for PL itself
        final DocumentType docTypeForPrelim =
                upsertType(DocumentTypeData.PRELIM_LETTER, executionContext);
        upsertTemplateForPdfWithApplicability(
                docTypeForPrelim,
                ApplicationTenancyForGlobal.PATH, null,
                false,
                url
                        + "Preliminary+Letter"
                        + "&id=${this.id}"
                        + "&rs:Command=Render&rs:Format=PDF",
                sipcRenderingStrategy,
                "Preliminary letter for Invoice ${this.buyer.reference} ${this.dueDate}",
                siRenderingStrategy,
                Invoice.class,
                StringInterpolatorToSsrsUrlOfInvoice.class,
                ForPrelimLetterOfInvoiceAttachToSame.class,
                executionContext
        );

        // (currently) this is identical to global
        upsertTemplateForPdfWithApplicability(
                docTypeForPrelim,
                ApplicationTenancyForIt.PATH, " (Italy)",
                false,
                url
                        + "Preliminary+Letter"
                        + "&id=${this.id}"
                        + "&rs:Command=Render&rs:Format=PDF",
                sipcRenderingStrategy,
                "Preliminary letter for Invoice ${this.buyer.reference} ${this.dueDate} (Italy)",
                siRenderingStrategy,
                Invoice.class,
                StringInterpolatorToSsrsUrlOfInvoice.class,
                ForPrelimLetterOfInvoiceAttachToSame.class,
                executionContext
        );


        //
        // invoice
        //

        // template for invoice cover note
        final DocumentType docTypeForInvoiceCoverNote =
                upsertType(DocumentTypeData.COVER_NOTE_INVOICE, executionContext);

        contentText = loadResource("InvoiceEmailCoverNote.html");
        upsertDocumentTemplateForTextHtmlWithApplicability(
                docTypeForInvoiceCoverNote,
                ApplicationTenancyForGlobal.PATH, null,
                contentText, fmkRenderingStrategy,
                NAME_TEXT_INVOICE_GLOBAL, fmkRenderingStrategy,
                Document.class,
                FreemarkerModelOfPrelimLetterOrInvoiceDocForEmailCover.class,
                AttachToNone.class,
                executionContext);

        contentText = loadResource("InvoiceEmailCoverNote-ITA.html");
        upsertDocumentTemplateForTextHtmlWithApplicability(
                docTypeForInvoiceCoverNote,
                ApplicationTenancyForIt.PATH, " (Italy)",
                contentText, fmkRenderingStrategy,
                NAME_TEXT_INVOICE_ITA, fmkRenderingStrategy,
                Document.class,
                FreemarkerModelOfPrelimLetterOrInvoiceDocForEmailCover.class,
                AttachToNone.class,
				executionContext
        );


        // template for invoice itself
        final DocumentType docTypeForInvoice = upsertType(DocumentTypeData.INVOICE, executionContext);

        upsertTemplateForPdfWithApplicability(
                docTypeForInvoice,
                ApplicationTenancyForGlobal.PATH, null,
                false,
                url
                + "Invoice"
                + "&id=${this.id}"
                + "&rs:Command=Render&rs:Format=PDF",
                sipcRenderingStrategy,
                "Invoice for ${this.buyer.reference} ${this.dueDate}", siRenderingStrategy,
                Invoice.class,
                StringInterpolatorToSsrsUrlOfInvoice.class,
                ForInvoiceDocOfInvoiceAttachToInvoiceAndAnyReceipts.class,
                executionContext
        );

        // (currently) this is identical to global
        upsertTemplateForPdfWithApplicability(
                docTypeForInvoice,
                ApplicationTenancyForIt.PATH, "( Italy)",
                false,
                url
                + "Invoice"
                + "&id=${this.id}"
                + "&rs:Command=Render&rs:Format=PDF",
                sipcRenderingStrategy,
                "Invoice for ${this.buyer.reference} ${this.dueDate} (Italy)", siRenderingStrategy,
                Invoice.class,
                StringInterpolatorToSsrsUrlOfInvoice.class,
                ForInvoiceDocOfInvoiceAttachToInvoiceAndAnyReceipts.class,
                executionContext
        );

        //
        // document types without any templates
        // (used to attach supporting documents to invoice)
        //
        upsertType(DocumentTypeData.SUPPLIER_RECEIPT, executionContext);
        upsertType(DocumentTypeData.TAX_REGISTER, executionContext);
        upsertType(DocumentTypeData.SPECIAL_COMMUNICATION, executionContext);
        upsertType(DocumentTypeData.CALCULATION, executionContext);

    }

    private void upsertTemplatesForInvoiceSummaryForPropertyDueDateStatus(final ExecutionContext executionContext) {

        final RenderingStrategy sipcRenderingStrategy =
                renderingStrategyRepository.findByReference(RenderingStrategies.REF_SIPC);
        final RenderingStrategy siRenderingStrategy =
                renderingStrategyRepository.findByReference(RenderingStrategies.REF_SI);

        upsertTemplateForPdfWithApplicability(
                upsertType(DocumentTypeData.INVOICES, executionContext),
                ApplicationTenancyForGlobal.PATH, null,
                true,
                URL
                + "Invoices"
                + "&dueDate=${this.dueDate}&${this.seller.id}&atPath=${this.atPath}"
                + "&rs:Command=Render&rs:Format=PDF",
                sipcRenderingStrategy,
                "Invoices overview",
                siRenderingStrategy,
                InvoiceSummaryForPropertyDueDateStatus.class,
                StringInterpolatorToSsrsUrlOfInvoiceSummary.class,
                AttachToNone.class,  // since preview only
                executionContext
        );

        upsertTemplateForPdfWithApplicability(
                upsertType(DocumentTypeData.INVOICES_PRELIM, executionContext),
                ApplicationTenancyForGlobal.PATH, null,
                true,
                URL
                + "Preliminary+Letter"
                + "&dueDate=${this.dueDate}&sellerId=${this.seller.id}&atPath=${this.atPath}"
                + "&rs:Command=Render&rs:Format=PDF",
                sipcRenderingStrategy,
                "Preliminary letter for Invoices",
                siRenderingStrategy,
                InvoiceSummaryForPropertyDueDateStatus.class,
                StringInterpolatorToSsrsUrlOfInvoiceSummary.class,
                AttachToNone.class,  // since preview only
                executionContext
        );

        upsertTemplateForPdfWithApplicability(
                upsertType(DocumentTypeData.INVOICES_FOR_SELLER, executionContext),
                ApplicationTenancyForGlobal.PATH, null,
                true,
                URL
                + "Preliminary+Letter"
                + "&dueDate=${this.dueDate}&sellerId=${this.seller.id}&atPath=${this.atPath}"
                + "&rs:Command=Render&rs:Format=PDF",
                sipcRenderingStrategy,
                "Preliminary Invoice for Seller",
                siRenderingStrategy,
                InvoiceSummaryForPropertyDueDateStatus.class,
                StringInterpolatorToSsrsUrlOfInvoiceSummary.class,
                AttachToNone.class,  // since preview only
                executionContext
        );
    }

    private DocumentTemplate upsertTemplateForPdfWithApplicability(
            final DocumentType documentType,
            final String atPath,
            final String templateNameSuffixIfAny,
            final boolean previewOnly,
            final String contentText, final RenderingStrategy contentRenderingStrategy,
            final String nameText, final RenderingStrategy nameRenderingStrategy,
            final Class<?> applicableToClass,
            final Class<? extends RendererModelFactory> rendererModelFactoryClass,
            final Class<? extends AttachmentAdvisor> attachmentAdvisorClass,
            final ExecutionContext executionContext) {

        final DocumentTemplate template =
                upsertTemplateForPdf(documentType, atPath, templateNameSuffixIfAny, previewOnly,
				        contentText, contentRenderingStrategy, 
						nameText, nameRenderingStrategy, 
						executionContext);

        mixin(DocumentTemplate._applicable.class, template)
                .applicable(applicableToClass, rendererModelFactoryClass, attachmentAdvisorClass);

        return template;
    }

    private DocumentTemplate upsertTemplateForPdf(
            final DocumentType docType,
            final String atPath,
            final String templateNameSuffixIfAny,
            final boolean previewOnly,
            final String contentText, final RenderingStrategy contentRenderingStrategy,
            final String nameText, final RenderingStrategy nameRenderingStrategy,
            final ExecutionContext executionContext) {

        final LocalDate now = clockService.now();

        return upsertDocumentTextTemplate(
                docType, now, atPath,
                ".pdf",
                previewOnly,
                buildTemplateName(docType, templateNameSuffixIfAny),
                "application/pdf",
                contentText, contentRenderingStrategy,
                nameText, nameRenderingStrategy,
                executionContext);
    }

    private void upsertDocumentTemplateForTextHtmlWithApplicability(
            final DocumentType docType,
            final String atPath,
            final String nameSuffixIfAny,
            final String contentText,
            final RenderingStrategy contentRenderingStrategy,
            final String nameText,
            final RenderingStrategy nameRenderingStrategy,
            final Class<?> domainClass,
            final Class<? extends RendererModelFactory> rendererModelFactoryClass,
            final Class<? extends AttachmentAdvisor> attachmentAdvisorClass,
            final ExecutionContext executionContext) {

        final LocalDate date = clockService.now();

        final Clob clob = new Clob(buildTemplateName(docType, nameSuffixIfAny, ".html"), "text/html", contentText);
        final DocumentTemplate documentTemplate = upsertDocumentClobTemplate(
                docType, date, atPath,
                ".html",
                false,
                clob, contentRenderingStrategy,
                nameText, nameRenderingStrategy,
                executionContext);

        mixin(DocumentTemplate._applicable.class, documentTemplate).applicable(domainClass, rendererModelFactoryClass, attachmentAdvisorClass);

        executionContext.addResult(this, documentTemplate);
    }

    private static String buildTemplateName(
            final DocumentType docType,
            final String templateNameSuffixIfAny) {
        return buildTemplateName(docType, templateNameSuffixIfAny, null);
    }

    private static String buildTemplateName(
            final DocumentType docType,
            final String templateNameSuffixIfAny,
            final String extension) {
        final String name = docType.getName() + (templateNameSuffixIfAny != null ? templateNameSuffixIfAny : "");
        return extension != null
                ? name.endsWith(extension)
                    ? name
                    : name + extension
                : name;
    }


    private static String loadResource(final String resourceName) {
        final URL templateUrl = Resources
                .getResource(DocumentTypeAndTemplatesFSForInvoicesUsingSsrs.class, resourceName);
        try {
            return Resources.toString(templateUrl, Charsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Unable to read resource URL '%s'", templateUrl));
        }
    }


    @Inject
    RenderingStrategyRepository renderingStrategyRepository;
    @Inject
    ClockService clockService;


}
