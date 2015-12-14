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

import com.google.common.base.Strings;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.lease.invoicing.InvoiceCalculationService.CalculationResult;
import org.estatio.dom.valuetypes.LocalDateInterval;
import org.joda.time.LocalDate;

import javax.jdo.annotations.InheritanceStrategy;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
public class LeaseTermForTurnoverRent extends LeaseTerm {

    private String turnoverRentRule;

    @javax.jdo.annotations.Column(allowsNull = "true", length = JdoColumnLength.LeaseTermForTurnoverRent.RENT_RULE)
    @Optional
    @Disabled
    public String getTurnoverRentRule() {
        return turnoverRentRule;
    }

    public void setTurnoverRentRule(final String turnoverRentRule) {
        this.turnoverRentRule = turnoverRentRule;
    }

    public String validateTurnoverRentRule(final String turnoverRentrule) {
        if (Strings.isNullOrEmpty(turnoverRentrule)) {
            TurnoverRentRuleHelper helper = new TurnoverRentRuleHelper(turnoverRentrule);
            if (!helper.isValid()) {
                return "'" + turnoverRentrule + "' is not a valid rule";
            }
        }
        return null;
    }

    // //////////////////////////////////////

    private BigDecimal auditedTurnover;

    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true")
    @Optional
    @Disabled
    public BigDecimal getAuditedTurnover() {
        return auditedTurnover;
    }

    public void setAuditedTurnover(final BigDecimal auditedTurnover) {
        this.auditedTurnover = auditedTurnover;
    }

    public LeaseTermForTurnoverRent changeParameters(
            final @Named("Turnover rent rule") @Optional String newTurnoverRentRule,
            final @Named("Total budgeted rent") @Optional BigDecimal newTotalBudgetedRent,
            final @Named("Audited Turnover") @Optional BigDecimal newAuditedTurnover) {
        setTurnoverRentRule(newTurnoverRentRule);
        setTotalBudgetedRent(newTotalBudgetedRent);
        setAuditedTurnover(newAuditedTurnover);
        setStatus(LeaseTermStatus.NEW);
        doAlign();
        return this;
    }

    public String default0ChangeParameters() {
        return getTurnoverRentRule();
    }

    public BigDecimal default1ChangeParameters() {
        return getTotalBudgetedRent();
    }

    public BigDecimal default2ChangeParameters() {
        return getAuditedTurnover();
    }

    // //////////////////////////////////////

    private BigDecimal contractualRent;

    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true")
    @Optional
    @Disabled
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
    @Disabled
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
    @Disabled
    public BigDecimal getBudgetedTurnoverRent() {
        return budgetedTurnoverRent;
    }

    public void setBudgetedTurnoverRent(final BigDecimal budgetedTurnoverRent) {
        this.budgetedTurnoverRent = budgetedTurnoverRent;
    }

    // //////////////////////////////////////

    private BigDecimal totalBudgetedRent;

    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true")
    @Optional
    public BigDecimal getTotalBudgetedRent() {
        return totalBudgetedRent;
    }

    public void setTotalBudgetedRent(final BigDecimal totalBudgetedRent) {
        this.totalBudgetedRent = totalBudgetedRent;
    }

    // //////////////////////////////////////

    @Override
    public BigDecimal valueForDate(LocalDate dueDate) {
        return ObjectUtils.firstNonNull(
                getAuditedTurnoverRent(),
                getBudgetedTurnoverRent(),
                BigDecimal.ZERO);
    }

    @Override
    public BigDecimal getEffectiveValue() {
        return valueForDate(null);
    }

    @Override
    public LeaseTermValueType valueType() {
        return getAuditedTurnoverRent() != null ? LeaseTermValueType.FIXED : LeaseTermValueType.ANNUAL;
    }

    // //////////////////////////////////////

    @Override
    protected void doAlign() {

        if (getStatus() != LeaseTermStatus.APPROVED) {
            // Collect all results
            BigDecimal newContractualRent = BigDecimal.ZERO;
            List<LeaseItem> rentItems = getLeaseItem().getLease().findItemsOfType(LeaseItemType.RENT);
            List<CalculationResult> calculationResults = new ArrayList<CalculationResult>();
            for (LeaseItem rentItem : rentItems) {
                calculationResults.addAll(
                        rentItem.calculationResults(getInterval(), this.getEndDate().plusYears(1)));
            }
            // TODO: do prorata when intervals don't match
            for (CalculationResult result : calculationResults) {
                if (getInterval().contains(result.invoicingInterval().asLocalDateInterval())) {
                    newContractualRent = newContractualRent.add(result.value());
                }
            }
            if (ObjectUtils.compare(getContractualRent(), newContractualRent) != 0) {
                setContractualRent(newContractualRent);
            }

            // Budgeted Turnover Rent
            if (getTotalBudgetedRent() != null) {
                BigDecimal newBudgetedTurnoverRent = getTotalBudgetedRent().subtract(getContractualRent());
                if (newBudgetedTurnoverRent.compareTo(BigDecimal.ZERO) < 0) {
                    newBudgetedTurnoverRent = BigDecimal.ZERO;
                }
                if (ObjectUtils.compare(getBudgetedTurnoverRent(), newBudgetedTurnoverRent) != 0) {
                    setBudgetedTurnoverRent(newBudgetedTurnoverRent);
                }
            }

            // Audited Turnover Rent
            if (getTurnoverRentRule() != null) {
                // Ignore the rule when empty
                TurnoverRentRuleHelper helper = new TurnoverRentRuleHelper(getTurnoverRentRule());
                BigDecimal newAuditedTurnoverRent = helper.calculateRent(getAuditedTurnover()).subtract(getContractualRent());
                if (ObjectUtils.compare(newAuditedTurnoverRent, BigDecimal.ZERO) < 0) {
                    newAuditedTurnoverRent = BigDecimal.ZERO;
                }
                if (ObjectUtils.compare(getAuditedTurnoverRent(), newAuditedTurnoverRent) != 0) {
                    setAuditedTurnoverRent(newAuditedTurnoverRent);
                }
            }
        }
    }

    @Override
    @Programmatic
    public void doInitialize() {
        LeaseTermForTurnoverRent prev = (LeaseTermForTurnoverRent) getPrevious();
        if (prev != null) {
            setTurnoverRentRule(prev.getTurnoverRentRule());
        }
        if(getEndDate() == null){
            setEndDate(LocalDateInterval.excluding(getStartDate(),nextStartDate()).endDate());
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
