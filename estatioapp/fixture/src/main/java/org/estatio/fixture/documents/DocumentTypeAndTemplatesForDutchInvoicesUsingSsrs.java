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

import org.incode.module.documents.dom.docs.DocumentTemplate;
import org.incode.module.documents.dom.rendering.RenderingStrategy;
import org.incode.module.documents.dom.rendering.RenderingStrategyRepository;
import org.incode.module.documents.dom.types.DocumentType;

import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.viewmodel.InvoiceSummaryForPropertyDueDateStatus;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForNl;

public class DocumentTypeAndTemplatesForDutchInvoicesUsingSsrs extends DocumentTemplateAbstract {

    public static final String AT_PATH = ApplicationTenancyForNl.PATH;

    // applicable to Invoice.class
    public static final String DOC_TYPE_REF_INVOICE_PRELIM_NLD = "INVOICE-PRELIM/NLD";
    public static final String DOC_TYPE_REF_INVOICE_NLD = "INVOICE/NLD";

    // applicable to InvoiceSummaryForPropertyDueDateStatus.class
    public static final String DOC_TYPE_REF_INVOICES_OVERVIEW_NLD = "INVOICES/NLD";
    public static final String DOC_TYPE_REF_INVOICES_PRELIM_NLD = "INVOICES-PRELIM/NLD";
    public static final String DOC_TYPE_REF_INVOICES_PRELIM_FOR_SELLER_NLD = "INVOICES-FOR-SELLER/NLD";

    @Override
    protected void execute(final ExecutionContext executionContext) {

        // prereqs
        if (isExecutePrereqs()) {
            executionContext.executeChild(this, new RenderingStrategyForSsrs());
        }

        final String url = "${reportServerBaseUrl}";

        final RenderingStrategy renderingStrategy =
                renderingStrategyRepository.findByReference(RenderingStrategyForSsrs.REF);

        final DocumentTemplate dutchPrelimInvoiceTemplate = createTypeAndTemplate(
                DOC_TYPE_REF_INVOICE_PRELIM_NLD,
                "Preliminary letter for Invoice (Netherlands)",
                AT_PATH,
                renderingStrategy,
                url
                        + "Preliminary+Letter"
                        + "&id=${this.id}"
                        + "&rs:Command=Render",
                executionContext);

        final DocumentTemplate dutchInvoiceTemplate = createTypeAndTemplate(
                DOC_TYPE_REF_INVOICE_NLD,
                "Invoice (Netherlands)",
                AT_PATH,
                renderingStrategy,
                url
                        + "Invoice"
                        + "&id=${this.id}"
                        + "&rs:Command=Render",
                executionContext);

        dutchPrelimInvoiceTemplate.applicable(
                Invoice.class.getName(), BinderForReportServer.class.getName());
        dutchInvoiceTemplate.applicable(
                Invoice.class.getName(), BinderForReportServer.class.getName());


        final DocumentTemplate dutchInvoicesOverviewTemplate = createTypeAndTemplate(
                DOC_TYPE_REF_INVOICES_OVERVIEW_NLD,
                "Invoices overview (Netherlands)",
                AT_PATH,
                renderingStrategy,
                url
                        + "Invoices"
                        + "&dueDate=${this.dueDate}&${this.seller.id}&atPath=${this.atPath}"
                        + "&rs:Command=Render",
                executionContext);

        final DocumentTemplate dutchPrelimInvoicesTemplate = createTypeAndTemplate(
                DOC_TYPE_REF_INVOICES_PRELIM_NLD,
                "Preliminary letter for Invoices (Netherlands)",
                AT_PATH,
                renderingStrategy,
                url
                        + "Preliminary+Letter"
                        + "&dueDate=${this.dueDate}&sellerId=${this.seller.id}&atPath=${this.atPath}"
                        + "&rs:Command=Render",
                executionContext);

        final DocumentTemplate dutchInvoicesForSellerTemplate = createTypeAndTemplate(
                DOC_TYPE_REF_INVOICES_PRELIM_FOR_SELLER_NLD,
                "Invoice for Seller (Netherlands)",
                AT_PATH,
                renderingStrategy,
                url
                        + "Preliminary+Letter"
                        + "&dueDate=${this.dueDate}&sellerId=${this.seller.id}&atPath=${this.atPath}"
                        + "&rs:Command=Render",
                executionContext);

        dutchInvoicesOverviewTemplate.applicable(
                InvoiceSummaryForPropertyDueDateStatus.class.getName(),
                BinderForReportServerForInvoiceSummaryForPropertyDueDateStatus.class.getName());
        dutchPrelimInvoicesTemplate.applicable(
                InvoiceSummaryForPropertyDueDateStatus.class.getName(),
                BinderForReportServerForInvoiceSummaryForPropertyDueDateStatus.class.getName());
        dutchInvoicesForSellerTemplate.applicable(
                InvoiceSummaryForPropertyDueDateStatus.class.getName(),
                BinderForReportServerForInvoiceSummaryForPropertyDueDateStatus.class.getName());
    }

    protected DocumentTemplate createTypeAndTemplate(
            final String docTypeRef,
            final String docTypeName,
            final String atPath,
            final RenderingStrategy renderingStrategy,
            final String templateText,
            final ExecutionContext executionContext) {


        final DocumentType docType = documentTypeRepository.create(docTypeRef, docTypeName);

        final LocalDate now = clockService.now();

        final ApplicationTenancy appTenancy = applicationTenancyRepository.findByPath(atPath);

        return createDocumentTextTemplate(
                docType, now, docType.getName(),
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
