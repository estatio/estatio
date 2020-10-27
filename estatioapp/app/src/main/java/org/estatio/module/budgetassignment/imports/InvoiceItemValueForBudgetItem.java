/*
 * Copyright 2012-2015 Eurocommercial Properties NV
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.estatio.module.budgetassignment.imports;

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
@Getter @Setter
@AllArgsConstructor
public class InvoiceItemValueForBudgetItem {

    @MemberOrder(sequence = "1")
    private String budgetItemChargeReference;

    @MemberOrder(sequence = "2")
    private String invoiceNumber;

    @MemberOrder(sequence = "3")
    private LocalDate invoiceDate;

    @MemberOrder(sequence = "4")
    @Digits(integer=13, fraction = 2)
    private BigDecimal invoiceItemNetAmount;

    @MemberOrder(sequence = "5")
    @Digits(integer=13, fraction = 6)
    private BigDecimal calculatedValue;

    @MemberOrder(sequence = "6")
    private LocalDate invoiceItemChargeStartDate;

    @MemberOrder(sequence = "7")
    private LocalDate invoiceItemChargeEndDate;

    @MemberOrder(sequence = "8")
    private LocalDate calculationStartDate;

    @MemberOrder(sequence = "9")
    private LocalDate calculationEndDate;

}
