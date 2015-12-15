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

import org.apache.commons.lang3.ObjectUtils;
import org.apache.isis.applib.annotation.Programmatic;
import org.estatio.dom.lease.invoicing.InvoiceCalculationService;
import org.estatio.dom.valuetypes.LocalDateInterval;
import org.joda.time.LocalDate;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.InheritanceStrategy;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
public class LeaseTermForPercentage extends LeaseTerm {

    //region > percentage (property)
    private BigDecimal percentage;

    @Column(allowsNull = "false", scale = 2)
    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(final BigDecimal percentage) {
        this.percentage = percentage;
    }
    //endregion

    //region > originalValue (property)
    private BigDecimal originalValue;

    @Column(allowsNull = "true", scale = 2)
    public BigDecimal getOriginalValue() {
        return originalValue;
    }

    public void setOriginalValue(final BigDecimal originalValue) {
        this.originalValue = originalValue;
    }
    //endregion

    public LeaseTermForPercentage changeParameters(
            final BigDecimal newPercentage) {
        setPercentage(newPercentage);
        setStatus(LeaseTermStatus.NEW);
        doAlign();
        return this;
    }

    public BigDecimal default0ChangeParameters() {
        return getPercentage();
    }

    public String validateChangeParameters(
            final BigDecimal newPercentage){
        if (newPercentage.compareTo(BigDecimal.ZERO)<0 || newPercentage.compareTo(new BigDecimal(100))>0){
            return "Percentage should be between 0 and 100";
        }
        return null;
    }


    @Override
    public BigDecimal valueForDate(LocalDate dueDate) {
        return getOriginalValue().multiply(getPercentage())
                .divide(new BigDecimal(100), MathContext.DECIMAL64)
                .setScale(2, BigDecimal.ROUND_HALF_UP);
    }


    // //////////////////////////////////////

    @Override
    public BigDecimal getEffectiveValue() {
        return valueForDate(null);
    }

    // //////////////////////////////////////

    @Override
    protected void doAlign() {

        if (getStatus() != LeaseTermStatus.APPROVED) {
            // Collect all results
            BigDecimal newOriginalValue = BigDecimal.ZERO;
            List<LeaseItem> rentItems = getLeaseItem().getLease().findItemsOfType(LeaseItemType.RENT);
            List<InvoiceCalculationService.CalculationResult> calculationResults = new ArrayList<InvoiceCalculationService.CalculationResult>();
            for (LeaseItem rentItem : rentItems) {
                calculationResults.addAll(
                        rentItem.calculationResults(getInterval(), this.getEndDate().plusYears(1)));
            }
            List<LeaseItem> torItems = getLeaseItem().getLease().findItemsOfType(LeaseItemType.TURNOVER_RENT);
            for (LeaseItem torItem : torItems) {
                calculationResults.addAll(
                        torItem.calculationResults(getInterval(), this.getEndDate().plusYears(1)));
            }
            // TODO: do prorata when intervals don't match
            for (InvoiceCalculationService.CalculationResult result : calculationResults) {
                if (getInterval().contains(result.invoicingInterval().asLocalDateInterval())) {
                    newOriginalValue = newOriginalValue.add(result.value());
                }
            }
            if (ObjectUtils.compare(getOriginalValue(), newOriginalValue) != 0) {
                setOriginalValue(newOriginalValue);
            }
        }

    }

    @Override
    @Programmatic
    public void doInitialize() {
        LeaseTermForPercentage prev = (LeaseTermForPercentage) getPrevious();
        if (prev != null) {
            setPercentage(prev.getPercentage());
        }
        if(getEndDate() == null){
            setEndDate(LocalDateInterval.excluding(getStartDate(), nextStartDate()).endDate());
        }
    }

    // //////////////////////////////////////

    @Override
    public LeaseTerm approve() {
        super.approve();

        return this;
    }

    // //////////////////////////////////////

    @Override
    public void copyValuesTo(final LeaseTerm target) {
        LeaseTermForPercentage t = (LeaseTermForPercentage) target;
        super.copyValuesTo(t);
        t.setPercentage(getPercentage());
        t.setOriginalValue(getOriginalValue());
    }

}
