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
package org.estatio.dom.lease;

import java.math.BigDecimal;

import javax.jdo.annotations.InheritanceStrategy;

import org.apache.commons.lang3.ObjectUtils;

import org.apache.isis.applib.annotation.Optional;

import org.estatio.dom.JdoColumnLength;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
public class LeaseTermForTurnoverRent extends LeaseTerm {

    private String turnoverRentRule;

    @javax.jdo.annotations.Column(allowsNull = "true", length = JdoColumnLength.LeaseTermForTurnoverRent.RENT_RULE)
    @Optional
    public String getTurnoverRentRule() {
        return turnoverRentRule;
    }

    public void setTurnoverRentRule(final String turnoverRentRule) {
        this.turnoverRentRule = turnoverRentRule;
    }

    public String validateTurnoverRentRule(final String rule) {
        if (rule == null || rule.trim().length() == 0) {
            return "Rule cannot be empty";
        }
        TurnoverRentRuleHelper helper = new TurnoverRentRuleHelper(rule);
        return helper.isValid() ? null : "'" + rule + "' is not a valid rule";
    }

    // //////////////////////////////////////

    private BigDecimal auditedTurnover;

    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true")
    @Optional
    public BigDecimal getAuditedTurnover() {
        return auditedTurnover;
    }

    public void setAuditedTurnover(final BigDecimal auditedTurnover) {
        this.auditedTurnover = auditedTurnover;
    }

    // //////////////////////////////////////

    private BigDecimal contractualRent;

    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true")
    @Optional
    public BigDecimal getContractualRent() {
        return contractualRent;
    }

    public void setContractualRent(final BigDecimal contractualRent) {
        this.contractualRent = contractualRent;
    }

    // //////////////////////////////////////

    private BigDecimal auditedTurnoverRent;

    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true")
    @Optional
    public BigDecimal getAuditedTurnoverRent() {
        return auditedTurnoverRent;
    }

    public void setAuditedTurnoverRent(final BigDecimal auditedTurnoverRent) {
        this.auditedTurnoverRent = auditedTurnoverRent;
    }

    // //////////////////////////////////////

    private BigDecimal budgetedTurnoverRent;

    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true")
    @Optional
    public BigDecimal getBudgetedTurnoverRent() {
        return budgetedTurnoverRent;
    }

    public void setBudgetedTurnoverRent(final BigDecimal budgetedTurnoverRent) {
        this.budgetedTurnoverRent = budgetedTurnoverRent;
    }

    // //////////////////////////////////////

    @Override
    public BigDecimal getEffectiveValue() {
        return ObjectUtils.firstNonNull(getBudgetedTurnoverRent(),
                getAuditedTurnoverRent(), BigDecimal.ZERO);
    }

    @Override
    public LeaseTermValueType valueType() {
        return getAuditedTurnoverRent() != null ? LeaseTermValueType.FIXED : LeaseTermValueType.ANNUAL;
    }

    // //////////////////////////////////////

    @Override
    protected void doAlign() {
        LeaseItem rentItem = getLeaseItem().getLease().findFirstItemOfType(LeaseItemType.RENT);
        // TODO: Should not be hardcoded searching for rent and should return a
        // collection. Also move the value of rent to a different field
        if (rentItem != null) {
            BigDecimal newContractualRent = rentItem.valueForPeriod(
                    getLeaseItem().getInvoicingFrequency(),
                    getStartDate(),
                    getStartDate().plusYears(2));
            setContractualRent(newContractualRent);
            TurnoverRentRuleHelper helper = new TurnoverRentRuleHelper(getTurnoverRentRule());
                BigDecimal newAuditedTurnoverRent = helper.calculateRent(getAuditedTurnover());
                if (ObjectUtils.compare(newAuditedTurnoverRent, getContractualRent()) > 0) {
                    setAuditedTurnoverRent(newAuditedTurnoverRent.subtract(getContractualRent()));
                }
        }
    }

    @Override
    public void doInitialize() {
        LeaseTermForTurnoverRent prev = (LeaseTermForTurnoverRent) getPrevious();
        if (prev != null) {
            setTurnoverRentRule(prev.getTurnoverRentRule());
        }
    }

    // //////////////////////////////////////

    @Override
    public LeaseTerm approve() {
        super.approve();

        if (!getStatus().isApproved()) {
            // need this guard, cos may be called as bulk action
            setAuditedTurnoverRent(getEffectiveValue());
        }

        return this;
    }

    // //////////////////////////////////////

    @Override
    public void copyValuesTo(final LeaseTerm target) {
        LeaseTermForTurnoverRent t = (LeaseTermForTurnoverRent) target;
        super.copyValuesTo(t);
        t.setTurnoverRentRule(getTurnoverRentRule());
        t.setAuditedTurnoverRent(getAuditedTurnoverRent());
        t.setAuditedTurnover(getAuditedTurnover());
        t.setContractualRent(getContractualRent());
    }

}
