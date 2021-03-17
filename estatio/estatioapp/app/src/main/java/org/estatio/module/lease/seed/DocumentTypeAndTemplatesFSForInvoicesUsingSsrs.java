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
package org.estatio.module.lease.seed;

import java.io.IOException;
import java.net.URL;

import javax.inject.Inject;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import org.joda.time.LocalDate;

import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.value.Clob;

import org.incode.module.apptenancy.fixtures.enums.ApplicationTenancy_enum;
import org.incode.module.base.dom.MimeTypeData;
import org.incode.module.document.dom.impl.applicability.AttachmentAdvisor;
import org.incode.module.document.dom.impl.applicability.RendererModelFactory;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;
import org.incode.module.document.dom.impl.docs.DocumentTemplate_applicable;
import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.fixture.DocumentTemplateFSAbstract;

import org.estatio.module.invoice.dom.DocumentTypeData;
import org.estatio.module.invoice.dom.Invoice;
import org.estatio.module.lease.dom.invoicing.summary.InvoiceSummaryForPropertyDueDateStatus;
import org.estatio.module.lease.spiimpl.document.binders.AttachToNone;
import org.estatio.module.lease.spiimpl.document.binders.ForPrimaryDocOfInvoiceAttachToInvoiceAndAnyRelevantSupportingDocuments;
import org.estatio.module.lease.spiimpl.document.binders.FreemarkerModelOfPrelimLetterOrInvoiceDocForEmailCover;
import org.estatio.module.lease.spiimpl.document.binders.StringInterpolatorToSsrsUrlOfInvoice;
import org.estatio.module.lease.spiimpl.document.binders.StringInterpolatorToSsrsUrlOfInvoiceSummary;

public class DocumentTypeAndTemplatesFSForInvoicesUsingSsrs extends DocumentTemplateFSAbstract {


    public static final String URL = "${reportServerBaseUrl}";

    private LocalDate templateDateIfAny;

    public DocumentTypeAndTemplatesFSForInvoicesUsingSsrs() {
        this(null);
    }

    public DocumentTypeAndTemplatesFSForInvoicesUsingSsrs(
            final LocalDate templateDateIfAny) {
        this.templateDateIfAny = templateDateIfAny;
    }

    LocalDate getTemplateDateElseNow() {
        return templateDateIfAny != null ? templateDateIfAny : clockService.now();
    }

    protected DocumentType upsertType(
            DocumentTypeData documentTypeData,
            ExecutionContext executionContext) {

        return upsertType(documentTypeData.getRef(), documentTypeData.getName(), executionContext);
    }


    @Override
    protected void execute(final ExecutionContext executionContext) {

        final LocalDate templateDate = getTemplateDateElseNow();

        // prereqs
        executionContext.executeChild(this, ApplicationTenancy_enum.Global.builder());
        executionContext.executeChild(this, ApplicationTenancy_enum.It.builder());

        upsertTemplatesForInvoice(templateDate, executionContext);

        upsertTemplatesForInvoiceSummaryForPropertyDueDateStatus(templateDate, executionContext);
    }

    private void upsertTemplatesForInvoice(
            final LocalDate templateDate,
            final ExecutionContext executionContext) {

        final String url = "${reportServerBaseUrl}";




        //
        // prelim letter
        //

        // template for PL cover note
        final DocumentType docTypeForPrelimCoverNote =
                upsertType(DocumentTypeData.COVER_NOTE_PRELIM_LETTER, executionContext);

        String contentText = loadResource("PrelimLetterEmailCoverNote.html.ftl");
        String subjLneText = loadResource("PrelimLetterEmailCoverNoteSubjectLine.ftl");
        upsertDocumentTemplateForTextHtmlWithApplicability(
                docTypeForPrelimCoverNote,
                templateDate, ApplicationTenancy_enum.Global.getPath(), null,
                contentText,
                subjLneText,
                Document.class,
                FreemarkerModelOfPrelimLetterOrInvoiceDocForEmailCover.class,
                AttachToNone.class,
                executionContext);

        contentText = loadResource("PrelimLetterEmailCoverNote-ITA.html.ftl");
        subjLneText = loadResource("PrelimLetterEmailCoverNoteSubjectLine-ITA.ftl");
        upsertDocumentTemplateForTextHtmlWithApplicability(
                docTypeForPrelimCoverNote,
                templateDate, ApplicationTenancy_enum.It.getPath(), " (Italy)",
                contentText,
                subjLneText,
                Document.class,
                FreemarkerModelOfPrelimLetterOrInvoiceDocForEmailCover.class,
                AttachToNone.class,
                executionContext);


        // template for PL itself
        final DocumentType docTypeForPrelim =
                upsertType(DocumentTypeData.PRELIM_LETTER, executionContext);
        String titleText = loadResource("PrelimLetterTitle.ftl");
        upsertTemplateForPdfWithApplicability(
                docTypeForPrelim,
                templateDate, ApplicationTenancy_enum.Global.getPath(), null,
                false,
                url
                        + "PreliminaryLetterV2"
                        + "&id=${this.id}"
                        + "&rs:Command=Render&rs:Format=PDF",
                titleText,
                Invoice.class,
                StringInterpolatorToSsrsUrlOfInvoice.class,
                ForPrimaryDocOfInvoiceAttachToInvoiceAndAnyRelevantSupportingDocuments.class,
                executionContext
        );

        // (currently) this is identical to global
        titleText = loadResource("PrelimLetterTitle-ITA.ftl");
        upsertTemplateForPdfWithApplicability(
                docTypeForPrelim,
                templateDate, ApplicationTenancy_enum.It.getPath(), " (Italy)",
                false,
                url
                        + "PreliminaryLetterV2"
                        + "&id=${this.id}"
                        + "&rs:Command=Render&rs:Format=PDF",
                titleText,
                Invoice.class,
                StringInterpolatorToSsrsUrlOfInvoice.class,
                ForPrimaryDocOfInvoiceAttachToInvoiceAndAnyRelevantSupportingDocuments.class,
                executionContext
        );


        //
        // invoice
        //

        // template for invoice cover note
        final DocumentType docTypeForInvoiceCoverNote =
                upsertType(DocumentTypeData.COVER_NOTE_INVOICE, executionContext);

        contentText = loadResource("InvoiceEmailCoverNote.html.ftl");
        subjLneText = loadResource("InvoiceEmailCoverNoteSubjectLine.ftl");
        upsertDocumentTemplateForTextHtmlWithApplicability(
                docTypeForInvoiceCoverNote,
                templateDate, ApplicationTenancy_enum.Global.getPath(), null,
                contentText,
                subjLneText,
                Document.class,
                FreemarkerModelOfPrelimLetterOrInvoiceDocForEmailCover.class,
                AttachToNone.class,
                executionContext);

        contentText = loadResource("InvoiceEmailCoverNote-ITA.html.ftl");
        subjLneText = loadResource("InvoiceEmailCoverNoteSubjectLine-ITA.ftl");
        upsertDocumentTemplateForTextHtmlWithApplicability(
                docTypeForInvoiceCoverNote,
                templateDate, ApplicationTenancy_enum.It.getPath(), " (Italy)",
                contentText,
                subjLneText,
                Document.class,
                FreemarkerModelOfPrelimLetterOrInvoiceDocForEmailCover.class,
                AttachToNone.class,
				executionContext
        );


        // template for invoice itself
        final DocumentType docTypeForInvoice = upsertType(DocumentTypeData.INVOICE, executionContext);

        titleText = loadResource("InvoiceTitle.ftl");
        upsertTemplateForPdfWithApplicability(
                docTypeForInvoice,
                templateDate, ApplicationTenancy_enum.Global.getPath(), null,
                false,
                url
                + "InvoiceItaly"
                + "&id=${this.id}"
                + "&rs:Command=Render&rs:Format=PDF",
                titleText,
                Invoice.class,
                StringInterpolatorToSsrsUrlOfInvoice.class,
                ForPrimaryDocOfInvoiceAttachToInvoiceAndAnyRelevantSupportingDocuments.class,
                executionContext
        );

        // (currently) this is identical to global
        // TODO: this was, and certainly now is, not true any more. Is seeding the right way to go? I find it confusing because throught the UI you can upload a template and that one is overwritten by this seed ... - see ECP-906
        titleText = loadResource("InvoiceTitle-ITA.ftl");
        upsertTemplateForPdfWithApplicability(
                docTypeForInvoice,
                templateDate, ApplicationTenancy_enum.It.getPath(), "( Italy)",
                false,
                url
                + "InvoiceItaly"
                + "&id=${this.id}"
                + "&rs:Command=Render&rs:Format=PDF",
                titleText,
                Invoice.class,
                StringInterpolatorToSsrsUrlOfInvoice.class,
                ForPrimaryDocOfInvoiceAttachToInvoiceAndAnyRelevantSupportingDocuments.class,
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

    private void upsertTemplatesForInvoiceSummaryForPropertyDueDateStatus(
            final LocalDate templateDate, final ExecutionContext executionContext) {

        upsertTemplateForPdfWithApplicability(
                upsertType(DocumentTypeData.INVOICES, executionContext),
                templateDate, ApplicationTenancy_enum.Global.getPath(), null,
                true,
                URL
                + "Invoices"
                + "&dueDate=${this.dueDate}&${this.seller.id}&atPath=${this.atPath}"
                + "&rs:Command=Render&rs:Format=PDF",
                "Invoices overview",
                InvoiceSummaryForPropertyDueDateStatus.class,
                StringInterpolatorToSsrsUrlOfInvoiceSummary.class,
                AttachToNone.class,  // since preview only
                executionContext
        );

        upsertTemplateForPdfWithApplicability(
                upsertType(DocumentTypeData.INVOICES_PRELIM, executionContext),
                templateDate, ApplicationTenancy_enum.Global.getPath(), null,
                true,
                URL
                + "PreliminaryLetterV2"
                + "&dueDate=${this.dueDate}&sellerId=${this.seller.id}&atPath=${this.atPath}"
                + "&rs:Command=Render&rs:Format=PDF",
                "Preliminary letter for Invoices",
                InvoiceSummaryForPropertyDueDateStatus.class,
                StringInterpolatorToSsrsUrlOfInvoiceSummary.class,
                AttachToNone.class,  // since preview only
                executionContext
        );

        upsertTemplateForPdfWithApplicability(
                upsertType(DocumentTypeData.INVOICES_FOR_SELLER, executionContext),
                templateDate, ApplicationTenancy_enum.Global.getPath(), null,
                true,
                URL
                + "PreliminaryLetterV2"
                + "&dueDate=${this.dueDate}&sellerId=${this.seller.id}&atPath=${this.atPath}"
                + "&rs:Command=Render&rs:Format=PDF",
                "Preliminary Invoice for Seller",
                InvoiceSummaryForPropertyDueDateStatus.class,
                StringInterpolatorToSsrsUrlOfInvoiceSummary.class,
                AttachToNone.class,  // since preview only
                executionContext
        );
    }

    private DocumentTemplate upsertTemplateForPdfWithApplicability(
            final DocumentType documentType,
            final LocalDate templateDate,
            final String atPath,
            final String templateNameSuffixIfAny,
            final boolean previewOnly,
            final String contentText,
            final String nameText,
            final Class<?> applicableToClass,
            final Class<? extends RendererModelFactory> rendererModelFactoryClass,
            final Class<? extends AttachmentAdvisor> attachmentAdvisorClass,
            final ExecutionContext executionContext) {

        final DocumentTemplate template =
                upsertTemplateForPdf(documentType, templateDate, atPath, templateNameSuffixIfAny, previewOnly,
				        contentText,
                        nameText,
                        executionContext);

        mixin(DocumentTemplate_applicable.class, template)
                .applicable(applicableToClass, rendererModelFactoryClass, attachmentAdvisorClass);

        return template;
    }

    private DocumentTemplate upsertTemplateForPdf(
            final DocumentType docType,
            final LocalDate templateDate,
            final String atPath,
            final String templateNameSuffixIfAny,
            final boolean previewOnly,
            final String contentText,
            final String nameText,
            final ExecutionContext executionContext) {

        return upsertDocumentTextTemplate(
                docType, templateDate, atPath,
                ".pdf",
                previewOnly,
                buildTemplateName(docType, templateNameSuffixIfAny),
                MimeTypeData.APPLICATION_PDF.asStr(),
                contentText,
                nameText,
                executionContext);
    }

    private void upsertDocumentTemplateForTextHtmlWithApplicability(
            final DocumentType docType,
            final LocalDate templateDate,
            final String atPath,
            final String nameSuffixIfAny,
            final String contentText,
            final String nameText,
            final Class<?> domainClass,
            final Class<? extends RendererModelFactory> rendererModelFactoryClass,
            final Class<? extends AttachmentAdvisor> attachmentAdvisorClass,
            final ExecutionContext executionContext) {

        final Clob clob = new Clob(buildTemplateName(docType, nameSuffixIfAny, ".html"), MimeTypeData.TEXT_HTML.asStr(), contentText);
        final DocumentTemplate documentTemplate = upsertDocumentClobTemplate(
                docType, templateDate, atPath,
                ".html",
                false,
                clob,
                nameText,
                executionContext);

        mixin(DocumentTemplate_applicable.class, documentTemplate).applicable(domainClass, rendererModelFactoryClass, attachmentAdvisorClass);

        executionContext.addResult(this, documentTemplate);
    }


    public static String loadResource(final String resourceName) {
        final URL templateUrl = Resources
                .getResource(DocumentTypeAndTemplatesFSForInvoicesUsingSsrs.class, resourceName);
        try {
            return Resources.toString(templateUrl, Charsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Unable to read resource URL '%s'", templateUrl));
        }
    }


    @Inject
    ClockService clockService;


}
