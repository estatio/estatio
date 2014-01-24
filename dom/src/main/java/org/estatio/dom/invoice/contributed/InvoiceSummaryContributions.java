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
package org.estatio.dom.invoice.contributed;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotInServiceMenu;

import org.estatio.dom.invoice.viewmodel.InvoiceSummaryForPropertyDueDate;
import org.estatio.dom.invoice.viewmodel.InvoiceSummaryForPropertyDueDateStatus;

@Hidden
public class InvoiceSummaryContributions {

    @NotInServiceMenu
    public URL reports(
            final @Named("Invoice Summary") InvoiceSummaryForPropertyDueDate invoiceSummary,
            final @Named("Report") InvoiceSummaryReportType reportType
            ) {
        try {
            return new URL(reportType.parse(invoiceSummary));
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @NotInServiceMenu
    public URL reports(
            final @Named("Invoice Summary") InvoiceSummaryForPropertyDueDateStatus invoiceSummary,
            final @Named("Report") InvoiceSummaryReportType reportType
            ) {
        try {
            return new URL(reportType.parse(invoiceSummary));
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public enum InvoiceSummaryReportType {

        INVOICES_OVERVIEW("http://ams-s-sql08/ReportServer/Pages/ReportViewer.aspx?/Estatio/Invoices&dueDate={dueDate}&propertyId={propertyId}");
        private String url;

        InvoiceSummaryReportType(final String url) {
            this.url = url;
        }

        public String parse(final InvoiceSummaryForPropertyDueDate invoiceSummary) {
            return url
                    .replace("{dueDate}", invoiceSummary.getDueDate().toString())
                    .replace("{propertyId}", invoiceSummary.getProperty().getId());
        }

        public String parse(final InvoiceSummaryForPropertyDueDateStatus invoiceSummary) {
            return url
                    .replace("{dueDate}", invoiceSummary.getDueDate().toString())
                    .replace("{invoiceStatus}", invoiceSummary.getStatus())
                    .replace("{propertyId}", invoiceSummary.getProperty().getId());
        }

    }

}
