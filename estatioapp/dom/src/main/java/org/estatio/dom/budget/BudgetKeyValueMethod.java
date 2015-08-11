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
package org.estatio.dom.budget;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Iterator;

import org.estatio.app.budget.IdentifierValueInputPair;
import org.estatio.app.budget.IdentifierValuesOutputObject;
import org.estatio.app.budget.Rounding;

public enum BudgetKeyValueMethod {
    PROMILLE {
        @Override
        public BigDecimal calculate(final BigDecimal numerator, final BigDecimal denominator) {
            return numerator.multiply(new BigDecimal(1000), MathContext.DECIMAL32).divide(denominator, MathContext.DECIMAL32);
        }
        @Override
        public boolean isValid(BudgetKeyTable budgetKeyTable) {
                if (!this.keySum(budgetKeyTable).equals(new BigDecimal(1000.000).setScale(3,BigDecimal.ROUND_HALF_UP))) {
                    return false;
                }
            return true;
        }
        @Override
        public BigDecimal keySum(BudgetKeyTable budgetKeyTable) {
            BigDecimal sum = BigDecimal.ZERO;
            for (Iterator<BudgetKeyItem> it = budgetKeyTable.getBudgetKeyItems().iterator(); it.hasNext();) {
                sum = sum.add(it.next().getKeyValue());
            }
            return sum.setScale(3, BigDecimal.ROUND_HALF_UP);
        }
        @Override
        public ArrayList<IdentifierValuesOutputObject> generateKeyValues(
                final ArrayList<IdentifierValueInputPair> input,
                final Rounding rounding,
                final boolean useRoundingErrorCorrection) {

            return generateKeyValuesWithExpectedTotal(input, rounding, useRoundingErrorCorrection, new BigDecimal(1000.000));

        }
    },
    PERCENT {
        @Override
        public BigDecimal calculate(final BigDecimal numerator, final BigDecimal denominator) {
            return numerator.multiply(new BigDecimal(100), MathContext.DECIMAL32).divide(denominator, MathContext.DECIMAL32);
        }
        @Override
        public boolean isValid(BudgetKeyTable budgetKeyTable) {
            if (!this.keySum(budgetKeyTable).equals(new BigDecimal(100.000).setScale(3, BigDecimal.ROUND_HALF_UP))) {
                return false;
            }
            return true;
        }
        @Override
        public BigDecimal keySum(BudgetKeyTable budgetKeyTable) {
            BigDecimal sum = BigDecimal.ZERO;
            for (Iterator<BudgetKeyItem> it = budgetKeyTable.getBudgetKeyItems().iterator(); it.hasNext();) {
                sum = sum.add(it.next().getKeyValue());
            }
            return sum.setScale(3,BigDecimal.ROUND_HALF_UP);
        }
        @Override
        public ArrayList<IdentifierValuesOutputObject> generateKeyValues(
                final ArrayList<IdentifierValueInputPair> input,
                final Rounding rounding,
                final boolean useRoundingErrorCorrection) {

            return generateKeyValuesWithExpectedTotal(input, rounding, useRoundingErrorCorrection, new BigDecimal(100.000));
        }
    },
    DEFAULT {
        @Override
        public BigDecimal calculate(final BigDecimal numerator, final BigDecimal denominator) {
            return numerator.divide(denominator, MathContext.DECIMAL32);
        }
        @Override
        public boolean isValid(BudgetKeyTable budgetKeyTable) {
            return true;
        }
        @Override
        public BigDecimal keySum(BudgetKeyTable budgetKeyTable) {
            BigDecimal sum = BigDecimal.ZERO;
            for (Iterator<BudgetKeyItem> it = budgetKeyTable.getBudgetKeyItems().iterator(); it.hasNext();) {
                sum = sum.add(it.next().getKeyValue());
            }
            return sum;
        }
        @Override
        public ArrayList<IdentifierValuesOutputObject> generateKeyValues(
                final ArrayList<IdentifierValueInputPair> input,
                final Rounding rounding,
                final boolean useRoundingErrorCorrection) {

            // for default method: no active denominator (so set to 1)
            BigDecimal denominator = BigDecimal.ONE;

            // create output object
            ArrayList<IdentifierValuesOutputObject> output = new ArrayList<IdentifierValuesOutputObject>();
            for (IdentifierValueInputPair inputPair : input) {
                BigDecimal keyValue = calculate(inputPair.getValue(), denominator);
                BigDecimal roundedKeyValue = keyValue.setScale(rounding.digits(), BigDecimal.ROUND_HALF_UP);
                IdentifierValuesOutputObject newOutputObject = new IdentifierValuesOutputObject(
                        inputPair.getIdentifier(),
                        keyValue,
                        roundedKeyValue,
                        roundedKeyValue.subtract(keyValue),
                        false
                );
                output.add(newOutputObject);
            }

            return output;
        }
    };

    public abstract BigDecimal calculate(final BigDecimal numerator, final BigDecimal denominator);

    public abstract ArrayList<IdentifierValuesOutputObject> generateKeyValues(
            final ArrayList<IdentifierValueInputPair> input,
            final Rounding rounding,
            final boolean useRoundingErrorCorrection);

    public abstract boolean isValid(final BudgetKeyTable budgetKeyTable);

    public abstract BigDecimal keySum(final BudgetKeyTable budgetKeyTable);

    private enum Delta {
        DELTA_POSITIVE,
        DELTA_NEGATIVE,
        NO_DELTA
    }

    ////////////////////////////////////////////

    ArrayList<IdentifierValuesOutputObject> generateKeyValuesWithExpectedTotal(
            final ArrayList<IdentifierValueInputPair> input,
            final Rounding rounding,
            final boolean useRoundingErrorCorrection,
            final BigDecimal expectedTotalOfKeyValues) {

        // determine denominator (sum of all input values)
        BigDecimal denominator = BigDecimal.ZERO;
        for (IdentifierValueInputPair pair : input) {
            denominator = denominator.add(pair.getValue());
        }

        // create output object
        ArrayList<IdentifierValuesOutputObject> output = new ArrayList<IdentifierValuesOutputObject>();
        for (IdentifierValueInputPair inputPair : input) {
            BigDecimal keyValue = calculate(inputPair.getValue(), denominator);
            BigDecimal roundedKeyValue = keyValue.setScale(rounding.digits(), BigDecimal.ROUND_HALF_UP);
            IdentifierValuesOutputObject newOutputObject = new IdentifierValuesOutputObject(
                    inputPair.getIdentifier(),
                    keyValue,
                    roundedKeyValue,
                    roundedKeyValue.subtract(keyValue),
                    false
            );
            output.add(newOutputObject);
        }

        // if rounding error correction is asked for
        if (useRoundingErrorCorrection) {

            // 1. check if rounding correction is needed
            BigDecimal sumOfCalculatedRoundedValues = BigDecimal.ZERO;
            for (IdentifierValueInputPair inputPair : input) {
                BigDecimal keyValue = calculate(inputPair.getValue(), denominator);
                BigDecimal roundedKeyValue = keyValue.setScale(rounding.digits(), BigDecimal.ROUND_HALF_UP);
                sumOfCalculatedRoundedValues = sumOfCalculatedRoundedValues.add(roundedKeyValue);
            }

            BigDecimal deltaOfSum = BigDecimal.ZERO;
            Delta deltaSignOfSum = Delta.NO_DELTA;
            BigDecimal validTotal = expectedTotalOfKeyValues.setScale(rounding.digits(), BigDecimal.ROUND_HALF_UP);

            if (sumOfCalculatedRoundedValues.compareTo(validTotal) > 0) {
                deltaSignOfSum = Delta.DELTA_POSITIVE;
                deltaOfSum = deltaOfSum.add(sumOfCalculatedRoundedValues.subtract(validTotal));
                    /*debug*/
                System.out.println("***************************");
                System.out.print("positive delta: ");
                System.out.println(deltaOfSum);
                    /*debug*/
            }
            if (sumOfCalculatedRoundedValues.compareTo(validTotal) < 0) {
                deltaSignOfSum = Delta.DELTA_NEGATIVE;
                deltaOfSum = deltaOfSum.add(sumOfCalculatedRoundedValues.subtract(validTotal));
                    /*debug*/
                System.out.println("***************************");
                System.out.print("negative delta: ");
                System.out.println(deltaOfSum);
                    /*debug*/
            }

            // 2. in case of rounding needed: iterate over sorted array until fixed
            Integer numberOfIterationsNeeded = deltaOfSum.abs().multiply(rounding.baseFactor()).intValue();

            for (int i = 0; i < numberOfIterationsNeeded; i = i + 1) {

                if (deltaSignOfSum == Delta.DELTA_NEGATIVE) {

                    //find largest (positive) delta in output Object and round up
                    BigDecimal largestPositiveDelta = new BigDecimal(-1);
                    IdentifierValuesOutputObject objectToRoundUp = null;
                    for (IdentifierValuesOutputObject object : output) {
                        if (object.getDelta().compareTo(largestPositiveDelta) > 0 && !object.isCorrected()) {
                            objectToRoundUp = object;
                        }
                    }
                    objectToRoundUp
                            .setRoundedValue(
                                    objectToRoundUp.getRoundedValue()
                                            .add(rounding.correctionFactor())
                                            .setScale(rounding.digits(), BigDecimal.ROUND_HALF_UP)
                            );
                    deltaOfSum = deltaOfSum.add(rounding.correctionFactor()).setScale(rounding.digits()+3, BigDecimal.ROUND_HALF_UP);
                    objectToRoundUp.setCorrected(true);

                        /*debug*/
                    System.out.print("Identifier: ");
                    System.out.println(objectToRoundUp.getIdentifier());
                    System.out.print("New keyRoundedValue: ");
                    System.out.println(objectToRoundUp.getRoundedValue());
                    System.out.print("New deltaOfSum: ");
                    System.out.println(deltaOfSum);
                        /*debug*/

                } else {

                    //find largest negative delta in output Object and round down
                    BigDecimal largestNegativeDelta = BigDecimal.ONE;
                    IdentifierValuesOutputObject objectToRoundDown = null;
                    for (IdentifierValuesOutputObject object : output) {
                        if (object.getDelta().compareTo(largestNegativeDelta) < 0 && !object.isCorrected()) {
                            objectToRoundDown = object;
                        }
                    }
                    objectToRoundDown
                            .setRoundedValue(
                                    objectToRoundDown.getRoundedValue()
                                            .subtract(rounding.correctionFactor())
                                            .setScale(rounding.digits(), BigDecimal.ROUND_HALF_UP)
                            );
                    deltaOfSum = deltaOfSum.subtract(rounding.correctionFactor()).setScale(rounding.digits()+3, BigDecimal.ROUND_HALF_UP);
                    objectToRoundDown.setCorrected(true);

                        /*debug*/
                    System.out.print("Identifier: ");
                    System.out.println(objectToRoundDown.getIdentifier());
                    System.out.print("New keyRoundedValue: ");
                    System.out.println(objectToRoundDown.getRoundedValue());
                    System.out.print("New deltaOfSum: ");
                    System.out.println(deltaOfSum);
                        /*debug*/

                }

            }

        }

        return output;
    }

    ////////////////////////////////////////////

}
