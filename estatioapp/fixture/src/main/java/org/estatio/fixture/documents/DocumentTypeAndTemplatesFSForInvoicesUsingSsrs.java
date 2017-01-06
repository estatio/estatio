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

import org.estatio.dom.documents.binders.AttachToNone;
import org.estatio.dom.documents.binders.ForEmailCoverOfPrelimLetterOrInvoiceDocAttachToSame;
import org.estatio.dom.documents.binders.ForInvoiceDocOfInvoiceAttachToInvoiceAndAnyReceipts;
import org.estatio.dom.documents.binders.ForPrelimLetterOfInvoiceAttachToSame;
import org.estatio.dom.documents.binders.StringInterpolatorToSsrsUrlOfInvoiceSummary;
import org.estatio.dom.documents.binders.FreemarkerModelOfPrelimLetterOrInvoiceDocForEmailCover;
import org.estatio.dom.documents.binders.StringInterpolatorToSsrsUrlOfInvoice;
import org.estatio.dom.invoice.Constants;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.lease.leaseinvoicing.viewmodel.InvoiceSummaryForPropertyDueDateStatus;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForGlobal;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForIt;

public class DocumentTypeAndTemplatesFSForInvoicesUsingSsrs extends DocumentTemplateFSAbstract {

    // applicable to Invoice.class
    public static final String DOC_TYPE_REF_PRELIM = Constants.DOC_TYPE_REF_PRELIM;
    public static final String DOC_TYPE_REF_INVOICE = Constants.DOC_TYPE_REF_INVOICE;

    public static final String DOC_TYPE_REF_SUPPLIER_RECEIPT = Constants.DOC_TYPE_REF_SUPPLIER_RECEIPT;
    public static final String DOC_TYPE_REF_TAX_RECEIPT = Constants.DOC_TYPE_REF_TAX_RECEIPT;

    // applicable to InvoiceSummaryForPropertyDueDateStatus.class
    public static final String DOC_TYPE_REF_INVOICES_OVERVIEW = "INVOICES";
    public static final String DOC_TYPE_REF_INVOICES_PRELIM = "INVOICES-PRELIM";
    public static final String DOC_TYPE_REF_INVOICES_PRELIM_FOR_SELLER = "INVOICES-FOR-SELLER";

    public static final String URL = "${reportServerBaseUrl}";

    public static final String NAME_TEXT_PRELIM_LETTER_GLOBAL = "${property.reference} ${tenant.name} ${(unit.name)!\"\"} ${(brand.name)!\"\"} Preliminary Letter ${invoice.dueDate}";
    public static final String NAME_TEXT_PRELIM_LETTER_ITA = "${property.reference} ${tenant.name} ${(unit.name)!\"\"} ${(brand.name)!\"\"} Preliminary Letter/Avviso di fatturazione ${invoice.dueDate}";

    public static final String NAME_TEXT_INVOICE_ITA = "${property.reference} ${tenant.name} ${(unit.name)!\"\"} ${(brand.name)!\"\"} Invoice/Fatturazione ${invoice.dueDate}";
    public static final String NAME_TEXT_INVOICE_GLOBAL = "${property.reference} ${tenant.name} ${(unit.name)!\"\"} ${(brand.name)!\"\"} Invoice ${invoice.dueDate}";

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
                upsertType(Constants.DOC_TYPE_REF_PRELIM_EMAIL_COVER_NOTE, "Email Cover Note for Preliminary Letter", executionContext);

        String contentText = loadResource("PrelimLetterEmailCoverNote.html");
        upsertDocumentTemplateForTextHtmlWithApplicability(
                docTypeForPrelimCoverNote,
                ApplicationTenancyForGlobal.PATH, null,
                contentText, fmkRenderingStrategy,
                NAME_TEXT_PRELIM_LETTER_GLOBAL, fmkRenderingStrategy,
                Document.class,
                FreemarkerModelOfPrelimLetterOrInvoiceDocForEmailCover.class,
                ForEmailCoverOfPrelimLetterOrInvoiceDocAttachToSame.class,
                executionContext);

        contentText = loadResource("PrelimLetterEmailCoverNote-ITA.html");
        upsertDocumentTemplateForTextHtmlWithApplicability(
                docTypeForPrelimCoverNote,
                ApplicationTenancyForIt.PATH, " (Italy)",
                contentText, fmkRenderingStrategy,
                NAME_TEXT_PRELIM_LETTER_ITA, fmkRenderingStrategy,
                Document.class,
                FreemarkerModelOfPrelimLetterOrInvoiceDocForEmailCover.class,
                ForEmailCoverOfPrelimLetterOrInvoiceDocAttachToSame.class,
                executionContext);


        // template for PL itself
        final DocumentType docTypeForPrelim =
                upsertType(DOC_TYPE_REF_PRELIM, "Preliminary letter for Invoice", executionContext);
        upsertTemplateForPdfWithApplicability(
                docTypeForPrelim,
                ApplicationTenancyForGlobal.PATH, null,
                false,
                url
                        + "Preliminary+Letter"
                        + "&id=${this.id}"
                        + "&rs:Command=Render&rs:Format=PDF",
                sipcRenderingStrategy,
                "Preliminary letter for Invoice ${this.number}",
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
                "Preliminary letter for Invoice ${this.number} (Italy)",
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
                upsertType(Constants.DOC_TYPE_REF_INVOICE_EMAIL_COVER_NOTE, "Email Cover Note for Invoice", executionContext);

        contentText = loadResource("InvoiceEmailCoverNote.html");
        upsertDocumentTemplateForTextHtmlWithApplicability(
                docTypeForInvoiceCoverNote,
                ApplicationTenancyForGlobal.PATH, null,
                contentText, fmkRenderingStrategy,
                NAME_TEXT_INVOICE_GLOBAL, fmkRenderingStrategy,
                Document.class,
                FreemarkerModelOfPrelimLetterOrInvoiceDocForEmailCover.class,
                ForEmailCoverOfPrelimLetterOrInvoiceDocAttachToSame.class,
                executionContext);

        contentText = loadResource("InvoiceEmailCoverNote-ITA.html");
        upsertDocumentTemplateForTextHtmlWithApplicability(
                docTypeForInvoiceCoverNote,
                ApplicationTenancyForIt.PATH, " (Italy)",
                contentText, fmkRenderingStrategy,
                NAME_TEXT_INVOICE_ITA, fmkRenderingStrategy,
                Document.class,
                FreemarkerModelOfPrelimLetterOrInvoiceDocForEmailCover.class,
                ForEmailCoverOfPrelimLetterOrInvoiceDocAttachToSame.class,
				executionContext
        );


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
                sipcRenderingStrategy,
                "Invoice for ${this.number}", siRenderingStrategy,
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
                "Invoice for ${this.number}", siRenderingStrategy,
                Invoice.class,
                StringInterpolatorToSsrsUrlOfInvoice.class,
                ForInvoiceDocOfInvoiceAttachToInvoiceAndAnyReceipts.class,
                executionContext
        );

        //
        // document types without any templates
        // (used to attach supporting documents to invoice)
        //
        upsertType(DOC_TYPE_REF_SUPPLIER_RECEIPT, "Invoice Supplier Receipt", executionContext);
        upsertType(DOC_TYPE_REF_TAX_RECEIPT, "Invoice Tax Receipt", executionContext);

    }

    private void upsertTemplatesForInvoiceSummaryForPropertyDueDateStatus(final ExecutionContext executionContext) {

        final RenderingStrategy sipcRenderingStrategy =
                renderingStrategyRepository.findByReference(RenderingStrategies.REF_SIPC);
        final RenderingStrategy siRenderingStrategy =
                renderingStrategyRepository.findByReference(RenderingStrategies.REF_SI);

        upsertTemplateForPdfWithApplicability(
                upsertType(DOC_TYPE_REF_INVOICES_OVERVIEW, "Invoices overview", executionContext),
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
                upsertType(DOC_TYPE_REF_INVOICES_PRELIM, "Preliminary letter for Invoices", executionContext),
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
                upsertType(DOC_TYPE_REF_INVOICES_PRELIM_FOR_SELLER, "Preliminary Invoice for Seller", executionContext),
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
            final String nameSuffixIfAny,
            final boolean previewOnly,
            final String contentText, final RenderingStrategy contentRenderingStrategy,
            final String nameText, final RenderingStrategy nameRenderingStrategy,
            final Class<?> applicableToClass,
            final Class<? extends RendererModelFactory> rendererModelFactoryClass,
            final Class<? extends AttachmentAdvisor> attachmentAdvisorClass,
            final ExecutionContext executionContext) {

        final DocumentTemplate template =
                upsertTemplateForPdf(documentType, atPath, nameSuffixIfAny, previewOnly, 
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
            final String nameSuffixIfAny,
            final boolean previewOnly,
            final String contentText, final RenderingStrategy contentRenderingStrategy,
            final String nameText, final RenderingStrategy nameRenderingStrategy,
            final ExecutionContext executionContext) {

        final LocalDate now = clockService.now();

        return upsertDocumentTextTemplate(
                docType, now, atPath,
                ".pdf",
                previewOnly,
                buildTemplateName(docType, nameSuffixIfAny),
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
    RenderingStrategyRepository renderingStrategyRepository;
    @Inject
    ClockService clockService;


}
