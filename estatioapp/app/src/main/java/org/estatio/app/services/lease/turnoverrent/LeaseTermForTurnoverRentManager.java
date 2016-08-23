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
package org.estatio.app.services.lease.turnoverrent;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;

import org.estatio.dom.asset.Property;
import org.estatio.dom.lease.LeaseItemType;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.lease.LeaseTermForTurnoverRent;
import org.estatio.dom.lease.LeaseTermRepository;
import org.estatio.dom.utils.TitleBuilder;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        editing = Editing.DISABLED
)
public class LeaseTermForTurnoverRentManager {

    //region > constructor, title
    public LeaseTermForTurnoverRentManager() {
    }

    public LeaseTermForTurnoverRentManager(Property property, LocalDate startDate) {
        this.property = property;
        this.startDate = startDate;
    }

    public String title() {
        return TitleBuilder.start()
                .withName(getProperty())
                .withName(getStartDate())
                .toString();
    }
    //endregion


    @Getter @Setter
    private Property property;

    //region > selectProperty (action)
    public LeaseTermForTurnoverRentManager selectProperty(
            final Property property,
            @ParameterLayout(named = "Start date")
            final LocalDate startDate) {
        setProperty(property);
        setStartDate(startDate);
        return this;
    }

    public List<LocalDate> choices1SelectProperty(Property property) {
        return leaseTermRepository.findStartDatesByPropertyAndType(property, LeaseItemType.SERVICE_CHARGE);
    }
    //endregion


    @org.apache.isis.applib.annotation.Property(optionality = Optionality.OPTIONAL)
    @Getter @Setter
    private LocalDate startDate;

    //region > selectStartDate (action)
    public LeaseTermForTurnoverRentManager selectStartDate(
            @Named("Start date") final LocalDate startDate) {
        setStartDate(startDate);
        return this;
    }

    public List<LocalDate> choices0SelectStartDate() {
        return leaseTermRepository.findStartDatesByPropertyAndType(property, LeaseItemType.SERVICE_CHARGE);
    }

    public LocalDate default0SelectStartDate() {
        return getStartDate();
    }
    //endregion


    //region > turnoverRents (derived collection)
    public List<LeaseTermForTurnoverRentLineItem> getTurnoverRents() {
        final List<LeaseTerm> terms = leaseTermRepository.findByPropertyAndTypeAndStartDate(getProperty(), LeaseItemType.TURNOVER_RENT, getStartDate());
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
    //endregion


    //region > download (action)
    public Blob download() {
        final String fileName = "TurnoverRentBulkUpdate-" + getProperty().getReference() + "@" + getStartDate() + ".xlsx";
        final List<LeaseTermForTurnoverRentLineItem> lineItems = getTurnoverRents();
        return excelService.toExcel(lineItems, LeaseTermForTurnoverRentLineItem.class, fileName);
    }

    //endregion

    //region > upload (action)

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

    //endregion

    //region > injected services

    @javax.inject.Inject
    private LeaseTermRepository leaseTermRepository;

    @javax.inject.Inject
    private ExcelService excelService;

    @javax.inject.Inject
    private LeaseTermForTurnoverRentService budgetAuditService;

    //endregion

}
