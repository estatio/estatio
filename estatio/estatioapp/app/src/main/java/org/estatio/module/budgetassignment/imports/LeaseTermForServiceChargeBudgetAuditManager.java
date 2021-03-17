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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

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
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;

import org.incode.module.base.dom.utils.TitleBuilder;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.budgetassignment.contributions.LeaseTermForServiceCharge_controlledByBudget;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseTermForServiceCharge;
import org.estatio.module.lease.dom.LeaseTermRepository;

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

    public LeaseTermForServiceChargeBudgetAuditManager(Property property, final List<LeaseItemType> leaseItemTypes, final List<LeaseAgreementRoleTypeEnum> invoicedBy, LocalDate startDate, LocalDate endDate) {
        this.property = property;
        this.startDate = startDate;
        this.endDate = endDate;
        this.leaseItemTypes = typesToString(leaseItemTypes);
        this.invoicedBy = invoicedByToString(invoicedBy);
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

    @Getter @Setter
    @org.apache.isis.applib.annotation.Property(optionality = Optionality.OPTIONAL)
    private LocalDate startDate;

    @Getter @Setter
    @org.apache.isis.applib.annotation.Property(optionality = Optionality.OPTIONAL)
    private LocalDate endDate;

    @Getter @Setter
    public String leaseItemTypes;

    @Getter @Setter
    public String invoicedBy;

    //region > serviceCharges (derived collection)

    public List<LeaseTermForServiceChargeBudgetAuditLineItem> getServiceCharges() {
        final List<LeaseTermForServiceCharge> terms = leaseTermRepository.findServiceChargeByPropertyAndItemTypeWithStartDateInPeriod(getProperty(), typesFromString(getLeaseItemTypes()), invoicedByFromString(getInvoicedBy()), getStartDate(), getEndDate())
                .stream()
//                .filter(t->!factoryService.mixin(LeaseTermForServiceCharge_controlledByBudget.class, t).$$()) //ECP-1023: filters out terms controlled by a budget
                .collect(Collectors.toList());
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
        final String fileName = "ServiceChargeUpdate-" + getProperty().getReference() + "@" + getStartDate() + "-" + getEndDate() + ".xlsx";
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
            if (!factoryService.mixin(LeaseTermForServiceCharge_controlledByBudget.class, leaseTerm).$$()) {
                leaseTerm.setBudgetedValue(lineItem.getBudgetedValue());
            }
            final LeaseTermForServiceCharge nextLeaseTerm = (LeaseTermForServiceCharge) leaseTerm.getNext();
            final LeaseTermForServiceCharge nextLeaseTermUploaded = lineItem.getNextLeaseTerm();
            if (nextLeaseTerm != null && nextLeaseTerm == nextLeaseTermUploaded) {
                nextLeaseTerm.setBudgetedValue(lineItem.getNextBudgetedValue());
            }
        }
        return this;
    }
    //endregion

    String typesToString(final List<LeaseItemType> leaseItemTypes){
        String result = new String();
        for (int i = 0; i < leaseItemTypes.size(); i++){
            result = result.concat(leaseItemTypes.get(i).name());
            if (i != leaseItemTypes.size()-1){
                result = result.concat(", ");
            }
        }
        return result;
    }

    List<LeaseItemType> typesFromString(final String stringOfTypes){
        return Arrays.stream(stringOfTypes.split(",\\s+")).map(LeaseItemType::valueOf).collect(Collectors.toList());
    }

    String invoicedByToString(final List<LeaseAgreementRoleTypeEnum> leaseItemTypes){
        String result = new String();
        for (int i = 0; i < leaseItemTypes.size(); i++){
            result = result.concat(leaseItemTypes.get(i).name());
            if (i != leaseItemTypes.size()-1){
                result = result.concat(", ");
            }
        }
        return result;
    }

    List<LeaseAgreementRoleTypeEnum> invoicedByFromString(final String stringOfTypes){
        return Arrays.stream(stringOfTypes.split(",\\s+")).map(LeaseAgreementRoleTypeEnum::valueOf).collect(Collectors.toList());
    }

    //region > injected services
    @Inject
    private LeaseTermRepository leaseTermRepository;

    @Inject
    private ExcelService excelService;

    @Inject FactoryService factoryService;
    //endregion

}
