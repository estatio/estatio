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

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Programmatic;
import org.estatio.dom.JdoColumnScale;
import org.joda.time.LocalDate;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.InheritanceStrategy;
import java.math.BigDecimal;
import java.util.List;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
public class LeaseTermForDeposit extends LeaseTerm {

    @Column(allowsNull = "true", scale = 2)
    @Getter @Setter
    private BigDecimal excludedAmount;


    @Column(allowsNull = "false")
    @Getter @Setter
    private DepositType depositType;


    @Getter @Setter
    @Column(allowsNull = "true", scale = JdoColumnScale.MONEY)
    private BigDecimal calculatedDepositValue;


    @Getter @Setter
    @Column(allowsNull = "true", scale = JdoColumnScale.MONEY)
    private BigDecimal manualDepositValue;


    public LeaseTermForDeposit terminate(final LocalDate endDate){
        setEndDate(endDate);
        return this;
    }

    public LocalDate default0Terminate(){
        return LocalDate.now();
    }

    public LeaseTermForDeposit changeParameters(
            final DepositType depositType,
            @Parameter(optionality = Optionality.OPTIONAL)
            final BigDecimal excludedAmount) {
        setDepositType(depositType);
        setExcludedAmount(excludedAmount);
        setStatus(LeaseTermStatus.NEW);
        return this;
    }

    public DepositType default0ChangeParameters() {
        return getDepositType();
    }

    public BigDecimal default1ChangeParameters() {
        return getExcludedAmount();
    }

    public String validateChangeParameters(
            final DepositType depositType,
            final BigDecimal excludedAmount
    ){
        if (excludedAmount != null && excludedAmount.compareTo(BigDecimal.ZERO)<0){
            return "Excluded amount should not be negative";
        }
        return null;
    }

    public LeaseTermForDeposit changeManualDepositValue(
            @Parameter(optionality = Optionality.OPTIONAL)
            final BigDecimal manualDepositValue){
        setManualDepositValue(manualDepositValue);
        setStatus(LeaseTermStatus.NEW);
        return this;
    }

    public BigDecimal default0ChangeManualDepositValue() {
        return getManualDepositValue();
    }

            // //////////////////////////////////////

    @Override
    public BigDecimal valueForDate(LocalDate dueDate) {
        return getInterval().contains(dueDate) ? ObjectUtils.firstNonNull(getManualDepositValue(), getCalculatedDepositValue()) : BigDecimal.ZERO.setScale(2);
   }

    @Override
    public BigDecimal getEffectiveValue() {
        return getEndDate() == null ? ObjectUtils.firstNonNull(getManualDepositValue(), getCalculatedDepositValue()) : BigDecimal.ZERO.setScale(2);
    }


    @Override
    public LeaseTerm verifyUntil(final LocalDate date){
        super.verifyUntil(date);

        if (getEffectiveInterval().contains(date)) {

            BigDecimal currentValue = BigDecimal.ZERO;
            List<LeaseItem> rentItems = getLeaseItem().getLease().findItemsOfType(LeaseItemType.RENT);

            for (LeaseItem rentItem : rentItems) {

                BigDecimal rentItemValueUntilVerificationDate = rentItem.valueForDate(date.minusDays(1));

                if (rentItemValueUntilVerificationDate != null) {
                    currentValue = currentValue.add(rentItemValueUntilVerificationDate);
                }
            }

            if (getExcludedAmount()!= null) {
                setCalculatedDepositValue(getDepositType().calculation(currentValue).subtract(getExcludedAmount()));
            } else {
                setCalculatedDepositValue(getDepositType().calculation(currentValue));
            }

        }
        return this;
    }

    public boolean hideChangeDates(
            final LocalDate newStartDate,
            final LocalDate newEndDate) {
        return true;
    }

    @Override
    @Programmatic
    public void doInitialize() {
        setCalculatedDepositValue(BigDecimal.ZERO);
        setExcludedAmount(BigDecimal.ZERO);
    }

    // //////////////////////////////////////

    @Override
    public LeaseTermValueType valueType() {
        return LeaseTermValueType.FIXED;
    }

    // //////////////////////////////////////

    @Override
    public void copyValuesTo(final LeaseTerm target) {
        LeaseTermForDeposit t = (LeaseTermForDeposit) target;
        super.copyValuesTo(t);
        t.setExcludedAmount(getExcludedAmount());
        t.setDepositType(getDepositType());
    }

}

