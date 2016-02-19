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

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;

import org.estatio.dom.utils.MathUtils;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@DomainObject(editing = Editing.DISABLED)
public class LeaseTermForServiceCharge extends LeaseTerm {

    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true")
    @Property(optionality = Optionality.OPTIONAL)
    @Getter @Setter
    private BigDecimal budgetedValue;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true")
    @Property(optionality = Optionality.OPTIONAL)
    @Getter @Setter
    private BigDecimal auditedValue;

    // //////////////////////////////////////

    public LeaseTermForServiceCharge changeValues(
            final @Parameter(optionality = Optionality.OPTIONAL) BigDecimal budgetedValue,
            final @Parameter(optionality = Optionality.OPTIONAL) BigDecimal auditedValue) {
        setBudgetedValue(budgetedValue);
        setAuditedValue(auditedValue);
        return this;
    }

    public BigDecimal default0ChangeValues() {
        return getBudgetedValue();
    }

    public BigDecimal default1ChangeValues() {
        return getAuditedValue();
    }

    // //////////////////////////////////////

    @Override
    public BigDecimal getEffectiveValue() {
        return MathUtils.firstNonZero(getAuditedValue(), getBudgetedValue());
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
                return MathUtils.firstNonZero(getAuditedValue(), getBudgetedValue());
            }
        }
        return getBudgetedValue();
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
    }

}
