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

import javax.jdo.annotations.InheritanceStrategy;

import org.apache.isis.applib.annotation.Mandatory;
import org.apache.isis.applib.annotation.Optional;

import org.estatio.dom.JdoColumnLength;

@javax.jdo.annotations.PersistenceCapable // identityType=IdentityType.DATASTORE inherited from superclass
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.SUPERCLASS_TABLE)
//no @DatastoreIdentity nor @Version, since inherited from supertype
public class LeaseTermForTurnoverRent extends LeaseTerm {

    // //////////////////////////////////////

    private String turnoverRentRule;

    @javax.jdo.annotations.Column(allowsNull="true", length=JdoColumnLength.LeaseTermForTurnoverRent.RENT_RULE)
    @Mandatory
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

    private BigDecimal budgetedTurnover;

    @javax.jdo.annotations.Column(scale = 2, allowsNull="true")
    @Optional
    public BigDecimal getBudgetedTurnover() {
        return budgetedTurnover;
    }

    public void setBudgetedTurnover(final BigDecimal budgetedTurnover) {
        this.budgetedTurnover = budgetedTurnover;
    }

    // //////////////////////////////////////

    private BigDecimal auditedTurnover;

    @javax.jdo.annotations.Column(scale = 2, allowsNull="true")
    @Optional
    public BigDecimal getAuditedTurnover() {
        return auditedTurnover;
    }

    public void setAuditedTurnover(final BigDecimal auditedTurnover) {
        this.auditedTurnover = auditedTurnover;
    }

    // //////////////////////////////////////

    private BigDecimal contractualRent;

    @javax.jdo.annotations.Column(scale = 2, allowsNull="true")
    @Optional
    public BigDecimal getContractualRent() {
        return contractualRent;
    }

    public void setContractualRent(final BigDecimal contractualRent) {
        this.contractualRent = contractualRent;
    }

    // //////////////////////////////////////

    private BigDecimal turnoverRentValue;

    @javax.jdo.annotations.Column(scale = 2, allowsNull="true")
    @Optional
    public BigDecimal getTurnoverRentValue() {
        return turnoverRentValue;
    }

    public void setTurnoverRentValue(final BigDecimal turnoverRentValue) {
        this.turnoverRentValue = turnoverRentValue;
    }

    // //////////////////////////////////////

    @Override
    public BigDecimal getApprovedValue() {
        return getStatus().isApproved()? getTurnoverRentValue(): null;
    }

    // //////////////////////////////////////

    @Override
    public BigDecimal getTrialValue() {
        TurnoverRentRuleHelper helper = new TurnoverRentRuleHelper(getTurnoverRentRule());
        BigDecimal calculatedTurnoverRent = helper.calculateRent(getAuditedTurnover());
        if (getContractualRent() != null && 
            calculatedTurnoverRent.compareTo(getContractualRent()) > 0) {
            return calculatedTurnoverRent.subtract(getContractualRent());
        }
        return BigDecimal.ZERO;
    }

    // //////////////////////////////////////

    @Override
    protected void update() {
        LeaseItem rentItem = getLeaseItem().getLease().findFirstItemOfType(LeaseItemType.RENT);
        // TODO: Should not be hardcoded searching for rent and should return a
        // collection. Also move the value of rent to a different field
        if (rentItem != null) {
            final BigDecimal contractualRent = rentItem.valueForPeriod(
                    getLeaseItem().getInvoicingFrequency(), 
                    getStartDate(), 
                    getStartDate().plusYears(2));
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
    public LeaseTerm approve() {
        super.approve();

        if (!getStatus().isApproved()) {
            // need this guard, cos may be called as bulk action
            setTurnoverRentValue(getTrialValue());
        }

        return this;
    }

}
