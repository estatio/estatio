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

import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.utils.StringUtils;

@Hidden
public class InvoiceContributions {

    @NotInServiceMenu
    public URL reports(
            final @Named("Invoice") Invoice invoice,
            final @Named("Report") InvoiceReportType reportType
            ) {
        try {
            return new URL(reportType.parse(invoice));
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public enum InvoiceReportType {

        INVOICE("http://ams-s-sql08/reportserver?/Estatio/Invoice&id={invoiceId}&rs:Command=Render"),
        PRELIMINARY_LETTER("http://ams-s-sql08/reportserver?/Estatio/Preliminary+Letter&id={invoiceId}&rs:Command=Render");

        private String url;

        InvoiceReportType(final String url) {
            this.url = url;
        }

        public String parse(final Invoice invoice) {
            return url.replace("{invoiceId}", invoice.getId().toString());
        }

        public String title() {
            return StringUtils.enumTitle(this.name());
        }

    }

}
