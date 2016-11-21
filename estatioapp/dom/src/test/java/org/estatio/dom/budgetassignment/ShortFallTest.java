/*
 * Copyright 2015 Yodo Int. Projects and Consultancy
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

package org.estatio.dom.budgetassignment;

import java.math.BigDecimal;

import org.junit.Test;

import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationType;

import static org.assertj.core.api.Assertions.assertThat;

public class ShortFallTest {

    ShortFall shortFall1;
    ShortFall shortFall2;

   @Test
   public void AddTest() {

       // given
       shortFall1 = new ShortFall();
       shortFall1.setBudgetedShortFall(new BigDecimal("100.00"));
       shortFall1.setAuditedShortFall(new BigDecimal("111.55"));

       shortFall2 = new ShortFall();
       shortFall2.setBudgetedShortFall(new BigDecimal("100.00"));
       shortFall2.setAuditedShortFall(new BigDecimal("0.45"));

       // when
       shortFall1.add(shortFall2);

       // then
       assertThat(shortFall1.getBudgetedShortFall()).isEqualTo(new BigDecimal("200.00"));
       assertThat(shortFall1.getAuditedShortFall()).isEqualTo(new BigDecimal("112.00"));

   }

    @Test
    public void getShortFallTest() {
        
        //given
        shortFall1 = new ShortFall();
        shortFall1.setBudgetedShortFall(new BigDecimal("100.00"));
        shortFall1.setAuditedShortFall(new BigDecimal("111.55"));

        // when
        // then
        assertThat(shortFall1.getShortFall(BudgetCalculationType.BUDGETED)).isEqualTo(new BigDecimal("100.00"));
        assertThat(shortFall1.getShortFall(BudgetCalculationType.ACTUAL)).isEqualTo(new BigDecimal("111.55"));
    }

}
