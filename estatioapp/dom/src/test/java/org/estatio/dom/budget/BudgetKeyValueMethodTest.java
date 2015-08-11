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

package org.estatio.dom.budget;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;

import org.estatio.app.budget.IdentifierValueInputPair;
import org.estatio.app.budget.IdentifierValuesOutputObject;
import org.estatio.app.budget.Rounding;

import static org.assertj.core.api.Assertions.assertThat;

public class BudgetKeyValueMethodTest {

    @Test
    public void testCalculateDefault() {

        BudgetKeyValueMethod method = BudgetKeyValueMethod.DEFAULT;
        assertThat(method.calculate(new BigDecimal(1), new BigDecimal(1))).isEqualTo(new BigDecimal(1));
        assertThat(method.calculate(new BigDecimal(1), new BigDecimal(10))).isEqualTo(new BigDecimal("0.1"));
        assertThat(method.calculate(new BigDecimal(1), new BigDecimal(100))).isEqualTo(new BigDecimal("0.01"));
    }

    @Test
    public void testCalculateThousand() {

        BudgetKeyValueMethod method = BudgetKeyValueMethod.PROMILLE;
        assertThat(method.calculate(new BigDecimal(1), new BigDecimal(1))).isEqualTo(new BigDecimal(1000));
        assertThat(method.calculate(new BigDecimal(1), new BigDecimal(10))).isEqualTo(new BigDecimal(100));
        assertThat(method.calculate(new BigDecimal(1), new BigDecimal(100))).isEqualTo(new BigDecimal(10));
        assertThat(method.calculate(new BigDecimal(1), new BigDecimal(1000))).isEqualTo(new BigDecimal(1));
        assertThat(method.calculate(new BigDecimal(1), new BigDecimal(10000))).isEqualTo(new BigDecimal(0.1).setScale(1, BigDecimal.ROUND_HALF_UP));
        assertThat(method.calculate(new BigDecimal(1), new BigDecimal(100000))).isEqualTo(new BigDecimal(0.01).setScale(2, BigDecimal.ROUND_HALF_UP));

    }

    @Test
    public void testCalculateHundred() {

        BudgetKeyValueMethod method = BudgetKeyValueMethod.PERCENT;
        assertThat(method.calculate(new BigDecimal(1), new BigDecimal(1))).isEqualTo(new BigDecimal(100));
        assertThat(method.calculate(new BigDecimal(1), new BigDecimal(10))).isEqualTo(new BigDecimal(10));
        assertThat(method.calculate(new BigDecimal(1), new BigDecimal(100))).isEqualTo(new BigDecimal(1));
        assertThat(method.calculate(new BigDecimal(1), new BigDecimal(1000))).isEqualTo(new BigDecimal(0.1).setScale(1, BigDecimal.ROUND_HALF_UP));
        assertThat(method.calculate(new BigDecimal(1), new BigDecimal(10000))).isEqualTo(new BigDecimal(0.01).setScale(2, BigDecimal.ROUND_HALF_UP));
        assertThat(method.calculate(new BigDecimal(1), new BigDecimal(100000))).isEqualTo(new BigDecimal(0.001).setScale(3, BigDecimal.ROUND_HALF_UP));

    }

    @Test
    public void testIsNotValid() {

        //given
        BudgetKeyValueMethod method = BudgetKeyValueMethod.PROMILLE;
        BudgetKeyTable budgetKeyTable = new BudgetKeyTable();
        SortedSet<BudgetKeyItem> budgetKeyItems = new TreeSet<BudgetKeyItem>();

        //when

        for(int i = 0; i < 1; i = i+1) {
            BudgetKeyItem budgetKeyItem = new BudgetKeyItem();
            budgetKeyItem.setKeyValue(new BigDecimal(999.999));
            budgetKeyItems.add(budgetKeyItem);
        }
        budgetKeyTable.setBudgetKeyItems(budgetKeyItems);

        // then
        assertThat(method.keySum(budgetKeyTable)).isEqualTo(new BigDecimal(999.999).setScale(3, BigDecimal.ROUND_HALF_UP));
        assertThat(method.isValid(budgetKeyTable)).isEqualTo(false);


    }

    @Test
    public void testIsValid() {

        //given
        BudgetKeyValueMethod method = BudgetKeyValueMethod.PROMILLE;
        BudgetKeyTable budgetKeyTable = new BudgetKeyTable();
        SortedSet<BudgetKeyItem> budgetKeyItems = new TreeSet<BudgetKeyItem>();

        //when

        for(int i = 0; i < 1; i = i+1) {
                        BudgetKeyItem budgetKeyItem = new BudgetKeyItem();
                        budgetKeyItem.setKeyValue(new BigDecimal(999.9999));
                        budgetKeyItems.add(budgetKeyItem);
                    }
        budgetKeyTable.setBudgetKeyItems(budgetKeyItems);

        // then
        assertThat(method.keySum(budgetKeyTable)).isEqualTo(new BigDecimal(1000).setScale(3, BigDecimal.ROUND_HALF_UP));
        assertThat(method.isValid(budgetKeyTable)).isEqualTo(true);

    }

    @Test
    public void testIsAlsoValid() {

        //given
        BudgetKeyValueMethod method = BudgetKeyValueMethod.PROMILLE;
        BudgetKeyTable budgetKeyTable = new BudgetKeyTable();
        SortedSet<BudgetKeyItem> budgetKeyItems = new TreeSet<BudgetKeyItem>();

        //when

        for(int i = 0; i < 1; i = i+1) {
            BudgetKeyItem budgetKeyItem = new BudgetKeyItem();
            budgetKeyItem.setKeyValue(new BigDecimal(1000.0001));
            budgetKeyItems.add(budgetKeyItem);
        }
        budgetKeyTable.setBudgetKeyItems(budgetKeyItems);

        // then
        assertThat(method.keySum(budgetKeyTable)).isEqualTo(new BigDecimal(1000).setScale(3, BigDecimal.ROUND_HALF_UP));
        assertThat(method.isValid(budgetKeyTable)).isEqualTo(true);

    }

    @Test
    public void generateKeyValuesRoundingBy3DecimalsNegativeDeltaPromille() {

        //given
        BudgetKeyValueMethod method = BudgetKeyValueMethod.PROMILLE;
        ArrayList<IdentifierValueInputPair> input = new ArrayList<IdentifierValueInputPair>();

        //10000.99
        IdentifierValueInputPair pair1 = new IdentifierValueInputPair(0, new BigDecimal(10000.99));
        input.add(pair1);

        //0.99
        IdentifierValueInputPair pair2 = new IdentifierValueInputPair(1, new BigDecimal(0.99));
        input.add(pair2);

        //1.99
        for (int i = 2; i < 14; i = i + 1) {
            IdentifierValueInputPair pair = new IdentifierValueInputPair(i, new BigDecimal(1.99));
            input.add(pair);
        }

        //theoretical max rounding error: 14*0.0005 = +/- 0.007
        //in this example here we get to -0.006

        //when

        ArrayList<IdentifierValuesOutputObject> output = method.generateKeyValues(input, Rounding.DECIMAL3, false);
        BigDecimal sumRoundedValues = BigDecimal.ZERO;
        for (IdentifierValuesOutputObject object: output) {
            sumRoundedValues = sumRoundedValues.add(object.getRoundedValue());
        }
        BigDecimal sumUnroundedValues = BigDecimal.ZERO;
        for (IdentifierValuesOutputObject object: output) {
            sumUnroundedValues = sumUnroundedValues.add(object.getValue());
        }

        //then
        assertThat(output.size()).isEqualTo(14);
        assertThat(output.get(0).getValue()).isEqualTo(new BigDecimal(997.5194).setScale(4, BigDecimal.ROUND_HALF_UP));
        assertThat(output.get(0).getRoundedValue()).isEqualTo(new BigDecimal(997.519).setScale(3, BigDecimal.ROUND_HALF_UP));
        assertThat(output.get(0).getDelta()).isEqualTo(new BigDecimal(-0.0004).setScale(4, BigDecimal.ROUND_HALF_UP));
        assertThat(output.get(0).isCorrected()).isEqualTo(false);

        assertThat(output.get(1).getValue()).isEqualTo(new BigDecimal(0.09874465).setScale(8, BigDecimal.ROUND_HALF_UP));
        assertThat(output.get(1).getRoundedValue()).isEqualTo(new BigDecimal(0.099).setScale(3, BigDecimal.ROUND_HALF_UP));
        assertThat(output.get(1).getDelta()).isEqualTo(new BigDecimal(0.00025535).setScale(8, BigDecimal.ROUND_HALF_UP));
        assertThat(output.get(1).isCorrected()).isEqualTo(false);

        for (int i = 2; i < 14; i = i + 1) {
            assertThat(output.get(i).getValue()).isEqualTo(new BigDecimal(0.1984867).setScale(7, BigDecimal.ROUND_HALF_UP));
            assertThat(output.get(i).getRoundedValue()).isEqualTo(new BigDecimal(0.198).setScale(3, BigDecimal.ROUND_HALF_UP));
            assertThat(output.get(i).getDelta()).isEqualTo(new BigDecimal(-0.0004867).setScale(7, BigDecimal.ROUND_HALF_UP));
            assertThat(output.get(i).isCorrected()).isEqualTo(false);
        }

        // Rounding Error for 3 decimals
        assertThat(sumRoundedValues.setScale(3, BigDecimal.ROUND_HALF_UP)).isEqualTo(new BigDecimal(999.994).setScale(3, BigDecimal.ROUND_HALF_UP));
        assertThat(sumUnroundedValues.setScale(3, BigDecimal.ROUND_HALF_UP)).isEqualTo(new BigDecimal(1000.000).setScale(3, BigDecimal.ROUND_HALF_UP));


        //And when (with correction)
        ArrayList<IdentifierValuesOutputObject> secondOutput = method.generateKeyValues(input, Rounding.DECIMAL3, true);
        BigDecimal sumOfRoundedValues = BigDecimal.ZERO;
        for (IdentifierValuesOutputObject object: secondOutput) {
            sumOfRoundedValues = sumOfRoundedValues.add(object.getRoundedValue());
        }
        BigDecimal sumOfUnroundedValues = BigDecimal.ZERO;
        for (IdentifierValuesOutputObject object: secondOutput) {
            sumOfUnroundedValues = sumOfUnroundedValues.add(object.getValue());
        }

        //then
        assertThat(secondOutput.size()).isEqualTo(14);
        assertThat(secondOutput.get(0).getValue()).isEqualTo(new BigDecimal(997.5194).setScale(4, BigDecimal.ROUND_HALF_UP));
        assertThat(secondOutput.get(0).getRoundedValue()).isEqualTo(new BigDecimal(997.519).setScale(3, BigDecimal.ROUND_HALF_UP));
        assertThat(secondOutput.get(0).getDelta()).isEqualTo(new BigDecimal(-0.0004).setScale(4, BigDecimal.ROUND_HALF_UP));
        assertThat(secondOutput.get(0).isCorrected()).isEqualTo(false);

        assertThat(secondOutput.get(1).getValue()).isEqualTo(new BigDecimal(0.09874465).setScale(8, BigDecimal.ROUND_HALF_UP));
        assertThat(secondOutput.get(1).getRoundedValue()).isEqualTo(new BigDecimal(0.099).setScale(3, BigDecimal.ROUND_HALF_UP));
        assertThat(secondOutput.get(1).getDelta()).isEqualTo(new BigDecimal(0.00025535).setScale(8, BigDecimal.ROUND_HALF_UP));
        assertThat(secondOutput.get(1).isCorrected()).isEqualTo(false);

        for (int i = 2; i < 8; i = i + 1) {
            assertThat(secondOutput.get(i).getValue()).isEqualTo(new BigDecimal(0.1984867).setScale(7, BigDecimal.ROUND_HALF_UP));
            assertThat(secondOutput.get(i).getRoundedValue()).isEqualTo(new BigDecimal(0.198).setScale(3, BigDecimal.ROUND_HALF_UP));
            assertThat(secondOutput.get(i).getDelta()).isEqualTo(new BigDecimal(-0.0004867).setScale(7, BigDecimal.ROUND_HALF_UP));
            assertThat(secondOutput.get(i).isCorrected()).isEqualTo(false);
        }

        for (int i = 8; i < 14; i = i + 1) {
            assertThat(secondOutput.get(i).getValue()).isEqualTo(new BigDecimal(0.1984867).setScale(7, BigDecimal.ROUND_HALF_UP));
            assertThat(secondOutput.get(i).getRoundedValue()).isEqualTo(new BigDecimal(0.199).setScale(3, BigDecimal.ROUND_HALF_UP));
            assertThat(secondOutput.get(i).getDelta()).isEqualTo(new BigDecimal(-0.0004867).setScale(7, BigDecimal.ROUND_HALF_UP));
            assertThat(secondOutput.get(i).isCorrected()).isEqualTo(true);
        }

        // Corrected Rounding Error for 3 decimals
        assertThat(sumOfRoundedValues.setScale(3, BigDecimal.ROUND_HALF_UP)).isEqualTo(new BigDecimal(1000.000).setScale(3, BigDecimal.ROUND_HALF_UP));
        assertThat(sumOfUnroundedValues.setScale(3, BigDecimal.ROUND_HALF_UP)).isEqualTo(new BigDecimal(1000.000).setScale(3, BigDecimal.ROUND_HALF_UP));


    }

    @Test
    public void generateKeyValuesRoundingBy3DecimalsPositiveDelta() {

        //given
        BudgetKeyValueMethod method = BudgetKeyValueMethod.PROMILLE;
        ArrayList<IdentifierValueInputPair> input = new ArrayList<IdentifierValueInputPair>();

        //10000.99
        IdentifierValueInputPair pair1 = new IdentifierValueInputPair(0, new BigDecimal(10000.99));
        input.add(pair1);

        //100.01
        IdentifierValueInputPair pair2 = new IdentifierValueInputPair(1, new BigDecimal(100.01));
        input.add(pair2);

        //1.99
        for (int i = 2; i < 14; i = i + 1) {
            IdentifierValueInputPair pair = new IdentifierValueInputPair(i, new BigDecimal(1.99));
            input.add(pair);
        }

        //theoretical max rounding error: 14*0.0005 = +/- 0.007
        //in this example here we get to + 0.006

        //when

        ArrayList<IdentifierValuesOutputObject> output = method.generateKeyValues(input, Rounding.DECIMAL3, false);
        BigDecimal sumRoundedValues = BigDecimal.ZERO;
        for (IdentifierValuesOutputObject object: output) {
            sumRoundedValues = sumRoundedValues.add(object.getRoundedValue());
        }
        BigDecimal sumUnroundedValues = BigDecimal.ZERO;
        for (IdentifierValuesOutputObject object: output) {
            sumUnroundedValues = sumUnroundedValues.add(object.getValue());
        }

        //then
        assertThat(output.size()).isEqualTo(14);
        assertThat(output.get(0).getValue()).isEqualTo(new BigDecimal(987.7638).setScale(4, BigDecimal.ROUND_HALF_UP));
        assertThat(output.get(0).getRoundedValue()).isEqualTo(new BigDecimal(987.764).setScale(3, BigDecimal.ROUND_HALF_UP));
        assertThat(output.get(0).getDelta()).isEqualTo(new BigDecimal(0.0002).setScale(4, BigDecimal.ROUND_HALF_UP));
        assertThat(output.get(0).isCorrected()).isEqualTo(false);

        assertThat(output.get(1).getValue()).isEqualTo(new BigDecimal(9.877648).setScale(6, BigDecimal.ROUND_HALF_UP));
        assertThat(output.get(1).getRoundedValue()).isEqualTo(new BigDecimal(9.878).setScale(3, BigDecimal.ROUND_HALF_UP));
        assertThat(output.get(1).getDelta()).isEqualTo(new BigDecimal(0.000352).setScale(6, BigDecimal.ROUND_HALF_UP));
        assertThat(output.get(1).isCorrected()).isEqualTo(false);

        for (int i = 2; i < 14; i = i + 1) {
            assertThat(output.get(i).getValue()).isEqualTo(new BigDecimal(0.1965455).setScale(7, BigDecimal.ROUND_HALF_UP));
            assertThat(output.get(i).getRoundedValue()).isEqualTo(new BigDecimal(0.197).setScale(3, BigDecimal.ROUND_HALF_UP));
            assertThat(output.get(i).getDelta()).isEqualTo(new BigDecimal(0.0004545).setScale(7, BigDecimal.ROUND_HALF_UP));
            assertThat(output.get(i).isCorrected()).isEqualTo(false);
        }

        // Rounding Error for 3 decimals
        assertThat(sumRoundedValues.setScale(3, BigDecimal.ROUND_HALF_UP)).isEqualTo(new BigDecimal(1000.006).setScale(3, BigDecimal.ROUND_HALF_UP));
        assertThat(sumUnroundedValues.setScale(3, BigDecimal.ROUND_HALF_UP)).isEqualTo(new BigDecimal(1000.000).setScale(3, BigDecimal.ROUND_HALF_UP));


        //And when (with correction)
        ArrayList<IdentifierValuesOutputObject> secondOutput = method.generateKeyValues(input, Rounding.DECIMAL3, true);
        BigDecimal sumOfRoundedValues = BigDecimal.ZERO;
        for (IdentifierValuesOutputObject object: secondOutput) {
            sumOfRoundedValues = sumOfRoundedValues.add(object.getRoundedValue());
        }
        BigDecimal sumOfUnroundedValues = BigDecimal.ZERO;
        for (IdentifierValuesOutputObject object: secondOutput) {
            sumOfUnroundedValues = sumOfUnroundedValues.add(object.getValue());
        }

        //then
        assertThat(secondOutput.size()).isEqualTo(14);
        assertThat(secondOutput.get(0).getValue()).isEqualTo(new BigDecimal(987.7638).setScale(4, BigDecimal.ROUND_HALF_UP));
        assertThat(secondOutput.get(0).getRoundedValue()).isEqualTo(new BigDecimal(987.764).setScale(3, BigDecimal.ROUND_HALF_UP));
        assertThat(secondOutput.get(0).getDelta()).isEqualTo(new BigDecimal(0.0002).setScale(4, BigDecimal.ROUND_HALF_UP));
        assertThat(secondOutput.get(0).isCorrected()).isEqualTo(false);

        assertThat(secondOutput.get(1).getValue()).isEqualTo(new BigDecimal(9.877648).setScale(6, BigDecimal.ROUND_HALF_UP));
        assertThat(secondOutput.get(1).getRoundedValue()).isEqualTo(new BigDecimal(9.878).setScale(3, BigDecimal.ROUND_HALF_UP));
        assertThat(secondOutput.get(1).getDelta()).isEqualTo(new BigDecimal(0.000352).setScale(6, BigDecimal.ROUND_HALF_UP));
        assertThat(secondOutput.get(1).isCorrected()).isEqualTo(false);

        for (int i = 2; i < 8; i = i + 1) {
            assertThat(secondOutput.get(i).getValue()).isEqualTo(new BigDecimal(0.1965455).setScale(7, BigDecimal.ROUND_HALF_UP));
            assertThat(secondOutput.get(i).getRoundedValue()).isEqualTo(new BigDecimal(0.197).setScale(3, BigDecimal.ROUND_HALF_UP));
            assertThat(secondOutput.get(i).getDelta()).isEqualTo(new BigDecimal(0.0004545).setScale(7, BigDecimal.ROUND_HALF_UP));
            assertThat(secondOutput.get(i).isCorrected()).isEqualTo(false);
        }

        for (int i = 8; i < 14; i = i + 1) {
            assertThat(secondOutput.get(i).getValue()).isEqualTo(new BigDecimal(0.1965455).setScale(7, BigDecimal.ROUND_HALF_UP));
            assertThat(secondOutput.get(i).getRoundedValue()).isEqualTo(new BigDecimal(0.196).setScale(3, BigDecimal.ROUND_HALF_UP));
            assertThat(secondOutput.get(i).getDelta()).isEqualTo(new BigDecimal(0.0004545).setScale(7, BigDecimal.ROUND_HALF_UP));
            assertThat(secondOutput.get(i).isCorrected()).isEqualTo(true);
        }

        // Corrected Rounding Error for 3 decimals
        assertThat(sumOfRoundedValues.setScale(3, BigDecimal.ROUND_HALF_UP)).isEqualTo(new BigDecimal(1000.000).setScale(3, BigDecimal.ROUND_HALF_UP));
        assertThat(sumOfUnroundedValues.setScale(3, BigDecimal.ROUND_HALF_UP)).isEqualTo(new BigDecimal(1000.000).setScale(3, BigDecimal.ROUND_HALF_UP));


    }

    @Test
    public void generateKeyValuesRoundingBy2DecimalsNegativeDelta() {

        //given
        BudgetKeyValueMethod method = BudgetKeyValueMethod.PROMILLE;
        ArrayList<IdentifierValueInputPair> input = new ArrayList<IdentifierValueInputPair>();

        //10000.99
        IdentifierValueInputPair pair1 = new IdentifierValueInputPair(0, new BigDecimal(10000.99));
        input.add(pair1);

        //200.09
        IdentifierValueInputPair pair2 = new IdentifierValueInputPair(1, new BigDecimal(200.09));
        input.add(pair2);

        //1.99
        for (int i = 2; i < 14; i = i + 1) {
            IdentifierValueInputPair pair = new IdentifierValueInputPair(i, new BigDecimal(1.99));
            input.add(pair);
        }

        //theoretical max rounding error: 14*0.005 = +/-0.07
        //in this example here we get to -0.05

        //when

        ArrayList<IdentifierValuesOutputObject> output = method.generateKeyValues(input, Rounding.DECIMAL2, false);
        BigDecimal sumRoundedValues = BigDecimal.ZERO;
        for (IdentifierValuesOutputObject object: output) {
            sumRoundedValues = sumRoundedValues.add(object.getRoundedValue());
        }
        BigDecimal sumUnroundedValues = BigDecimal.ZERO;
        for (IdentifierValuesOutputObject object: output) {
            sumUnroundedValues = sumUnroundedValues.add(object.getValue());
        }

        //then
        assertThat(output.size()).isEqualTo(14);
        assertThat(output.get(0).getValue()).isEqualTo(new BigDecimal(978.0958).setScale(4, BigDecimal.ROUND_HALF_UP));
        assertThat(output.get(0).getRoundedValue()).isEqualTo(new BigDecimal(978.10).setScale(2, BigDecimal.ROUND_HALF_UP));
        assertThat(output.get(0).getDelta()).isEqualTo(new BigDecimal(0.0042).setScale(4, BigDecimal.ROUND_HALF_UP));
        assertThat(output.get(0).isCorrected()).isEqualTo(false);

        assertThat(output.get(1).getValue()).isEqualTo(new BigDecimal(19.56878).setScale(5, BigDecimal.ROUND_HALF_UP));
        assertThat(output.get(1).getRoundedValue()).isEqualTo(new BigDecimal(19.57).setScale(2, BigDecimal.ROUND_HALF_UP));
        assertThat(output.get(1).getDelta()).isEqualTo(new BigDecimal(0.00122).setScale(5, BigDecimal.ROUND_HALF_UP));
        assertThat(output.get(1).isCorrected()).isEqualTo(false);

        for (int i = 2; i < 14; i = i + 1) {
            assertThat(output.get(i).getValue()).isEqualTo(new BigDecimal(0.1946218).setScale(7, BigDecimal.ROUND_HALF_UP));
            assertThat(output.get(i).getRoundedValue()).isEqualTo(new BigDecimal(0.19).setScale(2, BigDecimal.ROUND_HALF_UP));
            assertThat(output.get(i).getDelta()).isEqualTo(new BigDecimal(-0.0046218).setScale(7, BigDecimal.ROUND_HALF_UP));
            assertThat(output.get(i).isCorrected()).isEqualTo(false);
        }

        // Rounding Error for 2 decimals
        assertThat(sumRoundedValues.setScale(2, BigDecimal.ROUND_HALF_UP)).isEqualTo(new BigDecimal(999.95).setScale(2, BigDecimal.ROUND_HALF_UP));
        assertThat(sumUnroundedValues.setScale(2, BigDecimal.ROUND_HALF_UP)).isEqualTo(new BigDecimal(1000.00).setScale(2, BigDecimal.ROUND_HALF_UP));


        //And when (with correction)
        ArrayList<IdentifierValuesOutputObject> secondOutput = method.generateKeyValues(input, Rounding.DECIMAL2, true);
        BigDecimal sumOfRoundedValues = BigDecimal.ZERO;
        for (IdentifierValuesOutputObject object: secondOutput) {
            sumOfRoundedValues = sumOfRoundedValues.add(object.getRoundedValue());
        }
        BigDecimal sumOfUnroundedValues = BigDecimal.ZERO;
        for (IdentifierValuesOutputObject object: secondOutput) {
            sumOfUnroundedValues = sumOfUnroundedValues.add(object.getValue());
        }

        //then
        assertThat(secondOutput.size()).isEqualTo(14);
        assertThat(secondOutput.get(0).getValue()).isEqualTo(new BigDecimal(978.0958).setScale(4, BigDecimal.ROUND_HALF_UP));
        assertThat(secondOutput.get(0).getRoundedValue()).isEqualTo(new BigDecimal(978.10).setScale(2, BigDecimal.ROUND_HALF_UP));
        assertThat(secondOutput.get(0).getDelta()).isEqualTo(new BigDecimal(0.0042).setScale(4, BigDecimal.ROUND_HALF_UP));
        assertThat(secondOutput.get(0).isCorrected()).isEqualTo(false);

        assertThat(secondOutput.get(1).getValue()).isEqualTo(new BigDecimal(19.56878).setScale(5, BigDecimal.ROUND_HALF_UP));
        assertThat(secondOutput.get(1).getRoundedValue()).isEqualTo(new BigDecimal(19.57).setScale(2, BigDecimal.ROUND_HALF_UP));
        assertThat(secondOutput.get(1).getDelta()).isEqualTo(new BigDecimal(0.00122).setScale(5, BigDecimal.ROUND_HALF_UP));
        assertThat(secondOutput.get(1).isCorrected()).isEqualTo(false);

        for (int i = 2; i < 9; i = i + 1) {
            assertThat(secondOutput.get(i).getValue()).isEqualTo(new BigDecimal(0.1946218).setScale(7, BigDecimal.ROUND_HALF_UP));
            assertThat(secondOutput.get(i).getRoundedValue()).isEqualTo(new BigDecimal(0.19).setScale(2, BigDecimal.ROUND_HALF_UP));
            assertThat(secondOutput.get(i).getDelta()).isEqualTo(new BigDecimal(-0.0046218).setScale(7, BigDecimal.ROUND_HALF_UP));
            assertThat(secondOutput.get(i).isCorrected()).isEqualTo(false);
        }

        for (int i = 9; i < 14; i = i + 1) {
            assertThat(secondOutput.get(i).getValue()).isEqualTo(new BigDecimal(0.1946218).setScale(7, BigDecimal.ROUND_HALF_UP));
            assertThat(secondOutput.get(i).getRoundedValue()).isEqualTo(new BigDecimal(0.20).setScale(2, BigDecimal.ROUND_HALF_UP));
            assertThat(secondOutput.get(i).getDelta()).isEqualTo(new BigDecimal(-0.0046218).setScale(7, BigDecimal.ROUND_HALF_UP));
            assertThat(secondOutput.get(i).isCorrected()).isEqualTo(true);
        }

        // Corrected Rounding Error for 2 decimals
        assertThat(sumOfRoundedValues.setScale(2, BigDecimal.ROUND_HALF_UP)).isEqualTo(new BigDecimal(1000.00).setScale(2, BigDecimal.ROUND_HALF_UP));
        assertThat(sumOfUnroundedValues.setScale(2, BigDecimal.ROUND_HALF_UP)).isEqualTo(new BigDecimal(1000.00).setScale(2, BigDecimal.ROUND_HALF_UP));

    }

    @Test
    public void generateKeyValuesRoundingBy2DecimalsPositiveDelta() {

        //given
        BudgetKeyValueMethod method = BudgetKeyValueMethod.PROMILLE;
        ArrayList<IdentifierValueInputPair> input = new ArrayList<IdentifierValueInputPair>();

        //10000.99
        IdentifierValueInputPair pair1 = new IdentifierValueInputPair(0, new BigDecimal(10000.99));
        input.add(pair1);

        //100.09
        IdentifierValueInputPair pair2 = new IdentifierValueInputPair(1, new BigDecimal(100.09));
        input.add(pair2);

        //1.99
        for (int i = 2; i < 14; i = i + 1) {
            IdentifierValueInputPair pair = new IdentifierValueInputPair(i, new BigDecimal(1.99));
            input.add(pair);
        }

        //theoretical max rounding error: 14*0.005 = +/- 0.07
        //in this example here we get to + 0.05

        //when

        ArrayList<IdentifierValuesOutputObject> output = method.generateKeyValues(input, Rounding.DECIMAL2, false);
        BigDecimal sumRoundedValues = BigDecimal.ZERO;
        for (IdentifierValuesOutputObject object: output) {
            sumRoundedValues = sumRoundedValues.add(object.getRoundedValue());
        }
        BigDecimal sumUnroundedValues = BigDecimal.ZERO;
        for (IdentifierValuesOutputObject object: output) {
            sumUnroundedValues = sumUnroundedValues.add(object.getValue());
        }

        //then
        assertThat(output.size()).isEqualTo(14);
        assertThat(output.get(0).getValue()).isEqualTo(new BigDecimal(987.7560).setScale(4, BigDecimal.ROUND_HALF_UP));
        assertThat(output.get(0).getRoundedValue()).isEqualTo(new BigDecimal(987.76).setScale(2, BigDecimal.ROUND_HALF_UP));
        assertThat(output.get(0).getDelta()).isEqualTo(new BigDecimal(0.0040).setScale(4, BigDecimal.ROUND_HALF_UP));
        assertThat(output.get(0).isCorrected()).isEqualTo(false);

        assertThat(output.get(1).getValue()).isEqualTo(new BigDecimal(9.885471).setScale(6, BigDecimal.ROUND_HALF_UP));
        assertThat(output.get(1).getRoundedValue()).isEqualTo(new BigDecimal(9.89).setScale(2, BigDecimal.ROUND_HALF_UP));
        assertThat(output.get(1).getDelta()).isEqualTo(new BigDecimal(0.004529).setScale(6, BigDecimal.ROUND_HALF_UP));
        assertThat(output.get(1).isCorrected()).isEqualTo(false);

        for (int i = 2; i < 14; i = i + 1) {
            assertThat(output.get(i).getValue()).isEqualTo(new BigDecimal(0.1965440).setScale(7, BigDecimal.ROUND_HALF_UP));
            assertThat(output.get(i).getRoundedValue()).isEqualTo(new BigDecimal(0.20).setScale(2, BigDecimal.ROUND_HALF_UP));
            assertThat(output.get(i).getDelta()).isEqualTo(new BigDecimal(0.0034560).setScale(7, BigDecimal.ROUND_HALF_UP));
            assertThat(output.get(i).isCorrected()).isEqualTo(false);
        }

        // Rounding Error for 2 decimals
        assertThat(sumRoundedValues.setScale(2, BigDecimal.ROUND_HALF_UP)).isEqualTo(new BigDecimal(1000.05).setScale(2, BigDecimal.ROUND_HALF_UP));
        assertThat(sumUnroundedValues.setScale(2, BigDecimal.ROUND_HALF_UP)).isEqualTo(new BigDecimal(1000.00).setScale(2, BigDecimal.ROUND_HALF_UP));


        //And when (with correction)
        ArrayList<IdentifierValuesOutputObject> secondOutput = method.generateKeyValues(input, Rounding.DECIMAL2, true);
        BigDecimal sumOfRoundedValues = BigDecimal.ZERO;
        for (IdentifierValuesOutputObject object: secondOutput) {
            sumOfRoundedValues = sumOfRoundedValues.add(object.getRoundedValue());
        }
        BigDecimal sumOfUnroundedValues = BigDecimal.ZERO;
        for (IdentifierValuesOutputObject object: secondOutput) {
            sumOfUnroundedValues = sumOfUnroundedValues.add(object.getValue());
        }

        //then
        assertThat(secondOutput.size()).isEqualTo(14);
        assertThat(secondOutput.get(0).getValue()).isEqualTo(new BigDecimal(987.7560).setScale(4, BigDecimal.ROUND_HALF_UP));
        assertThat(secondOutput.get(0).getRoundedValue()).isEqualTo(new BigDecimal(987.76).setScale(2, BigDecimal.ROUND_HALF_UP));
        assertThat(secondOutput.get(0).getDelta()).isEqualTo(new BigDecimal(0.0040).setScale(4, BigDecimal.ROUND_HALF_UP));
        assertThat(secondOutput.get(0).isCorrected()).isEqualTo(false);

        assertThat(secondOutput.get(1).getValue()).isEqualTo(new BigDecimal(9.885471).setScale(6, BigDecimal.ROUND_HALF_UP));
        assertThat(secondOutput.get(1).getRoundedValue()).isEqualTo(new BigDecimal(9.89).setScale(2, BigDecimal.ROUND_HALF_UP));
        assertThat(secondOutput.get(1).getDelta()).isEqualTo(new BigDecimal(0.004529).setScale(6, BigDecimal.ROUND_HALF_UP));
        assertThat(secondOutput.get(1).isCorrected()).isEqualTo(false);

        for (int i = 2; i < 9; i = i + 1) {
            assertThat(secondOutput.get(i).getValue()).isEqualTo(new BigDecimal(0.1965440).setScale(7, BigDecimal.ROUND_HALF_UP));
            assertThat(secondOutput.get(i).getRoundedValue()).isEqualTo(new BigDecimal(0.20).setScale(2, BigDecimal.ROUND_HALF_UP));
            assertThat(secondOutput.get(i).getDelta()).isEqualTo(new BigDecimal(0.0034560).setScale(7, BigDecimal.ROUND_HALF_UP));
            assertThat(secondOutput.get(i).isCorrected()).isEqualTo(false);
        }

        for (int i = 9; i < 14; i = i + 1) {
            assertThat(secondOutput.get(i).getValue()).isEqualTo(new BigDecimal(0.1965440).setScale(7, BigDecimal.ROUND_HALF_UP));
            assertThat(secondOutput.get(i).getRoundedValue()).isEqualTo(new BigDecimal(0.19).setScale(2, BigDecimal.ROUND_HALF_UP));
            assertThat(secondOutput.get(i).getDelta()).isEqualTo(new BigDecimal(0.0034560).setScale(7, BigDecimal.ROUND_HALF_UP));
            assertThat(secondOutput.get(i).isCorrected()).isEqualTo(true);
        }

        // Corrected Rounding Error for 2 decimals
        assertThat(sumOfRoundedValues.setScale(2, BigDecimal.ROUND_HALF_UP)).isEqualTo(new BigDecimal(1000.00).setScale(2, BigDecimal.ROUND_HALF_UP));
        assertThat(sumOfUnroundedValues.setScale(2, BigDecimal.ROUND_HALF_UP)).isEqualTo(new BigDecimal(1000.00).setScale(2, BigDecimal.ROUND_HALF_UP));

    }

    @Test
    public void generateKeyValuesRoundingBy3DecimalsPositiveDeltaPercent() {

        //given
        BudgetKeyValueMethod method = BudgetKeyValueMethod.PERCENT;
        ArrayList<IdentifierValueInputPair> input = new ArrayList<IdentifierValueInputPair>();

        //10000.99
        IdentifierValueInputPair pair1 = new IdentifierValueInputPair(0, new BigDecimal(10000.99));
        input.add(pair1);

        //0.99
        IdentifierValueInputPair pair2 = new IdentifierValueInputPair(1, new BigDecimal(0.99));
        input.add(pair2);

        //1.99
        for (int i = 2; i < 14; i = i + 1) {
            IdentifierValueInputPair pair = new IdentifierValueInputPair(i, new BigDecimal(1.99));
            input.add(pair);
        }

        //theoretical max rounding error: 14*0.0005 = +/- 0.007
        //in this example here we get to 0.002

        //when

        ArrayList<IdentifierValuesOutputObject> output = method.generateKeyValues(input, Rounding.DECIMAL3, false);
        BigDecimal sumRoundedValues = BigDecimal.ZERO;
        for (IdentifierValuesOutputObject object: output) {
            sumRoundedValues = sumRoundedValues.add(object.getRoundedValue());
        }
        BigDecimal sumUnroundedValues = BigDecimal.ZERO;
        for (IdentifierValuesOutputObject object: output) {
            sumUnroundedValues = sumUnroundedValues.add(object.getValue());
        }

        //then
        assertThat(output.size()).isEqualTo(14);
        assertThat(output.get(0).getValue()).isEqualTo(new BigDecimal(99.75194).setScale(5, BigDecimal.ROUND_HALF_UP));
        assertThat(output.get(0).getRoundedValue()).isEqualTo(new BigDecimal(99.752).setScale(3, BigDecimal.ROUND_HALF_UP));
        assertThat(output.get(0).getDelta()).isEqualTo(new BigDecimal(0.00006).setScale(5, BigDecimal.ROUND_HALF_UP));
        assertThat(output.get(0).isCorrected()).isEqualTo(false);

        assertThat(output.get(1).getValue()).isEqualTo(new BigDecimal(0.009874465).setScale(9, BigDecimal.ROUND_HALF_UP));
        assertThat(output.get(1).getRoundedValue()).isEqualTo(new BigDecimal(0.010).setScale(3, BigDecimal.ROUND_HALF_UP));
        assertThat(output.get(1).getDelta()).isEqualTo(new BigDecimal(0.000125535).setScale(9, BigDecimal.ROUND_HALF_UP));
        assertThat(output.get(1).isCorrected()).isEqualTo(false);

        for (int i = 2; i < 14; i = i + 1) {
            assertThat(output.get(i).getValue()).isEqualTo(new BigDecimal(0.01984867).setScale(8, BigDecimal.ROUND_HALF_UP));
            assertThat(output.get(i).getRoundedValue()).isEqualTo(new BigDecimal(0.020).setScale(3, BigDecimal.ROUND_HALF_UP));
            assertThat(output.get(i).getDelta()).isEqualTo(new BigDecimal(0.00015133).setScale(8, BigDecimal.ROUND_HALF_UP));
            assertThat(output.get(i).isCorrected()).isEqualTo(false);
        }

        // Rounding Error for 3 decimals
        assertThat(sumRoundedValues.setScale(3, BigDecimal.ROUND_HALF_UP)).isEqualTo(new BigDecimal(100.002).setScale(3, BigDecimal.ROUND_HALF_UP));
        assertThat(sumUnroundedValues.setScale(3, BigDecimal.ROUND_HALF_UP)).isEqualTo(new BigDecimal(100.000).setScale(3, BigDecimal.ROUND_HALF_UP));


        //And when (with correction)
        ArrayList<IdentifierValuesOutputObject> secondOutput = method.generateKeyValues(input, Rounding.DECIMAL3, true);
        BigDecimal sumOfRoundedValues = BigDecimal.ZERO;
        for (IdentifierValuesOutputObject object: secondOutput) {
            sumOfRoundedValues = sumOfRoundedValues.add(object.getRoundedValue());
        }
        BigDecimal sumOfUnroundedValues = BigDecimal.ZERO;
        for (IdentifierValuesOutputObject object: secondOutput) {
            sumOfUnroundedValues = sumOfUnroundedValues.add(object.getValue());
        }

        //then
        assertThat(secondOutput.size()).isEqualTo(14);
        assertThat(secondOutput.get(0).getValue()).isEqualTo(new BigDecimal(99.75194).setScale(5, BigDecimal.ROUND_HALF_UP));
        assertThat(secondOutput.get(0).getRoundedValue()).isEqualTo(new BigDecimal(99.752).setScale(3, BigDecimal.ROUND_HALF_UP));
        assertThat(secondOutput.get(0).getDelta()).isEqualTo(new BigDecimal(0.00006).setScale(5, BigDecimal.ROUND_HALF_UP));
        assertThat(secondOutput.get(0).isCorrected()).isEqualTo(false);

        assertThat(secondOutput.get(1).getValue()).isEqualTo(new BigDecimal(0.009874465).setScale(9, BigDecimal.ROUND_HALF_UP));
        assertThat(secondOutput.get(1).getRoundedValue()).isEqualTo(new BigDecimal(0.010).setScale(3, BigDecimal.ROUND_HALF_UP));
        assertThat(secondOutput.get(1).getDelta()).isEqualTo(new BigDecimal(0.000125535).setScale(9, BigDecimal.ROUND_HALF_UP));
        assertThat(secondOutput.get(1).isCorrected()).isEqualTo(false);

        for (int i = 2; i < 12; i = i + 1) {
            assertThat(secondOutput.get(i).getValue()).isEqualTo(new BigDecimal(0.01984867).setScale(8, BigDecimal.ROUND_HALF_UP));
            assertThat(secondOutput.get(i).getRoundedValue()).isEqualTo(new BigDecimal(0.020).setScale(3, BigDecimal.ROUND_HALF_UP));
            assertThat(secondOutput.get(i).getDelta()).isEqualTo(new BigDecimal(0.00015133).setScale(8, BigDecimal.ROUND_HALF_UP));
            assertThat(secondOutput.get(i).isCorrected()).isEqualTo(false);
        }

        for (int i = 12; i < 14; i = i + 1) {
            assertThat(secondOutput.get(i).getValue()).isEqualTo(new BigDecimal(0.01984867).setScale(8, BigDecimal.ROUND_HALF_UP));
            assertThat(secondOutput.get(i).getRoundedValue()).isEqualTo(new BigDecimal(0.019).setScale(3, BigDecimal.ROUND_HALF_UP));
            assertThat(secondOutput.get(i).getDelta()).isEqualTo(new BigDecimal(0.00015133).setScale(8, BigDecimal.ROUND_HALF_UP));
            assertThat(secondOutput.get(i).isCorrected()).isEqualTo(true);
        }

        // Corrected Rounding Error for 3 decimals
        assertThat(sumOfRoundedValues.setScale(3, BigDecimal.ROUND_HALF_UP)).isEqualTo(new BigDecimal(100.000).setScale(3, BigDecimal.ROUND_HALF_UP));
        assertThat(sumOfUnroundedValues.setScale(3, BigDecimal.ROUND_HALF_UP)).isEqualTo(new BigDecimal(100.000).setScale(3, BigDecimal.ROUND_HALF_UP));


    }

    @Test
    public void generateKeyValuesRoundingBy3DecimalsNegativeDeltaPercent() {

        //given
        BudgetKeyValueMethod method = BudgetKeyValueMethod.PERCENT;
        ArrayList<IdentifierValueInputPair> input = new ArrayList<IdentifierValueInputPair>();

        //10000.99
        IdentifierValueInputPair pair1 = new IdentifierValueInputPair(0, new BigDecimal(10000.99));
        input.add(pair1);

        //200.99
        IdentifierValueInputPair pair2 = new IdentifierValueInputPair(1, new BigDecimal(200.99));
        input.add(pair2);

        //1.99
        for (int i = 2; i < 14; i = i + 1) {
            IdentifierValueInputPair pair = new IdentifierValueInputPair(i, new BigDecimal(1.99));
            input.add(pair);
        }

        //theoretical max rounding error: 14*0.0005 = +/- 0.007
        //in this example here we get to -0.005

        //when

        ArrayList<IdentifierValuesOutputObject> output = method.generateKeyValues(input, Rounding.DECIMAL3, false);
        BigDecimal sumRoundedValues = BigDecimal.ZERO;
        for (IdentifierValuesOutputObject object: output) {
            sumRoundedValues = sumRoundedValues.add(object.getRoundedValue());
        }
        BigDecimal sumUnroundedValues = BigDecimal.ZERO;
        for (IdentifierValuesOutputObject object: output) {
            sumUnroundedValues = sumUnroundedValues.add(object.getValue());
        }

        //then
        assertThat(output.size()).isEqualTo(14);
        assertThat(output.get(0).getValue()).isEqualTo(new BigDecimal(97.80097).setScale(5, BigDecimal.ROUND_HALF_UP));
        assertThat(output.get(0).getRoundedValue()).isEqualTo(new BigDecimal(97.801).setScale(3, BigDecimal.ROUND_HALF_UP));
        assertThat(output.get(0).getDelta()).isEqualTo(new BigDecimal(0.00003).setScale(5, BigDecimal.ROUND_HALF_UP));
        assertThat(output.get(0).isCorrected()).isEqualTo(false);

        assertThat(output.get(1).getValue()).isEqualTo(new BigDecimal(1.965507).setScale(6, BigDecimal.ROUND_HALF_UP));
        assertThat(output.get(1).getRoundedValue()).isEqualTo(new BigDecimal(1.966).setScale(3, BigDecimal.ROUND_HALF_UP));
        assertThat(output.get(1).getDelta()).isEqualTo(new BigDecimal(0.000493).setScale(6, BigDecimal.ROUND_HALF_UP));
        assertThat(output.get(1).isCorrected()).isEqualTo(false);

        for (int i = 2; i < 14; i = i + 1) {
            assertThat(output.get(i).getValue()).isEqualTo(new BigDecimal(0.01946047).setScale(8, BigDecimal.ROUND_HALF_UP));
            assertThat(output.get(i).getRoundedValue()).isEqualTo(new BigDecimal(0.019).setScale(3, BigDecimal.ROUND_HALF_UP));
            assertThat(output.get(i).getDelta()).isEqualTo(new BigDecimal(-0.00046047).setScale(8, BigDecimal.ROUND_HALF_UP));
            assertThat(output.get(i).isCorrected()).isEqualTo(false);
        }

        // Rounding Error for 3 decimals
        assertThat(sumRoundedValues.setScale(3, BigDecimal.ROUND_HALF_UP)).isEqualTo(new BigDecimal(99.995).setScale(3, BigDecimal.ROUND_HALF_UP));
        assertThat(sumUnroundedValues.setScale(3, BigDecimal.ROUND_HALF_UP)).isEqualTo(new BigDecimal(100.000).setScale(3, BigDecimal.ROUND_HALF_UP));


        //And when (with correction)
        ArrayList<IdentifierValuesOutputObject> secondOutput = method.generateKeyValues(input, Rounding.DECIMAL3, true);
        BigDecimal sumOfRoundedValues = BigDecimal.ZERO;
        for (IdentifierValuesOutputObject object: secondOutput) {
            sumOfRoundedValues = sumOfRoundedValues.add(object.getRoundedValue());
        }
        BigDecimal sumOfUnroundedValues = BigDecimal.ZERO;
        for (IdentifierValuesOutputObject object: secondOutput) {
            sumOfUnroundedValues = sumOfUnroundedValues.add(object.getValue());
        }

        //then
        assertThat(secondOutput.size()).isEqualTo(14);
        assertThat(secondOutput.get(0).getValue()).isEqualTo(new BigDecimal(97.80097).setScale(5, BigDecimal.ROUND_HALF_UP));
        assertThat(secondOutput.get(0).getRoundedValue()).isEqualTo(new BigDecimal(97.801).setScale(3, BigDecimal.ROUND_HALF_UP));
        assertThat(secondOutput.get(0).getDelta()).isEqualTo(new BigDecimal(0.00003).setScale(5, BigDecimal.ROUND_HALF_UP));
        assertThat(secondOutput.get(0).isCorrected()).isEqualTo(false);

        assertThat(secondOutput.get(1).getValue()).isEqualTo(new BigDecimal(1.965507).setScale(6, BigDecimal.ROUND_HALF_UP));
        assertThat(secondOutput.get(1).getRoundedValue()).isEqualTo(new BigDecimal(1.966).setScale(3, BigDecimal.ROUND_HALF_UP));
        assertThat(secondOutput.get(1).getDelta()).isEqualTo(new BigDecimal(0.000493).setScale(6, BigDecimal.ROUND_HALF_UP));
        assertThat(secondOutput.get(1).isCorrected()).isEqualTo(false);

        for (int i = 2; i < 9; i = i + 1) {
            assertThat(secondOutput.get(i).getValue()).isEqualTo(new BigDecimal(0.01946047).setScale(8, BigDecimal.ROUND_HALF_UP));
            assertThat(secondOutput.get(i).getRoundedValue()).isEqualTo(new BigDecimal(0.019).setScale(3, BigDecimal.ROUND_HALF_UP));
            assertThat(secondOutput.get(i).getDelta()).isEqualTo(new BigDecimal(-0.00046047).setScale(8, BigDecimal.ROUND_HALF_UP));
            assertThat(secondOutput.get(i).isCorrected()).isEqualTo(false);
        }

        for (int i = 9; i < 14; i = i + 1) {
            assertThat(secondOutput.get(i).getValue()).isEqualTo(new BigDecimal(0.01946047).setScale(8, BigDecimal.ROUND_HALF_UP));
            assertThat(secondOutput.get(i).getRoundedValue()).isEqualTo(new BigDecimal(0.020).setScale(3, BigDecimal.ROUND_HALF_UP));
            assertThat(secondOutput.get(i).getDelta()).isEqualTo(new BigDecimal(-0.00046047).setScale(8, BigDecimal.ROUND_HALF_UP));
            assertThat(secondOutput.get(i).isCorrected()).isEqualTo(true);
        }

        // Corrected Rounding Error for 3 decimals
        assertThat(sumOfRoundedValues.setScale(3, BigDecimal.ROUND_HALF_UP)).isEqualTo(new BigDecimal(100.000).setScale(3, BigDecimal.ROUND_HALF_UP));
        assertThat(sumOfUnroundedValues.setScale(3, BigDecimal.ROUND_HALF_UP)).isEqualTo(new BigDecimal(100.000).setScale(3, BigDecimal.ROUND_HALF_UP));


    }

}
