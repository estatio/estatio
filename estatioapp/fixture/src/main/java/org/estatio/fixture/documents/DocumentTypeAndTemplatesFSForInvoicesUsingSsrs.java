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

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.services.clock.ClockService;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

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
        executionContext.executeChild(this, new RenderingStrategyFSForSsrs());

        final String url = "${reportServerBaseUrl}";

        final RenderingStrategy renderingStrategy =
                renderingStrategyRepository.findByReference(RenderingStrategyFSForSsrs.REF);

        // Invoice (two templates, one global and one for the NL)
        final DocumentType invoicePrelim = createType(DOC_TYPE_REF_INVOICE_PRELIM, "Preliminary letter for Invoice");
        createTemplateWithApplicability(
                invoicePrelim,
                ApplicationTenancyForGlobal.PATH,
                renderingStrategy,
                url
                + "Preliminary+Letter"
                + "&id=${this.id}"
                + "&rs:Command=Render",
                null,
                Invoice.class, BinderForReportServer.class,
                executionContext);
        createTemplateWithApplicability(
                invoicePrelim,
                ApplicationTenancyForNl.PATH,
                renderingStrategy,
                url
                + "Preliminary+Letter"
                + "&id=${this.id}"
                + "&rs:Command=Render"
                + "&appTenancy=/NLD",
                " (Netherlands)",
                Invoice.class, BinderForReportServer.class,
                executionContext);

        createTemplateWithApplicability(
                createType(DOC_TYPE_REF_INVOICE, "Invoice"),
                ApplicationTenancyForGlobal.PATH,
                renderingStrategy, url
                + "Invoice"
                + "&id=${this.id}"
                + "&rs:Command=Render",
                null,
                Invoice.class, BinderForReportServer.class,
                executionContext);


        // InvoiceSummaryForPropertyDueDateStatus
        createTemplateWithApplicability(
                createType(DOC_TYPE_REF_INVOICES_OVERVIEW, "Invoices overview"),
                ApplicationTenancyForGlobal.PATH,
                renderingStrategy,
                url
                + "Invoices"
                + "&dueDate=${this.dueDate}&${this.seller.id}&atPath=${this.atPath}"
                + "&rs:Command=Render",
                null,
                InvoiceSummaryForPropertyDueDateStatus.class,
                BinderForReportServerForInvoiceSummaryForPropertyDueDateStatus.class,
                executionContext);

        createTemplateWithApplicability(
                createType(DOC_TYPE_REF_INVOICES_PRELIM, "Preliminary letter for Invoices"),
                ApplicationTenancyForGlobal.PATH,
                renderingStrategy,
                url
                + "Preliminary+Letter"
                + "&dueDate=${this.dueDate}&sellerId=${this.seller.id}&atPath=${this.atPath}"
                + "&rs:Command=Render",
                null,
                InvoiceSummaryForPropertyDueDateStatus.class,
                BinderForReportServerForInvoiceSummaryForPropertyDueDateStatus.class,
                executionContext);

        createTemplateWithApplicability(
                createType(DOC_TYPE_REF_INVOICES_PRELIM_FOR_SELLER, "Invoice for Seller"),
                ApplicationTenancyForGlobal.PATH,
                renderingStrategy,
                url
                + "Preliminary+Letter"
                + "&dueDate=${this.dueDate}&sellerId=${this.seller.id}&atPath=${this.atPath}"
                + "&rs:Command=Render",
                null,
                InvoiceSummaryForPropertyDueDateStatus.class,
                BinderForReportServerForInvoiceSummaryForPropertyDueDateStatus.class,
                executionContext);



    }

    private List<DocumentTemplate> createTemplateWithApplicability(
            final DocumentType documentType,
            final List<String> atPaths,
            final RenderingStrategy renderingStrategy,
            final String templateText,
            final Class<?> applicableToClass,
            final Class<? extends Binder> binderClass,
            final ExecutionContext executionContext) {
        List<DocumentTemplate> templates = atPaths.stream()
                .map(atPath -> createTemplateWithApplicability(
                        documentType, atPath, renderingStrategy,
                        templateText, null, applicableToClass, binderClass, executionContext))
                .collect(Collectors.toList());
        return templates;
    }

    private DocumentTemplate createTemplateWithApplicability(
            final DocumentType documentType,
            final String atPath,
            final RenderingStrategy renderingStrategy,
            final String templateText,
            final String nameSuffix, final Class<?> applicableToClass,
            final Class<? extends Binder> binderClass,
            final ExecutionContext executionContext) {

        final DocumentTemplate template =
                createTemplate(documentType, atPath, renderingStrategy, templateText, nameSuffix, executionContext);
        template.applicable(applicableToClass, binderClass);

        return template;
    }

    private DocumentType createType(final String docTypeRef, final String docTypeName) {
        return documentTypeRepository.create(docTypeRef, docTypeName);
    }

    private DocumentTemplate createTemplate(
            final DocumentType docType,
            final String atPath,
            final RenderingStrategy renderingStrategy,
            final String templateText,
            final String nameSuffix,
            final ExecutionContext executionContext) {
        final LocalDate now = clockService.now();

        final ApplicationTenancy appTenancy = applicationTenancyRepository.findByPath(atPath);

        return createDocumentTextTemplate(
                docType, now, docType.getName() + (nameSuffix != null? nameSuffix : ""),
                "application/pdf",
                ".pdf",
                appTenancy.getPath(),
                templateText,
                renderingStrategy,
                executionContext);
    }

    @Inject
    private ApplicationTenancyRepository applicationTenancyRepository;
    @Inject
    private RenderingStrategyRepository renderingStrategyRepository;
    @Inject
    private ClockService clockService;


}
