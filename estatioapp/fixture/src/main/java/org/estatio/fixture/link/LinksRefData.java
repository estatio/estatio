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
package org.estatio.fixture.link;

import javax.inject.Inject;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancies;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.viewmodel.InvoiceSummaryForPropertyDueDateStatus;
import org.estatio.fixture.EstatioFixtureScript;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForIt;
import org.estatio.domlink.Link;
import org.estatio.domlink.Links;

public class LinksRefData extends EstatioFixtureScript {

    @Inject
    private ApplicationTenancies applicationTenancies;

    @Override
    protected void execute(final ExecutionContext executionContext) {

        ApplicationTenancy italyAppTenancy = applicationTenancies.findTenancyByPath(ApplicationTenancyForIt.PATH);

        newLink(italyAppTenancy, Invoice.class,
                "Preliminary letter",
                "${reportServerBaseUrl}/reportserver?/Estatio/"
                        + "Preliminary+Letter&id=${this.id}&rs:Command=Render", executionContext);
        newLink(italyAppTenancy, Invoice.class,
                "Invoice",
                "${reportServerBaseUrl}/reportserver?/Estatio/"
                + "Invoice&id=${this.id}&rs:Command=Render", executionContext);

        newLink(italyAppTenancy, InvoiceSummaryForPropertyDueDateStatus.class,
                "Invoices overview",
                "${reportServerBaseUrl}/ReportServer/Pages/ReportViewer.aspx?/Estatio/"
                + "Invoices&dueDate=${this.dueDate}&propertyId=${this.property.id}&rs:Command=Render", executionContext);
        newLink(italyAppTenancy, InvoiceSummaryForPropertyDueDateStatus.class,
                "Preliminary letter",
                "${reportServerBaseUrl}/ReportServer/Pages/ReportViewer.aspx?/Estatio/"
                + "Preliminary+Letter&dueDate=${this.dueDate}&propertyId=${this.property.id}&rs:Command=Render", executionContext);
        newLink(italyAppTenancy, InvoiceSummaryForPropertyDueDateStatus.class,
                "Invoice",
                "${reportServerBaseUrl}/ReportServer/Pages/ReportViewer.aspx?/Estatio/"
                + "Invoice&dueDate=${this.dueDate}&propertyId=${this.property.id}&rs:Command=Render", executionContext);
    }

    private Link newLink(
            final ApplicationTenancy applicationTenancy,
            final Class<?> clsClass,
            final String name,
            final String urlTemplate,
            final ExecutionContext executionContext) {
        final Link link = links.newLink(applicationTenancy, clsClass, name, urlTemplate);
        return executionContext.addResult(this, link.getName(), link);
    }

    @javax.inject.Inject
    private Links links;

}
