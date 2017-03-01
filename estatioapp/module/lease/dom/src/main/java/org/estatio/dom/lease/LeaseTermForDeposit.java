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
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.InheritanceStrategy;

import org.apache.commons.lang3.ObjectUtils;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.clock.ClockService;

import org.incode.module.base.dom.types.MoneyType;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        schema = "dbo" // Isis' ObjectSpecId inferred from @Discriminator
)
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@javax.jdo.annotations.Discriminator("org.estatio.dom.lease.LeaseTermForDeposit")
public class LeaseTermForDeposit extends LeaseTerm {

    @Column(allowsNull = "false")
    @Getter @Setter
    private Fraction fraction;

    @Column(allowsNull = "true")
    @Getter @Setter
    private LocalDate fixedDepositCalculationDate;

    @Column(allowsNull = "true")
    @Getter @Setter
    private boolean includeVat;

    @Getter @Setter
    @Column(allowsNull = "true", scale = MoneyType.Meta.SCALE)
    private BigDecimal calculatedDepositValue;

    @Getter @Setter
    @Column(allowsNull = "true", scale = MoneyType.Meta.SCALE)
    private BigDecimal depositBase;

    @Getter @Setter
    @Column(allowsNull = "true", scale = MoneyType.Meta.SCALE)
    private BigDecimal manualDepositValue;

    public LeaseTermForDeposit terminate(final LocalDate endDate) {
        setEndDate(endDate);
        return this;
    }

    public LocalDate default0Terminate() {
        return clockService.now();
    }

    public LeaseTermForDeposit changeParameters(
            final Fraction fraction,
            @Parameter(optionality = Optionality.OPTIONAL)
            final LocalDate fixedDepositCalculationDate,
            final boolean includeVat) {
        setFraction(fraction);
        setFixedDepositCalculationDate(fixedDepositCalculationDate);
        setIncludeVat(includeVat);
        setStatus(LeaseTermStatus.NEW);
        return this;
    }

    public Fraction default0ChangeParameters() {
        return this.getFraction();
    }

    public LocalDate default1ChangeParameters() {
        return this.getFixedDepositCalculationDate();
    }

    public boolean default2ChangeParameters() {
        return this.isIncludeVat();
    }

    public LeaseTermForDeposit changeManualDepositValue(
            @Parameter(optionality = Optionality.OPTIONAL)
            final BigDecimal manualDepositValue) {
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
        return getInterval().contains(dueDate) ? ObjectUtils.firstNonNull(getManualDepositValue(), calculatedDepositValueForDate(dueDate)) : BigDecimal.ZERO.setScale(2);
    }

    @Programmatic
    BigDecimal calculatedDepositValueForDate(final LocalDate date){
        BigDecimal depositBaseForDate = calculateDepositBaseValue(date);
        return getFraction().fractionOf(depositBaseForDate);
    }

    @Override
    public BigDecimal getEffectiveValue() {
        return ObjectUtils.firstNonNull(getManualDepositValue(), getCalculatedDepositValue());
    }

    @Override
    public LeaseTerm verifyUntil(final LocalDate date) {
        super.verifyUntil(date);

        setDepositBase(calculateDepositBaseValue(date));
        setCalculatedDepositValue(getFraction().fractionOf(getDepositBase()));

        return this;
    }

    @Programmatic
    BigDecimal calculateDepositBaseValue(final LocalDate verificationDate) {
        BigDecimal calculatedValue = BigDecimal.ZERO;
        for (LeaseItem leaseItem : this.getLeaseItem().getSourceItems().stream().map(i-> i.getSourceItem()).collect(Collectors.toList())) {
            LocalDate dateToUse = ObjectUtils.firstNonNull(getFixedDepositCalculationDate(), verificationDate);
            BigDecimal valueForDate = leaseItem.valueForDate(dateToUse);
            calculatedValue = calculatedValue.add(addVatIfNeeded(valueForDate, leaseItem, dateToUse));
        }
        return calculatedValue;
    }

    @Programmatic
    private BigDecimal addVatIfNeeded(BigDecimal netValue, LeaseItem leaseItem, LocalDate date) {
        if (isIncludeVat()) {
            return leaseItem.getEffectiveTax().grossFromNet(netValue, date);
        }
        return netValue;
    }

    public boolean hideChangeDates() {
        return true;
    }

    @Override
    @Programmatic
    public void doInitialize() {
        setCalculatedDepositValue(BigDecimal.ZERO);
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
        t.setFraction(this.getFraction());
        if (getFixedDepositCalculationDate() != null) t.setFixedDepositCalculationDate(this.getFixedDepositCalculationDate());
        t.setIncludeVat(this.isIncludeVat());
    }

    @Inject
    ClockService clockService;


}

