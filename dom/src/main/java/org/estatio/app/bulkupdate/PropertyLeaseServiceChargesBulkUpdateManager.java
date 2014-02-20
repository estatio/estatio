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
package org.estatio.app.bulkupdate;

import java.util.List;

import com.danhaywood.isis.domainservice.excel.applib.ExcelService;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

import org.estatio.app.EstatioViewModel;
import org.estatio.dom.asset.Property;
import org.estatio.dom.lease.LeaseTermForServiceCharge;
import org.estatio.dom.lease.LeaseTerms;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.value.Blob;

@Immutable
@Bookmarkable
public class PropertyLeaseServiceChargesBulkUpdateManager extends EstatioViewModel {

    
    @Override
    public String viewModelMemento() {
        return propertyServiceChargeBulkUpdateService.mementoFor(this);
    }

    @Override
    public void viewModelInit(String mementoStr) {
        propertyServiceChargeBulkUpdateService.initOf(mementoStr, this);
    }

    
    // //////////////////////////////////////
    // property (property)
    // //////////////////////////////////////

    private Property property;
    
    @Title(sequence = "1")
    @MemberOrder(sequence="1")
    public Property getProperty() {
        return property;
    }
    public void setProperty(Property property) {
        this.property = property;
    }

    @Named("Select")
    @MemberOrder(name="property", sequence="1")
    public PropertyLeaseServiceChargesBulkUpdateManager selectProperty(
            final Property property,
            @Named("Start date") final LocalDate startDate) {
        setProperty(property);
        setStartDate(startDate);
        return propertyServiceChargeBulkUpdateService.newManager(this);
    }
    public List<LocalDate> choices1SelectProperty(Property property) {
        return leaseTerms.findServiceChargeDatesByProperty(property);
    }


    // //////////////////////////////////////
    // startDate (property)
    // //////////////////////////////////////

    private LocalDate startDate;

    @Title(sequence = "2", prepend="@")
    @Optional
    @MemberOrder(sequence="2")
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }

    // //////////////////////////////////////

    
    @Named("Select")
    @MemberOrder(name="startDate", sequence="1")
    public PropertyLeaseServiceChargesBulkUpdateManager selectStartDate(
            @Named("Start date") final LocalDate startDate) {
        setStartDate(startDate);
        return propertyServiceChargeBulkUpdateService.newManager(this);
    }
    public List<LocalDate> choices0SelectStartDate() {
        return leaseTerms.findServiceChargeDatesByProperty(property);
    }
    public LocalDate default0SelectStartDate() {
        return getStartDate();
    }

    
    // //////////////////////////////////////
    // serviceCharges (collection)
    // //////////////////////////////////////

    @Render(Type.EAGERLY)
    public List<LeaseTermForServiceChargeBulkUpdateLineItem> getServiceCharges() { 
        final List<LeaseTermForServiceCharge> terms = leaseTerms.findServiceChargeByPropertyAndStartDate(getProperty(), getStartDate());
        return Lists.transform(terms, newLeaseTermForServiceChargeAuditBulkUpdate());
    }

    private Function<LeaseTermForServiceCharge, LeaseTermForServiceChargeBulkUpdateLineItem> newLeaseTermForServiceChargeAuditBulkUpdate() {
        return new Function<LeaseTermForServiceCharge, LeaseTermForServiceChargeBulkUpdateLineItem>(){
            @Override
            public LeaseTermForServiceChargeBulkUpdateLineItem apply(final LeaseTermForServiceCharge leaseTerm) {
                LeaseTermForServiceChargeBulkUpdateLineItem template = newTransientInstance(LeaseTermForServiceChargeBulkUpdateLineItem.class);
                template.modifyLeaseTerm(leaseTerm);
                return propertyServiceChargeBulkUpdateService.newLineItem(template);
            }};
    }

    // //////////////////////////////////////
    // download (action)
    // //////////////////////////////////////

    @Named("Download")
    @MemberOrder(name="serviceCharges", sequence="1")
    public Blob downloadForServiceChargeBulkUpdate() {
        final String fileName = "ServiceChargeBulkUpdate-" + getProperty().getReference() + "@" + getStartDate() + ".xlsx";
        final List<LeaseTermForServiceChargeBulkUpdateLineItem> viewModels = getServiceCharges();
        return excelService.toExcel(viewModels, LeaseTermForServiceChargeBulkUpdateLineItem.class, fileName);
    }

    // //////////////////////////////////////
    // upload (action)
    // //////////////////////////////////////

    @Named("Upload")
    @MemberOrder(name="serviceCharges", sequence="2")
    public PropertyLeaseServiceChargesBulkUpdateManager uploadForServiceChargeBulkUpdate(final @Named("Excel spreadsheet") Blob spreadsheet) {
        List<LeaseTermForServiceChargeBulkUpdateLineItem> lineItems = 
                excelService.fromExcel(spreadsheet, LeaseTermForServiceChargeBulkUpdateLineItem.class);
        for (LeaseTermForServiceChargeBulkUpdateLineItem ltfscbu : lineItems) {
            final LeaseTermForServiceCharge leaseTerm = ltfscbu.getLeaseTerm();
            leaseTerm.setAuditedValue(ltfscbu.getAuditedValue());
            leaseTerm.setBudgetedValue(ltfscbu.getBudgetedValue());

            final LeaseTermForServiceCharge nextLeaseTerm = (LeaseTermForServiceCharge) leaseTerm.getNext();
            final LeaseTermForServiceCharge nextLeaseTermUploaded = ltfscbu.getNextLeaseTerm();
            if(nextLeaseTerm != null && nextLeaseTerm == nextLeaseTermUploaded) {
                nextLeaseTerm.setBudgetedValue(ltfscbu.getNextBudgetedValue());
            }
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
    private PropertyLeaseServiceChargesBulkUpdateService propertyServiceChargeBulkUpdateService;

}
