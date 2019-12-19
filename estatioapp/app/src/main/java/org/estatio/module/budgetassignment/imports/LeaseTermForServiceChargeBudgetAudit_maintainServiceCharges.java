package org.estatio.module.budgetassignment.imports;

import org.apache.isis.applib.annotation.*;
import org.estatio.module.asset.dom.Property;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.lease.dom.LeaseItemType;
import org.joda.time.LocalDate;

import javax.inject.Inject;
import java.util.List;


/* TODO: check if this mixin works correctly */
@Mixin(method = "act")
public class LeaseTermForServiceChargeBudgetAudit_maintainServiceCharges {

    private final Property property;

    public LeaseTermForServiceChargeBudgetAudit_maintainServiceCharges(Property property) {
        this.property = property;
    }

    @ActionLayout(contributed = Contributed.AS_ACTION)
    public LeaseTermForServiceChargeBudgetAuditManager act(
            final List<LeaseItemType> leaseItemTypes,
            final List<LeaseAgreementRoleTypeEnum> invoicedBy,
            final LocalDate startDate,
            final LocalDate endDate
    ) {
        return leaseTermForServiceChargeBudgetAuditService.maintainServiceCharges(this.property, leaseItemTypes, invoicedBy, startDate, endDate);
    }

    public List<LeaseItemType> choices0Act(){
        return leaseTermForServiceChargeBudgetAuditService.choices1MaintainServiceCharges();
    }

    public List<LeaseItemType> default0Act(){
        return leaseTermForServiceChargeBudgetAuditService.default1MaintainServiceCharges();

    }

    public List<LeaseAgreementRoleTypeEnum> choices1Act(){
        return leaseTermForServiceChargeBudgetAuditService.choices2MaintainServiceCharges();
    }

    public List<LeaseAgreementRoleTypeEnum> default1Act(){
        return leaseTermForServiceChargeBudgetAuditService.default2MaintainServiceCharges();
    }

    public List<LocalDate> choices2Act(final List<LeaseItemType> leaseItemTypes, final List<LeaseAgreementRoleTypeEnum> invoicedBy, final LocalDate startDate, final LocalDate endDate) {
        return leaseTermForServiceChargeBudgetAuditService.choices3MaintainServiceCharges(this.property, leaseItemTypes, invoicedBy, startDate, endDate);
    }

    public List<LocalDate> choices3Act(final List<LeaseItemType> leaseItemTypes, final List<LeaseAgreementRoleTypeEnum> invoicedBy, final LocalDate startDate, final LocalDate endDate) {
        return leaseTermForServiceChargeBudgetAuditService.choices4MaintainServiceCharges(this.property, leaseItemTypes, invoicedBy, startDate, endDate);
    }

    public String validateAct(
            final List<LeaseItemType> leaseItemTypes,
            final List<LeaseAgreementRoleTypeEnum> invoicedBy,
            final LocalDate startDate,
            final LocalDate endDate) {
        return leaseTermForServiceChargeBudgetAuditService.validateMaintainServiceCharges(this.property, leaseItemTypes, invoicedBy, startDate, endDate);
    }

    @Inject
    LeaseTermForServiceChargeBudgetAuditService leaseTermForServiceChargeBudgetAuditService;

}
