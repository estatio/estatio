/*
 * Copyright 2012-2015 Eurocommercial Properties NV
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.estatio.app.services.invoice;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;
import org.isisaddons.module.excel.dom.WorksheetContent;
import org.isisaddons.module.excel.dom.WorksheetSpec;

import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseItemRepository;
import org.estatio.dom.lease.LeaseItemType;
import org.estatio.dom.lease.LeaseRepository;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.app.services.invoice.InvoiceImportManager"
)
public class InvoiceImportManager {

    public String title() {
        return "Import manager for invoice for lease";
    }

    public InvoiceImportManager() {
        this.name = "Invoice Import";
    }

    public InvoiceImportManager(final Property property){
        this();
        this.property = property;
    }

    @Getter @Setter
    private Property property;

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(cssClassFa = "fa-download")
    public Blob downloadTemplate() {
        final String fileName = "template.xlsx";
        WorksheetSpec spec = new WorksheetSpec(InvoiceImportLine.class, "invoiceImportLine");
        WorksheetContent worksheetContent = new WorksheetContent(getLines(), spec);
        return excelService.toExcel(worksheetContent, fileName);
    }

    @Action(publishing = Publishing.DISABLED, semantics = SemanticsOf.IDEMPOTENT)
    @CollectionLayout(paged = -1)
    public List<InvoiceImportLine> importInvoices(
            @Parameter(fileAccept = ".xlsx")
            @ParameterLayout(named = "Excel spreadsheet")
            final Blob spreadsheet) {
        List<InvoiceImportLine> lineItems =
                excelService.fromExcel(spreadsheet, InvoiceImportLine.class, InvoiceImportLine.class.getSimpleName());
        return lineItems;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<InvoiceImportLine> getLines(){
        List<InvoiceImportLine> result = new ArrayList<>();
        for (Lease lease : leaseRepository.findByAssetAndActiveOnDate(getProperty(), clockService.now())){

            PaymentMethod paymentMethod = null;
            Unit unit = lease.primaryOccupancy().get().getUnit();

            if (lease.getItems().size()>0) {

                if (leaseItemRepository.findLeaseItemsByType(lease, LeaseItemType.RENT).size() > 0) {
                    paymentMethod = leaseItemRepository.findLeaseItemsByType(lease, LeaseItemType.RENT).get(0).getPaymentMethod();
                } else {
                    paymentMethod = lease.getItems().first().getPaymentMethod();
                }

                result.add(new InvoiceImportLine(lease.getReference(), null, paymentMethod.name(), null, null, null, null, null, unit.getReference()));

            } else {

                result.add(new InvoiceImportLine(lease.getReference(), null, null, null, null, null, null, null, unit.getReference()));

            }

        }
        return result;
    }

    @Getter @Setter
    private String name;

    @Inject
    private ExcelService excelService;

    @Inject
    private LeaseRepository leaseRepository;

    @Inject
    private ClockService clockService;

    @Inject
    private LeaseItemRepository leaseItemRepository;

}
