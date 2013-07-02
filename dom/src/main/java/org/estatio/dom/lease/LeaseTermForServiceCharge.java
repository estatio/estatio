/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
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

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Mask;
import org.apache.isis.applib.annotation.MemberGroups;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.dom.utils.MathUtils;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@javax.jdo.annotations.Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
@MemberGroups({ "General", "Dates", "Service Charges", "Related" })
public class LeaseTermForServiceCharge extends LeaseTerm {

    @javax.jdo.annotations.Column(scale = 2)
    private BigDecimal budgetedValue;

    @MemberOrder(sequence = "11", name = "Service Charges")
    public BigDecimal getBudgetedValue() {
        return budgetedValue;
    }

    public void setBudgetedValue(final BigDecimal budgetedValue) {
        this.budgetedValue = budgetedValue;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(scale = 2)
    private BigDecimal auditedValue;

    @MemberOrder(sequence = "12", name = "Service Charges")
    public BigDecimal getAuditedValue() {
        return auditedValue;
    }

    public void setAuditedValue(final BigDecimal auditedValue) {
        this.auditedValue = auditedValue;
    }

    // //////////////////////////////////////

    @Override
    @Mask("")
    public BigDecimal getApprovedValue() {
        return getStatus().isLocked() ? getTrialValue() : null;
    }

    // //////////////////////////////////////

    @Override
    @Mask("")
    public BigDecimal getTrialValue() {
        return MathUtils.isNotZeroOrNull(getAuditedValue()) 
                ? getAuditedValue() 
                : getBudgetedValue();
    }

    // //////////////////////////////////////

    @Override
    @Programmatic
    public BigDecimal valueForDueDate(LocalDate dueDate) {
        // use the audited value after the end of the term and only when its
        // available
        if (MathUtils.isNotZeroOrNull(getAuditedValue())) {
            if (getEndDate() != null) {
                if (dueDate.compareTo(getEndDate().plusDays(1)) >= 0) {
                    return getAuditedValue();
                }
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
            this.setBudgetedValue(MathUtils.isNotZeroOrNull(previousTerm.getAuditedValue()) ? previousTerm.getAuditedValue() : previousTerm.getBudgetedValue());
        }
    }

    @Override
    @Programmatic
    public void update() {
        super.update();
        if (getStatus().isUnlocked()) {
            // date from previous term
            if (getPrevious() != null && MathUtils.isZeroOrNull(getBudgetedValue())) {
                if (MathUtils.isNotZeroOrNull(getPrevious().getTrialValue())) {
                    setBudgetedValue(getPrevious().getTrialValue());
                }
            }

        }
    }

}
