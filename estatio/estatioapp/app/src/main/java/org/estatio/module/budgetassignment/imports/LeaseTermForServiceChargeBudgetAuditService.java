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
package org.estatio.module.budgetassignment.imports;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.NotContributed;
import org.apache.isis.applib.annotation.NotContributed.As;
import org.apache.isis.applib.annotation.NotInServiceMenu;
import org.apache.isis.applib.services.bookmark.BookmarkService;

import org.isisaddons.module.excel.dom.ExcelService;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.base.dom.UdoDomainService;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseTermRepository;

@DomainService(
        menuOrder = "00",
        objectType = "org.estatio.app.services.lease.budgetaudit.LeaseTermForServiceChargeBudgetAuditService"
)
@Immutable
public class LeaseTermForServiceChargeBudgetAuditService extends UdoDomainService<LeaseTermForServiceChargeBudgetAuditService> {

    public LeaseTermForServiceChargeBudgetAuditService() {
        super(LeaseTermForServiceChargeBudgetAuditService.class);
    }

    // //////////////////////////////////////

    @PostConstruct
    public void init(final Map<String, String> properties) {
        super.init(properties);
        if (bookmarkService == null) {
            throw new IllegalStateException("Require BookmarkService to be configured");
        }
        if (excelService == null) {
            throw new IllegalStateException("Require ExcelService to be configured");
        }
    }

    // //////////////////////////////////////

    @NotContributed(As.ASSOCIATION)
    // ie *is* contributed as action
    @NotInServiceMenu
    public LeaseTermForServiceChargeBudgetAuditManager maintainServiceCharges(
            final Property property,
            final List<LeaseItemType> leaseItemTypes,
            final List<LeaseAgreementRoleTypeEnum> invoicedBy,
            final LocalDate startDate,
            final LocalDate endDate
    ) {
        return new LeaseTermForServiceChargeBudgetAuditManager(property, leaseItemTypes, invoicedBy, startDate, endDate);
    }

    public List<LeaseItemType> choices1MaintainServiceCharges(){
        return LeaseItemType.typesForLeaseTermForServiceCharge();
    }

    public List<LeaseItemType> default1MaintainServiceCharges(){
        return LeaseItemType.typesForLeaseTermForServiceCharge();
    }

    public List<LeaseAgreementRoleTypeEnum> choices2MaintainServiceCharges(){
        return LeaseItemType.invoicedByForLeaseTermForServiceCharge();
    }

    public List<LeaseAgreementRoleTypeEnum> default2MaintainServiceCharges(){
        return Lists.newArrayList(LeaseAgreementRoleTypeEnum.LANDLORD);
    }

    public List<LocalDate> choices3MaintainServiceCharges(final Property property, final List<LeaseItemType> leaseItemTypes, final List<LeaseAgreementRoleTypeEnum> invoicedBy, final LocalDate startDate, final LocalDate endDate) {
        return leaseTermRepository.findServiceChargeDatesByPropertyAndLeaseItemTypeAndInvoicedBy(property, leaseItemTypes, invoicedBy);
    }

    public List<LocalDate> choices4MaintainServiceCharges(final Property property, final List<LeaseItemType> leaseItemTypes, final List<LeaseAgreementRoleTypeEnum> invoicedBy, final LocalDate startDate, final LocalDate endDate) {
        return leaseTermRepository.findServiceChargeDatesByPropertyAndLeaseItemTypeAndInvoicedBy(property, leaseItemTypes, invoicedBy);
    }

    public String validateMaintainServiceCharges(
            final Property property,
            final List<LeaseItemType> leaseItemTypes,
            final List<LeaseAgreementRoleTypeEnum> invoicedBy,
            final LocalDate startDate,
            final LocalDate endDate) {
        if (startDate.isAfter(endDate)) return "The end date should on or after the start date";
        return null;
    }

    // //////////////////////////////////////

    @javax.inject.Inject
    private ExcelService excelService;

    @javax.inject.Inject
    private BookmarkService bookmarkService;

    @javax.inject.Inject
    private LeaseTermRepository leaseTermRepository;

}
