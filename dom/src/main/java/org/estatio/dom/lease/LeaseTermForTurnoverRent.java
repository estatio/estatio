/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
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

import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.InheritanceStrategy;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@javax.jdo.annotations.Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
public class LeaseTermForTurnoverRent extends LeaseTerm {

    private String turnoverRentRule;

    public String getTurnoverRentRule() {
        return turnoverRentRule;
    }

    public void setTurnoverRentRule(final String turnoverRentRule) {
        this.turnoverRentRule = turnoverRentRule;
    }

    public String validateTurnoverRentRule(final String rule) {
        if (rule == null || rule.trim().length() == 0)
            return null;
        TurnoverRentRuleHelper helper = new TurnoverRentRuleHelper(rule);
        return helper.isValid() ? null : "This is not a valid rule";
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(scale = 2)
    private BigDecimal budgetedTurnover;

    public BigDecimal getBudgetedTurnover() {
        return budgetedTurnover;
    }

    public void setBudgetedTurnover(final BigDecimal budgetedTurnover) {
        this.budgetedTurnover = budgetedTurnover;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(scale = 2)
    private BigDecimal auditedTurnover;

    public BigDecimal getAuditedTurnover() {
        return auditedTurnover;
    }

    public void setAuditedTurnover(final BigDecimal auditedTurnover) {
        this.auditedTurnover = auditedTurnover;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(scale = 2)
    private BigDecimal contractualRent;

    public BigDecimal getContractualRent() {
        return contractualRent;
    }

    public void setContractualRent(final BigDecimal contractualRent) {
        this.contractualRent = contractualRent;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(scale = 2)
    private BigDecimal turnoverRentValue;

    public BigDecimal getTurnoverRentValue() {
        return turnoverRentValue;
    }

    public void setTurnoverRentValue(final BigDecimal turnoverRentValue) {
        this.turnoverRentValue = turnoverRentValue;
    }

    // //////////////////////////////////////

    @Override
    public BigDecimal getApprovedValue() {
        return isLocked()? getTurnoverRentValue(): null;
    }

    // //////////////////////////////////////

    @Override
    public BigDecimal getTrialValue() {
        TurnoverRentRuleHelper helper = new TurnoverRentRuleHelper(getTurnoverRentRule());
        BigDecimal calculatedTurnoverRent = helper.calculateRent(getAuditedTurnover());
        if (calculatedTurnoverRent.compareTo(contractualRent) > 0)
            return calculatedTurnoverRent.subtract(contractualRent);
        return BigDecimal.ZERO;
    }

    // //////////////////////////////////////

    @Override
    protected void update() {
        LeaseItem rentItem = getLeaseItem().getLease().findFirstItemOfType(LeaseItemType.RENT);
        // TODO: Should not be hardcoded searching for rent and should return a
        // collection. Also move the value of rent to a different field
        if (rentItem != null) {
            BigDecimal contractualRent = rentItem.valueForPeriod(getLeaseItem().getInvoicingFrequency(), getStartDate(), getStartDate().plusYears(2));
            setContractualRent(contractualRent);
        }
    }

    @Override
    protected void initialize() {
        super.initialize();
        LeaseTermForTurnoverRent prev = (LeaseTermForTurnoverRent) getPrevious();
        if (prev != null) {
            setTurnoverRentRule(prev.getTurnoverRentRule());
        }
    }
    
    // //////////////////////////////////////

    @Override
    public LeaseTerm lock() {
        super.lock();

        // guard against invalid updates when called as bulk action
        if (isLocked()) {
            return this;
        } 

        setTurnoverRentValue(getTrialValue());
        return this;
    }

}
