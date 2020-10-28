/*
 *
 *  Copyright 2012-2015 Eurocommercial Properties NV
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
package org.estatio.module.budget.dom.budgetcalculation;

import java.math.BigDecimal;

import javax.validation.constraints.Digits;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Nature;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@DomainObject(nature = Nature.VIEW_MODEL)
@AllArgsConstructor
@Getter @Setter
public class CalculationVMForLease {

    @MemberOrder(sequence = "1")
    private String leaseReference;

    @MemberOrder(sequence = "2")
    private String unitReference;

    @MemberOrder(sequence = "3")
    private String budgetItemChargeReferenceAndPartitioning;

    @MemberOrder(sequence = "4")
    @Digits(integer=13, fraction = 2)
    private BigDecimal budgetItemAmountForCalculationPeriod;

    @MemberOrder(sequence = "5")
    private String calculationDescription;

    @MemberOrder(sequence = "6")
    private String tableNameAndSourceValue;

    @MemberOrder(sequence = "7")
    @Digits(integer=13, fraction = 6)
    private BigDecimal value;

    @MemberOrder(sequence = "8")
    @Digits(integer=13, fraction = 2)
    private BigDecimal auditedCostForBudgetPeriod;

    @MemberOrder(sequence = "9")
    private LocalDate calculationStartDate;

    @MemberOrder(sequence = "10")
    private LocalDate calculationEndDate;

}
