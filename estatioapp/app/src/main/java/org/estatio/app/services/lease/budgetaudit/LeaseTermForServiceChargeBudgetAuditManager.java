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

import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;
import org.apache.isis.applib.annotation.ViewModel;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;

import org.estatio.app.EstatioViewModel;
import org.estatio.dom.asset.Property;
import org.estatio.dom.lease.LeaseTermForServiceCharge;
import org.estatio.dom.lease.LeaseTermRepository;
import org.estatio.dom.utils.TitleBuilder;

@Immutable
@Bookmarkable
@ViewModel
public class LeaseTermForServiceChargeBudgetAuditManager extends EstatioViewModel {

    public LeaseTermForServiceChargeBudgetAuditManager() {
    }

    public String title() {
        return TitleBuilder.start()
                .withParent(getProperty())
                .withName(getStartDate())
                .toString();
    }

    public LeaseTermForServiceChargeBudgetAuditManager(Property property, LocalDate startDate) {
        this.property = property;
        this.startDate = startDate;
    }

    private Property property;

    @MemberOrder(sequence = "1")
    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    @Named("Select")
    @MemberOrder(name = "property", sequence = "1")
    public LeaseTermForServiceChargeBudgetAuditManager selectProperty(
            final Property property,
            @Named("Start date") final LocalDate startDate) {
        setProperty(property);
        setStartDate(startDate);
        return this;
    }

    public List<LocalDate> choices1SelectProperty(Property property) {
        return leaseTermRepository.findServiceChargeDatesByProperty(property);
    }

    // //////////////////////////////////////

    private LocalDate startDate;

    @Optional
    @MemberOrder(sequence = "2")
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }

    // //////////////////////////////////////

    @Named("Select")
    @MemberOrder(name = "startDate", sequence = "1")
    public LeaseTermForServiceChargeBudgetAuditManager selectStartDate(
            @Named("Start date") final LocalDate startDate) {
        setStartDate(startDate);
        return this;
    }

    public List<LocalDate> choices0SelectStartDate() {
        return leaseTermRepository.findServiceChargeDatesByProperty(property);
    }

    public LocalDate default0SelectStartDate() {
        return getStartDate();
    }

    // //////////////////////////////////////

    @Render(Type.EAGERLY)
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

    // //////////////////////////////////////

    @MemberOrder(name = "serviceCharges", sequence = "1")
    public Blob download() {
        final String fileName = "ServiceChargeBulkUpdate-" + getProperty().getReference() + "@" + getStartDate() + ".xlsx";
        final List<LeaseTermForServiceChargeBudgetAuditLineItem> lineItems = getServiceCharges();
        return excelService.toExcel(lineItems, LeaseTermForServiceChargeBudgetAuditLineItem.class, fileName);
    }

    // //////////////////////////////////////

    @MemberOrder(name = "serviceCharges", sequence = "2")
    public LeaseTermForServiceChargeBudgetAuditManager upload(final @Named("Excel spreadsheet") Blob spreadsheet) {
        List<LeaseTermForServiceChargeBudgetAuditLineItem> lineItems =
                excelService.fromExcel(spreadsheet, LeaseTermForServiceChargeBudgetAuditLineItem.class);
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

    // //////////////////////////////////////

    @javax.inject.Inject
    private LeaseTermRepository leaseTermRepository;

    @javax.inject.Inject
    private ExcelService excelService;

}
