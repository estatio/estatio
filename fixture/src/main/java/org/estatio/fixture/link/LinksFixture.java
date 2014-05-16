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

import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.viewmodel.InvoiceSummaryForPropertyDueDateStatus;
import org.estatio.services.links.Link;
import org.estatio.services.links.Links;
import org.apache.isis.applib.fixturescripts.SimpleFixtureScript;

public class LinksFixture extends SimpleFixtureScript {

    @Override
    protected void execute(ExecutionContext fixtureResults) {
        newLink(Invoice.class, "Preliminary letter",
                "${reportServerBaseUrl}/reportserver?/Estatio/"
                        + "Preliminary+Letter&id=${this.id}&rs:Command=Render",
                fixtureResults);
        newLink(Invoice.class, "Invoice",
                "${reportServerBaseUrl}/reportserver?/Estatio/"
                + "Invoice&id=${this.id}&rs:Command=Render",
                fixtureResults);

        newLink(InvoiceSummaryForPropertyDueDateStatus.class, "Invoices overview",
                "${reportServerBaseUrl}/ReportServer/Pages/ReportViewer.aspx?/Estatio/"
                + "Invoices&dueDate=${this.dueDate}&propertyId=${this.property.id}&rs:Command=Render",
                fixtureResults);
        newLink(InvoiceSummaryForPropertyDueDateStatus.class, "Preliminary letter",
                "${reportServerBaseUrl}/ReportServer/Pages/ReportViewer.aspx?/Estatio/"
                + "Preliminary+Letter&dueDate=${this.dueDate}&propertyId=${this.property.id}&rs:Command=Render",
                fixtureResults);
        newLink(InvoiceSummaryForPropertyDueDateStatus.class, "Invoice",
                "${reportServerBaseUrl}/ReportServer/Pages/ReportViewer.aspx?/Estatio/"
                + "Invoice&dueDate=${this.dueDate}&propertyId=${this.property.id}&rs:Command=Render",
                fixtureResults);
    }

    private Link newLink(Class<?> clsClass, String name, String urlTemplate, ExecutionContext fixtureResults) {
        final Link link = links.newLink(clsClass, name, urlTemplate);
        return fixtureResults.add(this, link.getName(), link);
    }

    @javax.inject.Inject
    private Links links;

}
