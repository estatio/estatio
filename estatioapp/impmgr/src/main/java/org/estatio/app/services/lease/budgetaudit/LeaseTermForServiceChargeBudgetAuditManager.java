/*
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
package org.estatio.app.services.lease.budgetaudit;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;

import org.incode.module.base.dom.utils.TitleBuilder;

import org.estatio.dom.asset.Property;
import org.estatio.dom.lease.LeaseTermForServiceCharge;
import org.estatio.dom.lease.LeaseTermRepository;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.app.services.lease.budgetaudit.LeaseTermForServiceChargeBudgetAuditManager",
        editing = Editing.DISABLED
)
public class LeaseTermForServiceChargeBudgetAuditManager  {

    //region > constructor, title
    public LeaseTermForServiceChargeBudgetAuditManager() {
    }

    public LeaseTermForServiceChargeBudgetAuditManager(Property property, LocalDate startDate) {
        this.property = property;
        this.startDate = startDate;
    }

    public String title() {
        return TitleBuilder.start()
                .withParent(getProperty())
                .withName(getStartDate())
                .toString();
    }

    //endregion


    @Getter @Setter
    private Property property;

    //region > selectProperty (action)
    public LeaseTermForServiceChargeBudgetAuditManager selectProperty(
            final Property property,
            @ParameterLayout(named = "Start date")
            final LocalDate startDate) {
        setProperty(property);
        setStartDate(startDate);
        return this;
    }

    public List<LocalDate> choices1SelectProperty(Property property) {
        return leaseTermRepository.findServiceChargeDatesByProperty(property);
    }
    //endregion


    @Getter @Setter
    @org.apache.isis.applib.annotation.Property(optionality = Optionality.OPTIONAL)
    private LocalDate startDate;


    //region > selectStartDate (action)
    public LeaseTermForServiceChargeBudgetAuditManager selectStartDate(
            @ParameterLayout(named = "Start date")
            final LocalDate startDate) {
        setStartDate(startDate);
        return this;
    }

    public List<LocalDate> choices0SelectStartDate() {
        return leaseTermRepository.findServiceChargeDatesByProperty(property);
    }

    public LocalDate default0SelectStartDate() {
        return getStartDate();
    }
    //endregion

    //region > serviceCharges (derived collection)

    public List<LeaseTermForServiceChargeBudgetAuditLineItem> getServiceCharges() {
        final List<LeaseTermForServiceCharge> terms = leaseTermRepository.findServiceChargeByPropertyAndStartDate(getProperty(), getStartDate());
        return Lists.transform(terms, newLeaseTermForServiceChargeAuditBulkUpdate());
    }

    private Function<LeaseTermForServiceCharge, LeaseTermForServiceChargeBudgetAuditLineItem> newLeaseTermForServiceChargeAuditBulkUpdate() {
        return new Function<LeaseTermForServiceCharge, LeaseTermForServiceChargeBudgetAuditLineItem>() {
            @Override
            public LeaseTermForServiceChargeBudgetAuditLineItem apply(final LeaseTermForServiceCharge leaseTerm) {
                return new LeaseTermForServiceChargeBudgetAuditLineItem(leaseTerm);
            }
        };
    }

    //endregion


    //region > download (action)
    @Action(semantics = SemanticsOf.SAFE)
    public Blob download() {
        final String fileName = "ServiceChargeBulkUpdate-" + getProperty().getReference() + "@" + getStartDate() + ".xlsx";
        final List<LeaseTermForServiceChargeBudgetAuditLineItem> lineItems = getServiceCharges();
        return excelService.toExcel(lineItems, LeaseTermForServiceChargeBudgetAuditLineItem.class, "lease terms", fileName);
    }
    //endregion

    //region > upload (action)
    @Action(publishing = Publishing.DISABLED, semantics = SemanticsOf.IDEMPOTENT)
    public LeaseTermForServiceChargeBudgetAuditManager upload(
            @Parameter(fileAccept = ".xlsx")
            @ParameterLayout(named = "Excel spreadsheet")
            final Blob spreadsheet) {
        List<LeaseTermForServiceChargeBudgetAuditLineItem> lineItems =
                excelService.fromExcel(spreadsheet, LeaseTermForServiceChargeBudgetAuditLineItem.class, "lease terms");
        for (LeaseTermForServiceChargeBudgetAuditLineItem lineItem : lineItems) {
            final LeaseTermForServiceCharge leaseTerm = lineItem.getLeaseTerm();
            leaseTerm.setAuditedValue(lineItem.getAuditedValue());
            leaseTerm.setBudgetedValue(lineItem.getBudgetedValue());

            final LeaseTermForServiceCharge nextLeaseTerm = (LeaseTermForServiceCharge) leaseTerm.getNext();
            final LeaseTermForServiceCharge nextLeaseTermUploaded = lineItem.getNextLeaseTerm();
            if (nextLeaseTerm != null && nextLeaseTerm == nextLeaseTermUploaded) {
                nextLeaseTerm.setBudgetedValue(lineItem.getNextBudgetedValue());
            }
        }
        return this;
    }
    //endregion

    //region > injected services
    @javax.inject.Inject
    private LeaseTermRepository leaseTermRepository;

    @javax.inject.Inject
    private ExcelService excelService;
    //endregion

}
