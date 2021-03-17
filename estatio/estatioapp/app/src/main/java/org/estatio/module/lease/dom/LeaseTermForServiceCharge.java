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
package org.estatio.module.lease.dom;

import java.math.BigDecimal;

import javax.jdo.annotations.InheritanceStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.IsisApplibModule;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;

import org.incode.module.base.dom.utils.MathUtils;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        schema = "dbo"     // Isis' ObjectSpecId inferred from @Discriminator
)
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@javax.jdo.annotations.Discriminator("org.estatio.dom.lease.LeaseTermForServiceCharge")
@DomainObject(editing = Editing.DISABLED)
public class LeaseTermForServiceCharge extends LeaseTerm {

    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true")
    @Property(optionality = Optionality.OPTIONAL)
    @Getter @Setter
    private BigDecimal budgetedValue;

    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true")
    @Property(optionality = Optionality.OPTIONAL)
    @Getter @Setter
    private BigDecimal auditedValue;

    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true")
    @Property(optionality = Optionality.OPTIONAL)
    @Getter @Setter
    private BigDecimal manualServiceChargeValue;

    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true")
    @Property(optionality = Optionality.OPTIONAL)
    @Getter @Setter
    private BigDecimal shortfall;

    public static class changeValuesEvent extends IsisApplibModule.ActionDomainEvent<LeaseTermForServiceCharge> {
        private static final long serialVersionUID = 1L;
    }

    public static class changeManualValueEvent extends IsisApplibModule.ActionDomainEvent<LeaseTermForServiceCharge> {
        private static final long serialVersionUID = 1L;
    }

    @Action(domainEvent = changeValuesEvent.class)
    public LeaseTermForServiceCharge changeValues(
            final @Parameter(optionality = Optionality.OPTIONAL) BigDecimal budgetedValue,
            final @Parameter(optionality = Optionality.OPTIONAL) BigDecimal auditedValue) {
        setBudgetedValue(budgetedValue);
        setAuditedValue(auditedValue);
        return this;
    }

    public boolean hideChangeValues(){
        if (getLeaseItem().getLease().getStatus()==LeaseStatus.PREVIEW) return true;
        return false;
    }

    public BigDecimal default0ChangeValues() {
        return getBudgetedValue();
    }

    public BigDecimal default1ChangeValues() {
        return getAuditedValue();
    }

    @Action(domainEvent = changeManualValueEvent.class)
    public LeaseTermForServiceCharge changeManualValue(final @Parameter(optionality = Optionality.OPTIONAL) BigDecimal manualValue){
        setManualServiceChargeValue(manualValue);
        return this;
    }

    public boolean hideChangeManualValue(){
        if (getLeaseItem().getLease().getStatus()==LeaseStatus.PREVIEW) return true;
        return false;
    }

    public BigDecimal default0ChangeManualValue() {
        return getManualServiceChargeValue();
    }

    public LeaseTermForServiceCharge changeShortfall(final @Parameter(optionality = Optionality.OPTIONAL) BigDecimal shortfall){
        setShortfall(shortfall);
        return this;
    }

    public boolean hideChangeShortfall(){
        if (getLeaseItem().getLease().getStatus()==LeaseStatus.PREVIEW) return true;
        return false;
    }

    public BigDecimal default0ChangeShortfall() {
        return getShortfall();
    }

    // //////////////////////////////////////

    @Override
    public BigDecimal getEffectiveValue() {
        return MathUtils.firstNonZero(getManualServiceChargeValue(), getAuditedValue(), getBudgetedValue());
    }

    // //////////////////////////////////////

    @Override
    @Programmatic
    public BigDecimal valueForDate(final LocalDate dueDate) {
        // TODO: we might need an effective date on the Service Charge too
        LocalDate endDate = getInterval().endDateExcluding();
        if (endDate != null) {
            LocalDate effectiveDate = endDate;
            if (getEndDate() != null && effectiveDate.compareTo(dueDate) <= 0) {
                return getEffectiveValue();
            }
        }
        return MathUtils.firstNonZero(getManualServiceChargeValue(), getBudgetedValue());
    }

    // //////////////////////////////////////

    @Override
    protected void doInitialize() {
        LeaseTermForServiceCharge previousTerm = (LeaseTermForServiceCharge) getPrevious();
        if (previousTerm != null) {
            this.setBudgetedValue(
                    MathUtils.firstNonZero(
                            previousTerm.getAuditedValue(),
                            previousTerm.getBudgetedValue()));
        }
    }

    // //////////////////////////////////////

    @Override
    protected void doAlign() {
        if (getPrevious() != null && MathUtils.isZeroOrNull(getBudgetedValue())) {
            if (MathUtils.isNotZeroOrNull(getPrevious().getEffectiveValue())) {
                setBudgetedValue(getPrevious().getEffectiveValue());
            }
        }
    }

    // //////////////////////////////////////

    @Override
    @Programmatic
    public void copyValuesTo(final LeaseTerm target) {
        LeaseTermForServiceCharge t = (LeaseTermForServiceCharge) target;
        super.copyValuesTo(t);
        t.setBudgetedValue(getBudgetedValue());
        t.setAuditedValue(getAuditedValue());
        t.setManualServiceChargeValue(getManualServiceChargeValue());
        t.setShortfall(getShortfall());
    }

    @Override
    @Programmatic
    public void negateAmountsAndApplyPercentage(final BigDecimal discountPercentage){
        if (getBudgetedValue()!=null) {
            setBudgetedValue(applyPercentage(getBudgetedValue(), discountPercentage).negate());
        }
        if (getAuditedValue()!=null){
            setAuditedValue(applyPercentage(getAuditedValue(), discountPercentage).negate());
        }
        if (getManualServiceChargeValue()!=null){
            setManualServiceChargeValue(applyPercentage(getManualServiceChargeValue(), discountPercentage).negate());
        }
    }

}
