package org.estatio.dom.budgeting;

import org.estatio.dom.budgeting.Distributable;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

public class DistributionService {

    /**
     * @return a {@link List} of
     * {@link org.estatio.dom.budgeting.Distributable} items with {@link BigDecimal} targetTotal
     * equally distributed over {@link BigDecimal} item.value according to
     * {@link BigDecimal} item.sourceValue.
     *
     * Rounding correction finds place according to {@link int} precision.
     *
     * The original order of List input is preserved.
     *
     * Tested for item.sourceValue >= 0
     */
    public List<Distributable> distribute(
            final List<Distributable> input,
            final BigDecimal targetTotal,
            final int precision){

        // determine denominator (sum of all input values)
        BigDecimal denominator = BigDecimal.ZERO;
        for (Distributable distributable : input) {
            denominator = denominator.add(distributable.getSourceValue(), MathContext.DECIMAL64);
        }

        // Case where no non-zero source values are found and so denominator equals zero
        if (denominator.compareTo(BigDecimal.ZERO) <= 0) {
            for (Distributable inputItem : input) {
                inputItem.setValue(BigDecimal.ZERO);
            }
            return input;
        }

            /*debug*/
//        System.out.println("***************************");
//        System.out.println("denominator: ");
//        System.out.println(denominator);
            /*debug*/

        List<OutputHelper> outputHelperList = new ArrayList<>();
        for (Distributable distributable : input) {

            BigDecimal unroundedTargetValue = distributable.getSourceValue().multiply(targetTotal, MathContext.DECIMAL64).divide(denominator, MathContext.DECIMAL64);
            BigDecimal roundedTargetValue = unroundedTargetValue.setScale(precision, BigDecimal.ROUND_HALF_UP);
            distributable.setValue(roundedTargetValue);
            OutputHelper newOutputObject = new OutputHelper(
                    distributable,
                    roundedTargetValue.subtract(unroundedTargetValue, MathContext.DECIMAL64),
                    false
            );

            outputHelperList.add(newOutputObject);

        }


        // 1. check if rounding correction is needed
        BigDecimal sumOfCalculatedRoundedValues = BigDecimal.ZERO;
        for (OutputHelper helper : outputHelperList) {
            sumOfCalculatedRoundedValues = sumOfCalculatedRoundedValues.add(helper.distributable.getValue(), MathContext.DECIMAL64);
                    /*debug*/
//            System.out.println("***************************");
//            System.out.println(helper.distributable.getValue());
//            System.out.print("sumOfCalculatedRoundedValues: ");
//            System.out.println(sumOfCalculatedRoundedValues);
                    /*debug*/
        }

        BigDecimal deltaOfSum = BigDecimal.ZERO;
        Delta deltaSignOfSum = Delta.NO_DELTA;
        BigDecimal validTotal = targetTotal.setScale(precision, BigDecimal.ROUND_HALF_UP);

        if (sumOfCalculatedRoundedValues.compareTo(validTotal) > 0) {
            deltaSignOfSum = Delta.DELTA_POSITIVE;
            deltaOfSum = deltaOfSum.add(sumOfCalculatedRoundedValues.subtract(validTotal, MathContext.DECIMAL64));
                    /*debug*/
//            System.out.println("***************************");
//            System.out.print("positive delta: ");
//            System.out.println(deltaOfSum);
                    /*debug*/
        }
        if (sumOfCalculatedRoundedValues.compareTo(validTotal) < 0) {
            deltaSignOfSum = Delta.DELTA_NEGATIVE;
            deltaOfSum = deltaOfSum.add(sumOfCalculatedRoundedValues.subtract(validTotal, MathContext.DECIMAL64));
                    /*debug*/
//            System.out.println("***************************");
//            System.out.print("negative delta: ");
//            System.out.println(deltaOfSum);
                    /*debug*/
        }

        // 2. in case of rounding needed: iterate over sorted array until fixed
        Integer numberOfIterationsNeeded = deltaOfSum.abs().multiply(multiplicationFactor(precision)).intValue();

        for (int i = 0; i < numberOfIterationsNeeded; i = i + 1) {

            if (deltaSignOfSum == Delta.DELTA_NEGATIVE) {

                //find largest (positive) delta in output Object and round up
                BigDecimal largestPositiveDelta = new BigDecimal(-1);
                OutputHelper helperToRoundUp = null;
                for (OutputHelper helper : outputHelperList) {
                    if (
                            helper.delta.compareTo(largestPositiveDelta) > 0
                            &&
                                    !helper.corrected
                            &&
                                    !(helper.distributable.getSourceValue().compareTo(BigDecimal.ZERO) == 0)
                            ) {
                        helperToRoundUp = helper;
                    }
                }
                helperToRoundUp
                        .distributable.setValue(
                        helperToRoundUp.distributable.getValue()
                                .add(increment(precision), MathContext.DECIMAL64)
                                .setScale(precision, BigDecimal.ROUND_HALF_UP)
                );
                deltaOfSum = deltaOfSum.add(increment(precision)).setScale(precision +3, BigDecimal.ROUND_HALF_UP);
                helperToRoundUp.setCorrected(true);

                        /*debug*/
//                System.out.print("Identifier: ");
//                System.out.println(helperToRoundUp.distributable.getSourceValue());
//                System.out.print("New keyRoundedValue: ");
//                System.out.println(helperToRoundUp.distributable.getValue());
//                System.out.print("New deltaOfSum: ");
//                System.out.println(deltaOfSum);
                        /*debug*/

            } else {

                //find largest negative delta in output Object and round down
                BigDecimal largestNegativeDelta = BigDecimal.ONE;
                OutputHelper helperToRoundDown = null;
                for (OutputHelper helper : outputHelperList) {
                    if (
                            helper.delta.compareTo(largestNegativeDelta) < 0
                            &&
                                    !helper.corrected
                            &&
                                    !(helper.distributable.getSourceValue().compareTo(BigDecimal.ZERO) == 0)
                            ) {
                        helperToRoundDown = helper;
                    }
                }
                helperToRoundDown.
                        distributable.setValue(
                        helperToRoundDown.distributable.getValue()
                                .subtract(increment(precision), MathContext.DECIMAL64)
                                .setScale(precision, BigDecimal.ROUND_HALF_UP)
                );
                deltaOfSum = deltaOfSum.subtract(increment(precision)).setScale(precision +3, BigDecimal.ROUND_HALF_UP);
                helperToRoundDown.setCorrected(true);

                        /*debug*/
//                System.out.print("Identifier: ");
//                System.out.println(helperToRoundDown.distributable.getSourceValue());
//                System.out.print("New keyRoundedValue: ");
//                System.out.println(helperToRoundDown.distributable.getValue());
//                System.out.print("New deltaOfSum: ");
//                System.out.println(deltaOfSum);
                        /*debug*/

            }

        }

        ArrayList<Distributable> output = new ArrayList<Distributable>();

        for (OutputHelper helper : outputHelperList) {
            output.add(helper.distributable);
        }

        return output;
    }

    private BigDecimal multiplicationFactor(final int scale) {
        return new BigDecimal(10).pow(scale, MathContext.DECIMAL64);
    }

    private BigDecimal increment(final int scale) {
        return BigDecimal.valueOf(0.1).pow(scale, MathContext.DECIMAL64);
    }

    private class OutputHelper {

        OutputHelper(
                final Distributable distributable,
                final BigDecimal delta,
                final boolean corrected) {
            this.distributable = distributable;
            this.delta = delta;
            this.corrected = corrected;
        }

        private Distributable distributable;

        private BigDecimal delta;

        private boolean corrected;

        public void setCorrected(final boolean corrected){
            this.corrected = corrected;
        }

    }

    private enum Delta {
        DELTA_POSITIVE,
        DELTA_NEGATIVE,
        NO_DELTA
    }

}
