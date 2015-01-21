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
package org.estatio.app.lease.turnoverrent;

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
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.ViewModel;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;

import org.estatio.app.EstatioViewModel;
import org.estatio.dom.asset.Property;
import org.estatio.dom.lease.LeaseItemType;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.lease.LeaseTermForTurnoverRent;
import org.estatio.dom.lease.LeaseTerms;

@Immutable
@Bookmarkable
@ViewModel
public class LeaseTermForTurnoverRentManager extends EstatioViewModel {

    public LeaseTermForTurnoverRentManager() {
    }

    public LeaseTermForTurnoverRentManager(Property property, LocalDate startDate) {
        this.property = property;
        this.startDate = startDate;
    }

    // //////////////////////////////////////

    private Property property;

    @Title(sequence = "1")
    @MemberOrder(sequence = "1")
    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    @Named("Select")
    @MemberOrder(name = "property", sequence = "1")
    public LeaseTermForTurnoverRentManager selectProperty(
            final Property property,
            @Named("Start date") final LocalDate startDate) {
        setProperty(property);
        setStartDate(startDate);
        return this;
    }

    public List<LocalDate> choices1SelectProperty(Property property) {
        return leaseTerms.findStartDatesByPropertyAndType(property, LeaseItemType.SERVICE_CHARGE);
    }

    // //////////////////////////////////////

    private LocalDate startDate;

    @Title(sequence = "2", prepend = "@")
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
    public LeaseTermForTurnoverRentManager selectStartDate(
            @Named("Start date") final LocalDate startDate) {
        setStartDate(startDate);
        return this;
    }

    public List<LocalDate> choices0SelectStartDate() {
        return leaseTerms.findStartDatesByPropertyAndType(property, LeaseItemType.SERVICE_CHARGE);
    }

    public LocalDate default0SelectStartDate() {
        return getStartDate();
    }

    // //////////////////////////////////////
    // TurnoverRents (collection)
    // //////////////////////////////////////

    @Render(Type.EAGERLY)
    public List<LeaseTermForTurnoverRentLineItem> getTurnoverRents() {
        final List<LeaseTerm> terms = leaseTerms.findByPropertyAndTypeAndStartDate(getProperty(), LeaseItemType.TURNOVER_RENT, getStartDate());
        return Lists.transform(terms, newLeaseTermForTurnoverRentAuditBulkUpdate());
    }

    private Function<LeaseTerm, LeaseTermForTurnoverRentLineItem> newLeaseTermForTurnoverRentAuditBulkUpdate() {
        return new Function<LeaseTerm, LeaseTermForTurnoverRentLineItem>() {
            @Override
            public LeaseTermForTurnoverRentLineItem apply(final LeaseTerm leaseTerm) {
                return new LeaseTermForTurnoverRentLineItem(leaseTerm);
            }
        };
    }

    // //////////////////////////////////////
    // download (action)
    // //////////////////////////////////////

    @MemberOrder(name = "turnover", sequence = "1")
    public Blob download() {
        final String fileName = "TurnoverRentBulkUpdate-" + getProperty().getReference() + "@" + getStartDate() + ".xlsx";
        final List<LeaseTermForTurnoverRentLineItem> lineItems = getTurnoverRents();
        return excelService.toExcel(lineItems, LeaseTermForTurnoverRentLineItem.class, fileName);
    }

    // //////////////////////////////////////
    // upload (action)
    // //////////////////////////////////////

    @MemberOrder(name = "turnover", sequence = "2")
    public LeaseTermForTurnoverRentManager upload(final @Named("Excel spreadsheet") Blob spreadsheet) {
        List<LeaseTermForTurnoverRentLineItem> lineItems =
                excelService.fromExcel(spreadsheet, LeaseTermForTurnoverRentLineItem.class);
        for (LeaseTermForTurnoverRentLineItem lineItem : lineItems) {
            final LeaseTermForTurnoverRent leaseTerm = lineItem.getLeaseTerm();
            leaseTerm.setAuditedTurnover(lineItem.getAuditedTurnover());
            leaseTerm.verify();
        }
        return this;
    }

    // //////////////////////////////////////
    // injected services
    // //////////////////////////////////////

    @javax.inject.Inject
    private LeaseTerms leaseTerms;

    @javax.inject.Inject
    private ExcelService excelService;

    @javax.inject.Inject
    private LeaseTermForTurnoverRentService budgetAuditService;

}
