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
import org.estatio.dom.charge.Charge;

public class BudgetOverrideRepositoryTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    public static class ValidateWithSameLeaseAndInvoiceCharge_Test extends BudgetOverrideRepositoryTest {

        BudgetOverrideRepository budgetOverrideRepository = new BudgetOverrideRepository();

        @Test
        public void validation_kicks_in_no_incoming_charge_and_type_specified() throws Exception {

            //given
            BudgetOverrideForTesting override = new BudgetOverrideForTesting();
            BudgetOverrideForTesting overrideToCompare = new BudgetOverrideForTesting();
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
            BudgetOverrideForTesting override = new BudgetOverrideForTesting();
            override.setType(BudgetCalculationType.BUDGETED);
            BudgetOverrideForTesting overrideToCompare = new BudgetOverrideForTesting();
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
            BudgetOverrideForTesting override = new BudgetOverrideForTesting();
            BudgetOverrideForTesting overrideToCompare = new BudgetOverrideForTesting();
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
            BudgetOverrideForTesting override = new BudgetOverrideForTesting();
            override.setIncomingCharge(incomingCharge);
            BudgetOverrideForTesting overrideToCompare = new BudgetOverrideForTesting();
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
            BudgetOverrideForTesting override = new BudgetOverrideForTesting();
            BudgetOverrideForTesting overrideToCompare = new BudgetOverrideForTesting();
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
            BudgetOverrideForTesting override = new BudgetOverrideForTesting();
            override.setStartDate(date);
            BudgetOverrideForTesting overrideToCompare = new BudgetOverrideForTesting();
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
            BudgetOverrideForTesting override = new BudgetOverrideForTesting();
            override.setIncomingCharge(incomingCharge1);
            BudgetOverrideForTesting overrideToCompare = new BudgetOverrideForTesting();
            overrideToCompare.setIncomingCharge(incomingCharge2);
            List<BudgetOverride> overrides = Arrays.asList(overrideToCompare);

            //expect nothing

            //when
            budgetOverrideRepository.validateWithSameLeaseAndInvoiceCharge(override, overrides);
        }

        @Test
        public void validation_does_not_kick_in_different_type_specified() throws Exception {

            //given
            BudgetOverrideForTesting override = new BudgetOverrideForTesting();
            override.setType(BudgetCalculationType.BUDGETED);
            BudgetOverrideForTesting overrideToCompare = new BudgetOverrideForTesting();
            overrideToCompare.setType(BudgetCalculationType.ACTUAL);
            List<BudgetOverride> overrides = Arrays.asList(overrideToCompare);

            //expect nothing

            //when
            budgetOverrideRepository.validateWithSameLeaseAndInvoiceCharge(override, overrides);
        }

    }

}
