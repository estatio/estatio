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

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.dom.utils.MathUtils;

@javax.jdo.annotations.PersistenceCapable
// identityType=IdentityType.DATASTORE inherited from superclass
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
public class LeaseTermForServiceCharge extends LeaseTerm {

    private static final int MONTHS = 3;
    private BigDecimal budgetedValue;

    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true")
    @Optional
    public BigDecimal getBudgetedValue() {
        return budgetedValue;
    }

    public void setBudgetedValue(final BigDecimal budgetedValue) {
        this.budgetedValue = budgetedValue;
    }

    // //////////////////////////////////////

    private BigDecimal auditedValue;

    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true")
    @Optional
    public BigDecimal getAuditedValue() {
        return auditedValue;
    }

    public void setAuditedValue(final BigDecimal auditedValue) {
        this.auditedValue = auditedValue;
    }

    // //////////////////////////////////////

    @Override
    public BigDecimal getApprovedValue() {
        return getStatus().isApproved() ? getTrialValue() : null;
    }

    // //////////////////////////////////////

    @Override
    public BigDecimal getTrialValue() {
        return MathUtils.firstNonZero(getAuditedValue(), getBudgetedValue());
    }

    // //////////////////////////////////////

    @Override
    @Programmatic
    public BigDecimal valueForDate(final LocalDate dueDate) {
        // TODO: we might need an effective date on the Service Charge too
        LocalDate endDate = getInterval().endDateExcluding();
        if (endDate != null) {
            LocalDate effectiveDate = endDate.plusMonths(MONTHS);
            if (getEndDate() != null && effectiveDate.compareTo(dueDate) <= 0) {
                return MathUtils.firstNonZero(getAuditedValue(), getBudgetedValue());
            }
        }
        return getBudgetedValue();
    }

    // //////////////////////////////////////

    @Override
    @Programmatic
    public void initialize() {
        super.initialize();
        LeaseTermForServiceCharge previousTerm = (LeaseTermForServiceCharge) getPrevious();
        if (previousTerm != null) {
            this.setBudgetedValue(MathUtils.firstNonZero(previousTerm.getAuditedValue(), previousTerm.getBudgetedValue()));
        }
    }

    @Override
    @Programmatic
    public void update() {
        super.update();
        if (getPrevious() != null && MathUtils.isZeroOrNull(getBudgetedValue())) {
            if (MathUtils.isNotZeroOrNull(getPrevious().getTrialValue())) {
                setBudgetedValue(getPrevious().getTrialValue());
            }
        }
    }

    // //////////////////////////////////////

    @Override
    public void copyValuesTo(final LeaseTerm target) {
        LeaseTermForServiceCharge t = (LeaseTermForServiceCharge) target;
        super.copyValuesTo(t);
        t.setBudgetedValue(getBudgetedValue());
        t.setAuditedValue(getAuditedValue());
    }

}
