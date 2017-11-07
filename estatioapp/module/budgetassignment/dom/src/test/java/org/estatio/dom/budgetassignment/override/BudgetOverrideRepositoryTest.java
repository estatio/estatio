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

package org.estatio.dom.budgetassignment.override;

import java.util.Arrays;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationType;
import org.estatio.module.charge.dom.Charge;

public class BudgetOverrideRepositoryTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    public static class ValidateWithSameLeaseAndInvoiceCharge_Test extends BudgetOverrideRepositoryTest {

        BudgetOverrideRepository budgetOverrideRepository = new BudgetOverrideRepository();

        @Test
        public void validation_kicks_in_no_incoming_charge_and_type_specified() throws Exception {

            //given
            BudgetOverrideDummy override = new BudgetOverrideDummy();
            BudgetOverrideDummy overrideToCompare = new BudgetOverrideDummy();
            List<BudgetOverride> overrides = Arrays.asList(overrideToCompare);

            //expect
            expectedException.expect(IllegalArgumentException.class);
            expectedException.expectMessage("Conflicting budget overrides found");

            //when
            budgetOverrideRepository.validateWithSameLeaseAndInvoiceCharge(override, overrides);

        }

        @Test
        public void validation_kicks_in_same_type_specified() throws Exception {

            //given
            BudgetOverrideDummy override = new BudgetOverrideDummy();
            override.setType(BudgetCalculationType.BUDGETED);
            BudgetOverrideDummy overrideToCompare = new BudgetOverrideDummy();
            overrideToCompare.setType(BudgetCalculationType.BUDGETED);
            List<BudgetOverride> overrides = Arrays.asList(overrideToCompare);

            //expect
            expectedException.expect(IllegalArgumentException.class);
            expectedException.expectMessage("Conflicting budget overrides found");

            //when
            budgetOverrideRepository.validateWithSameLeaseAndInvoiceCharge(override, overrides);

        }

        @Test
        public void validation_kicks_in_one_type_specified() throws Exception {

            //given
            BudgetOverrideDummy override = new BudgetOverrideDummy();
            BudgetOverrideDummy overrideToCompare = new BudgetOverrideDummy();
            overrideToCompare.setType(BudgetCalculationType.BUDGETED);
            List<BudgetOverride> overrides = Arrays.asList(overrideToCompare);

            //expect
            expectedException.expect(IllegalArgumentException.class);
            expectedException.expectMessage("Conflicting budget overrides found");

            //when
            budgetOverrideRepository.validateWithSameLeaseAndInvoiceCharge(override, overrides);

        }

        @Test
        public void validation_kicks_in_same_incoming_charge_specified() throws Exception {

            //given
            Charge incomingCharge = new Charge();
            BudgetOverrideDummy override = new BudgetOverrideDummy();
            override.setIncomingCharge(incomingCharge);
            BudgetOverrideDummy overrideToCompare = new BudgetOverrideDummy();
            overrideToCompare.setIncomingCharge(incomingCharge);

            List<BudgetOverride> overrides = Arrays.asList(overrideToCompare);

            //expect
            expectedException.expect(IllegalArgumentException.class);
            expectedException.expectMessage("Conflicting budget overrides found");

            //when
            budgetOverrideRepository.validateWithSameLeaseAndInvoiceCharge(override, overrides);

        }

        @Test
        public void validation_kicks_in_one_incoming_charge_specified() throws Exception {

            //given
            Charge incomingCharge = new Charge();
            BudgetOverrideDummy override = new BudgetOverrideDummy();
            BudgetOverrideDummy overrideToCompare = new BudgetOverrideDummy();
            overrideToCompare.setIncomingCharge(incomingCharge);

            List<BudgetOverride> overrides = Arrays.asList(overrideToCompare);

            //expect
            expectedException.expect(IllegalArgumentException.class);
            expectedException.expectMessage("Conflicting budget overrides found");

            //when
            budgetOverrideRepository.validateWithSameLeaseAndInvoiceCharge(override, overrides);

        }

        @Test
        public void validation_does_not_kick_in_no_overlap() throws Exception {

            //given
            LocalDate date = new LocalDate(2000,01,01);
            BudgetOverrideDummy override = new BudgetOverrideDummy();
            override.setStartDate(date);
            BudgetOverrideDummy overrideToCompare = new BudgetOverrideDummy();
            overrideToCompare.setEndDate(date);
            List<BudgetOverride> overrides = Arrays.asList(overrideToCompare);

            //expect nothing

            //when
            budgetOverrideRepository.validateWithSameLeaseAndInvoiceCharge(override, overrides);
        }

        @Test
        public void validation_does_not_kick_in_different_incoming_charge_specified() throws Exception {

            //given
            Charge incomingCharge1 = new Charge();
            Charge incomingCharge2 = new Charge();
            BudgetOverrideDummy override = new BudgetOverrideDummy();
            override.setIncomingCharge(incomingCharge1);
            BudgetOverrideDummy overrideToCompare = new BudgetOverrideDummy();
            overrideToCompare.setIncomingCharge(incomingCharge2);
            List<BudgetOverride> overrides = Arrays.asList(overrideToCompare);

            //expect nothing

            //when
            budgetOverrideRepository.validateWithSameLeaseAndInvoiceCharge(override, overrides);
        }

        @Test
        public void validation_does_not_kick_in_different_type_specified() throws Exception {

            //given
            BudgetOverrideDummy override = new BudgetOverrideDummy();
            override.setType(BudgetCalculationType.BUDGETED);
            BudgetOverrideDummy overrideToCompare = new BudgetOverrideDummy();
            overrideToCompare.setType(BudgetCalculationType.ACTUAL);
            List<BudgetOverride> overrides = Arrays.asList(overrideToCompare);

            //expect nothing

            //when
            budgetOverrideRepository.validateWithSameLeaseAndInvoiceCharge(override, overrides);
        }

    }

}
