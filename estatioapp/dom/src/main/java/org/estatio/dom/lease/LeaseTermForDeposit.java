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
import java.util.List;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.InheritanceStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.dom.JdoColumnScale;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
public class LeaseTermForDeposit extends LeaseTerm {

    @Column(allowsNull = "true", scale = 2)
    @Getter @Setter
    private BigDecimal excludedAmount;

    // //////////////////////////////////////

    @Column(allowsNull = "false")
    @Getter @Setter
    private DepositType depositType;

    // //////////////////////////////////////

    @Getter @Setter
    @Column(allowsNull = "true", scale = JdoColumnScale.MONEY)
    private BigDecimal depositValue;

    // //////////////////////////////////////

    public LeaseTermForDeposit changeParameters(
            final DepositType depositType,
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
        if (excludedAmount.compareTo(BigDecimal.ZERO)<0){
            return "Excluded amount should not be negative";
        }
        return null;
    }

    // //////////////////////////////////////

    @Override
    public BigDecimal valueForDate(LocalDate dueDate) {
        return getDepositValue();
   }

    @Override
    public BigDecimal getEffectiveValue() {
        return getDepositValue();
    }


    @Override
    public LeaseTerm verifyUntil(final LocalDate date){
        super.verifyUntil(date);

        // a term for deposit should have no end date
        if (getEndDate()!=null) {
            setEndDate(null);
        }

        if (getEffectiveInterval().contains(date)) {

            BigDecimal currentValue = BigDecimal.ZERO;
            List<LeaseItem> rentItems = getLeaseItem().getLease().findItemsOfType(LeaseItemType.RENT);

            for (LeaseItem rentItem : rentItems) {

                BigDecimal rentItemValueUntilVerificationDate = rentItem.valueForDate(date.minusDays(1));

                if (rentItemValueUntilVerificationDate != null) {
                    currentValue = currentValue.add(rentItemValueUntilVerificationDate);
                }
            }

            setDepositValue(getDepositType().calculation(currentValue).subtract(getExcludedAmount()));

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
        setDepositValue(BigDecimal.ZERO);
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

