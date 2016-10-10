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

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.incode.module.documents.dom.impl.applicability.Binder;
import org.incode.module.documents.dom.impl.docs.Document;
import org.incode.module.documents.dom.impl.docs.DocumentTemplate;
import org.incode.module.documents.dom.impl.rendering.RenderingStrategy;
import org.incode.module.documents.dom.impl.rendering.RenderingStrategyRepository;
import org.incode.module.documents.dom.impl.types.DocumentType;
import org.incode.module.documents.fixture.DocumentTemplateFSAbstract;

import org.estatio.dom.documents.binders.BinderForDocumentAttachedToPrelimLetterOrInvoice;
import org.estatio.dom.documents.binders.BinderForReportServerAttachNone;
import org.estatio.dom.documents.binders.BinderForReportServerAttachToInput;
import org.estatio.dom.invoice.Constants;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.viewmodel.InvoiceSummaryForPropertyDueDateStatus;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForGlobal;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForIt;

public class DocumentTypeAndTemplatesFSForInvoicesUsingSsrs extends DocumentTemplateFSAbstract {

    // applicable to Invoice.class
    public static final String DOC_TYPE_REF_INVOICE_PRELIM = Constants.DOC_TYPE_REF_PRELIM;
    public static final String DOC_TYPE_REF_INVOICE = Constants.DOC_TYPE_REF_INVOICE;

    // applicable to InvoiceSummaryForPropertyDueDateStatus.class
    public static final String DOC_TYPE_REF_INVOICES_OVERVIEW = "INVOICES";
    public static final String DOC_TYPE_REF_INVOICES_PRELIM = "INVOICES-PRELIM";
    public static final String DOC_TYPE_REF_INVOICES_PRELIM_FOR_SELLER = "INVOICES-FOR-SELLER";
    public static final String URL = "${reportServerBaseUrl}";

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


        //
        // prelim letter
        //

        // template for PL cover note
        final DocumentType docTypeForPrelimCoverNote =
                upsertType(Constants.DOC_TYPE_REF_PRELIM_EMAIL_COVER_NOTE, "Email Cover Note for Preliminary Letter", executionContext);

        String subjectText = "Preliminary letter for invoice ${invoice.lease.reference} due ${invoice.dueDate}";
        String contentText = loadResource("PrelimLetterEmailCoverNote.html");
        upsertDocumentTemplateForTextHtmlWithApplicability(
                docTypeForPrelimCoverNote,
                ApplicationTenancyForGlobal.PATH, null,
                subjectText,
                contentText,
                executionContext);

        subjectText = "Lettera preliminare per fattura ${invoice.lease.reference} causa ${invoice.dueDate}";
        contentText = loadResource("PrelimLetterEmailCoverNote-ITA.html");
        upsertDocumentTemplateForTextHtmlWithApplicability(
                docTypeForPrelimCoverNote,
                ApplicationTenancyForIt.PATH, " (Italy)",
                subjectText,
                contentText,
                executionContext);


        // template for PL itself
        final DocumentType docTypeForPrelim =
                upsertType(DOC_TYPE_REF_INVOICE_PRELIM, "Preliminary letter for Invoice", executionContext);
        upsertTemplateForPdfWithApplicability(
                docTypeForPrelim,
                ApplicationTenancyForGlobal.PATH, null,
                false, url
                        + "Preliminary+Letter"
                        + "&id=${this.id}"
                        + "&rs:Command=Render&rs:Format=PDF",
                "Preliminary letter for Invoice ${this.number}",
                Invoice.class, BinderForReportServerAttachToInput.class,
                executionContext);
        upsertTemplateForPdfWithApplicability(
                docTypeForPrelim,
                ApplicationTenancyForIt.PATH, " (Italy)",
                false, url
                        + "Preliminary+Letter"
                        + "&id=${this.id}"
                        + "&rs:Command=Render&rs:Format=PDF"
                        + "&appTenancy=/ITA",
                "Preliminary letter for Invoice ${this.number} (Italy)",
                Invoice.class, BinderForReportServerAttachToInput.class,
                executionContext);


        //
        // invoice
        //

        // template for invoice cover note
        final DocumentType docTypeForInvoiceCoverNote =
                upsertType(Constants.DOC_TYPE_REF_INVOICE_EMAIL_COVER_NOTE, "Email Cover Note for Invoice", executionContext);

        subjectText = "Invoice ${invoice.lease.reference} due ${invoice.dueDate}";
        contentText = loadResource("InvoiceEmailCoverNote.html");
        upsertDocumentTemplateForTextHtmlWithApplicability(
                docTypeForInvoiceCoverNote,
                ApplicationTenancyForGlobal.PATH, null,
                subjectText,
                contentText,
                executionContext);

        subjectText = "Fattura ${invoice.lease.reference} causa ${invoice.dueDate}";
        contentText = loadResource("InvoiceEmailCoverNote-ITA.html");
        upsertDocumentTemplateForTextHtmlWithApplicability(
                docTypeForInvoiceCoverNote,
                ApplicationTenancyForIt.PATH, " (Italy)",
                subjectText,
                contentText,
                executionContext);


        // template for invoice itself
        final DocumentType docTypeForInvoice = upsertType(DOC_TYPE_REF_INVOICE, "Invoice", executionContext);

        upsertTemplateForPdfWithApplicability(
                docTypeForInvoice,
                ApplicationTenancyForGlobal.PATH, null,
                false,
                url
                + "Invoice"
                + "&id=${this.id}"
                + "&rs:Command=Render&rs:Format=PDF",
                "Invoice for ${this.number}",
                Invoice.class, BinderForReportServerAttachToInput.class,
                executionContext);
        upsertTemplateForPdfWithApplicability(
                docTypeForInvoice,
                ApplicationTenancyForIt.PATH, "( Italy)",
                false,
                url
                + "Invoice"
                + "&id=${this.id}"
                + "&rs:Command=Render&rs:Format=PDF"
                + "&appTenancy=/ITA",
                "Invoice for ${this.number}",
                Invoice.class, BinderForReportServerAttachToInput.class,
                executionContext);
    }

    private void upsertTemplatesForInvoiceSummaryForPropertyDueDateStatus(final ExecutionContext executionContext) {

        upsertTemplateForPdfWithApplicability(
                upsertType(DOC_TYPE_REF_INVOICES_OVERVIEW, "Invoices overview", executionContext),
                ApplicationTenancyForGlobal.PATH, null,
                true,
                URL
                + "Invoices"
                + "&dueDate=${this.dueDate}&${this.seller.id}&atPath=${this.atPath}"
                + "&rs:Command=Render&rs:Format=PDF",
                "Invoices overview",
                InvoiceSummaryForPropertyDueDateStatus.class,
                BinderForReportServerAttachNone.class,
                executionContext);

        upsertTemplateForPdfWithApplicability(
                upsertType(DOC_TYPE_REF_INVOICES_PRELIM, "Preliminary letter for Invoices", executionContext),
                ApplicationTenancyForGlobal.PATH, null,
                true,
                URL
                + "Preliminary+Letter"
                + "&dueDate=${this.dueDate}&sellerId=${this.seller.id}&atPath=${this.atPath}"
                + "&rs:Command=Render&rs:Format=PDF",
                "Preliminary letter for Invoices",
                InvoiceSummaryForPropertyDueDateStatus.class,
                BinderForReportServerAttachNone.class,
                executionContext);

        upsertTemplateForPdfWithApplicability(
                upsertType(DOC_TYPE_REF_INVOICES_PRELIM_FOR_SELLER, "Preliminary Invoice for Seller", executionContext),
                ApplicationTenancyForGlobal.PATH, null,
                true,
                URL
                + "Preliminary+Letter"
                + "&dueDate=${this.dueDate}&sellerId=${this.seller.id}&atPath=${this.atPath}"
                + "&rs:Command=Render&rs:Format=PDF",
                "Preliminary Invoice for Seller",
                InvoiceSummaryForPropertyDueDateStatus.class,
                BinderForReportServerAttachNone.class,
                executionContext);
    }

    private DocumentTemplate upsertTemplateForPdfWithApplicability(
            final DocumentType documentType,
            final String atPath, final String nameSuffixIfAny,
            final boolean previewOnly,
            final String text,
            final String subjectText,
            final Class<?> applicableToClass,
            final Class<? extends Binder> binderClass,
            final ExecutionContext executionContext) {

        final RenderingStrategy contentRenderingStrategy =
                renderingStrategyRepository.findByReference(RenderingStrategies.REF_SIPC);
        final RenderingStrategy subjectRenderingStrategy =
                renderingStrategyRepository.findByReference(RenderingStrategies.REF_SI);

        final DocumentTemplate template =
                upsertTemplateForPdf(documentType, atPath, nameSuffixIfAny, previewOnly, text, contentRenderingStrategy, subjectText,
                        subjectRenderingStrategy, executionContext);
        template.applicable(applicableToClass, binderClass);

        return template;
    }

    private DocumentTemplate upsertTemplateForPdf(
            final DocumentType docType,
            final String atPath,
            final String nameSuffixIfAny,
            final boolean previewOnly,
            final String contentText, final RenderingStrategy contentRenderingStrategy,
            final String subjectText, final RenderingStrategy subjectRenderingStrategy,
            final ExecutionContext executionContext) {

        final LocalDate now = clockService.now();

        return upsertDocumentTextTemplate(
                docType, now, atPath,
                ".pdf",
                previewOnly,
                buildTemplateName(docType, nameSuffixIfAny),
                "application/pdf",
                contentText, contentRenderingStrategy,
                subjectText, subjectRenderingStrategy,
                executionContext);
    }

    private void upsertDocumentTemplateForTextHtmlWithApplicability(
            final DocumentType docType,
            final String atPath,
            final String nameSuffixIfAny,
            final String subjectText,
            final String contentText,
            final ExecutionContext executionContext) {

        final LocalDate date = clockService.now();

        final RenderingStrategy freemarkerRenderingStrategy =
                renderingStrategyRepository.findByReference(RenderingStrategies.REF_FMK);

        final Clob clob = new Clob(buildTemplateName(docType, nameSuffixIfAny, ".html"), "text/html", contentText);
        final DocumentTemplate documentTemplate = upsertDocumentClobTemplate(
                docType, date, atPath,
                ".html",
                false,
                clob, freemarkerRenderingStrategy,
                subjectText, freemarkerRenderingStrategy,
                executionContext);

        documentTemplate.applicable(Document.class, BinderForDocumentAttachedToPrelimLetterOrInvoice.class);

        executionContext.addResult(this, documentTemplate);
    }

    private static String buildTemplateName(
            final DocumentType docType,
            final String nameSuffixIfAny) {
        return buildTemplateName(docType, nameSuffixIfAny, null);
    }

    private static String buildTemplateName(
            final DocumentType docType,
            final String nameSuffixIfAny,
            final String extension) {
        final String name = docType.getName() + (nameSuffixIfAny != null ? nameSuffixIfAny : "");
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
    private ApplicationTenancyRepository applicationTenancyRepository;
    @Inject
    private RenderingStrategyRepository renderingStrategyRepository;
    @Inject
    private ClockService clockService;


}
