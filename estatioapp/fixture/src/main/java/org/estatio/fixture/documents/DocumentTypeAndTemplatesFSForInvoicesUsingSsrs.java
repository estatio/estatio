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

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.services.clock.ClockService;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.incode.module.docrendering.stringinterpolator.fixture.RenderingStrategyFSForStringInterpolator;
import org.incode.module.docrendering.stringinterpolator.fixture.RenderingStrategyFSForStringInterpolatorPreviewAndCaptureUrl;
import org.incode.module.documents.dom.impl.applicability.Binder;
import org.incode.module.documents.dom.impl.docs.DocumentTemplate;
import org.incode.module.documents.dom.impl.rendering.RenderingStrategy;
import org.incode.module.documents.dom.impl.rendering.RenderingStrategyRepository;
import org.incode.module.documents.dom.impl.types.DocumentType;
import org.incode.module.documents.fixture.DocumentTemplateFSAbstract;

import org.estatio.dom.documents.binders.BinderForReportServer;
import org.estatio.dom.documents.binders.BinderForReportServerForInvoiceSummaryForPropertyDueDateStatus;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.viewmodel.InvoiceSummaryForPropertyDueDateStatus;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForGlobal;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForNl;

public class DocumentTypeAndTemplatesFSForInvoicesUsingSsrs extends DocumentTemplateFSAbstract {

    // applicable to Invoice.class
    public static final String DOC_TYPE_REF_INVOICE_PRELIM = "INVOICE-PRELIM";
    public static final String DOC_TYPE_REF_INVOICE = "INVOICE";

    // applicable to InvoiceSummaryForPropertyDueDateStatus.class
    public static final String DOC_TYPE_REF_INVOICES_OVERVIEW = "INVOICES";
    public static final String DOC_TYPE_REF_INVOICES_PRELIM = "INVOICES-PRELIM";
    public static final String DOC_TYPE_REF_INVOICES_PRELIM_FOR_SELLER = "INVOICES-FOR-SELLER";



    @Override
    protected void execute(final ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, new RenderingStrategyFSForStringInterpolatorPreviewAndCaptureUrl());
        executionContext.executeChild(this, new RenderingStrategyFSForStringInterpolator());

        final String url = "${reportServerBaseUrl}";

        final RenderingStrategy stringInterpolatePreviewCapture =
                renderingStrategyRepository.findByReference(
                        RenderingStrategyFSForStringInterpolatorPreviewAndCaptureUrl.REF);
        final RenderingStrategy stringInterpolate =
                renderingStrategyRepository.findByReference(
                        RenderingStrategyFSForStringInterpolator.REF);

        // Invoice (two templates, one global and one for the NL)
        final DocumentType invoicePrelim = createType(DOC_TYPE_REF_INVOICE_PRELIM, "Preliminary letter for Invoice");
        createTemplateWithApplicability(
                invoicePrelim,
                ApplicationTenancyForGlobal.PATH, null,
                false, url
                + "Preliminary+Letter"
                + "&id=${this.id}"
                + "&rs:Command=Render&rs:Format=PDF", stringInterpolatePreviewCapture,
                "Preliminary letter for ${invoice.id}", stringInterpolate,
                Invoice.class, BinderForReportServer.class,
                executionContext);
        createTemplateWithApplicability(
                invoicePrelim,
                ApplicationTenancyForNl.PATH, " (Netherlands)",
                false, url
                + "Preliminary+Letter"
                + "&id=${this.id}"
                + "&rs:Command=Render&rs:Format=PDF"
                + "&appTenancy=/NLD", stringInterpolatePreviewCapture,
                "Preliminary letter for ${this.id} (Netherlands)", stringInterpolate,
                Invoice.class, BinderForReportServer.class,
                executionContext);

        createTemplateWithApplicability(
                createType(DOC_TYPE_REF_INVOICE, "Invoice"),
                ApplicationTenancyForGlobal.PATH, null,
                false,
                url
                + "Invoice"
                + "&id=${this.id}"
                + "&rs:Command=Render&rs:Format=PDF", stringInterpolatePreviewCapture,
                "Invoice for ${this.id}", stringInterpolate,
                Invoice.class, BinderForReportServer.class,
                executionContext);


        // InvoiceSummaryForPropertyDueDateStatus
        createTemplateWithApplicability(
                createType(DOC_TYPE_REF_INVOICES_OVERVIEW, "Invoices overview"),
                ApplicationTenancyForGlobal.PATH, null,
                true,
                url
                + "Invoices"
                + "&dueDate=${this.dueDate}&${this.seller.id}&atPath=${this.atPath}"
                + "&rs:Command=Render&rs:Format=PDF", stringInterpolatePreviewCapture,
                "Invoices overview", stringInterpolate,
                InvoiceSummaryForPropertyDueDateStatus.class,
                BinderForReportServerForInvoiceSummaryForPropertyDueDateStatus.class,
                executionContext);

        createTemplateWithApplicability(
                createType(DOC_TYPE_REF_INVOICES_PRELIM, "Preliminary letter for Invoices"),
                ApplicationTenancyForGlobal.PATH, null,
                true,
                url
                + "Preliminary+Letter"
                + "&dueDate=${this.dueDate}&sellerId=${this.seller.id}&atPath=${this.atPath}"
                + "&rs:Command=Render&rs:Format=PDF", stringInterpolatePreviewCapture,
                "Preliminary letter for Invoices", stringInterpolate,
                InvoiceSummaryForPropertyDueDateStatus.class,
                BinderForReportServerForInvoiceSummaryForPropertyDueDateStatus.class,
                executionContext);

        createTemplateWithApplicability(
                createType(DOC_TYPE_REF_INVOICES_PRELIM_FOR_SELLER, "Preliminary Invoice for Seller"),
                ApplicationTenancyForGlobal.PATH, null,
                true,
                url
                + "Preliminary+Letter"
                + "&dueDate=${this.dueDate}&sellerId=${this.seller.id}&atPath=${this.atPath}"
                + "&rs:Command=Render&rs:Format=PDF", stringInterpolatePreviewCapture,
                "Preliminary Invoice for Seller", stringInterpolate,
                InvoiceSummaryForPropertyDueDateStatus.class,
                BinderForReportServerForInvoiceSummaryForPropertyDueDateStatus.class,
                executionContext);
    }

    private DocumentTemplate createTemplateWithApplicability(
            final DocumentType documentType,
            final String atPath, final String nameSuffixIfAny,
            final boolean previewOnly,
            final String text, final RenderingStrategy contentRenderingStrategy,
            final String subjectText, final RenderingStrategy subjectRenderingStrategy,
            final Class<?> applicableToClass,
            final Class<? extends Binder> binderClass,
            final ExecutionContext executionContext) {

        final DocumentTemplate template =
                createTemplate(documentType, atPath, nameSuffixIfAny, previewOnly, text, contentRenderingStrategy, subjectText,
                        subjectRenderingStrategy, executionContext);
        template.applicable(applicableToClass, binderClass);

        return template;
    }

    private DocumentType createType(final String docTypeRef, final String docTypeName) {
        return documentTypeRepository.create(docTypeRef, docTypeName);
    }

    private DocumentTemplate createTemplate(
            final DocumentType docType,
            final String atPath,
            final String nameSuffixIfAny,
            final boolean previewOnly,
            final String text, final RenderingStrategy contentRenderingStrategy,
            final String subjectText, final RenderingStrategy subjectRenderingStrategy,
            final ExecutionContext executionContext) {
        final LocalDate now = clockService.now();

        final ApplicationTenancy appTenancy = applicationTenancyRepository.findByPath(atPath);

        return createDocumentTextTemplate(
                docType, now, appTenancy.getPath(), ".pdf", previewOnly,
                docType.getName() + (nameSuffixIfAny != null? nameSuffixIfAny : ""),
                "application/pdf",
                text,
                contentRenderingStrategy,
                subjectText, subjectRenderingStrategy, executionContext);
    }

    @Inject
    private ApplicationTenancyRepository applicationTenancyRepository;
    @Inject
    private RenderingStrategyRepository renderingStrategyRepository;
    @Inject
    private ClockService clockService;


}
