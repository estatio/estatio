package org.estatio.dom.budgeting;

import org.assertj.core.api.Assertions;
import org.estatio.dom.budgeting.keyitem.KeyItem;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jodo on 11/08/15.
 */
public class DistributionServiceTest {

    @Test
    public void generateKeyValuesRoundingBy3DecimalsNegativeDeltaPromille() {

        //given
        DistributionService distributionService = new DistributionService();
        List<Distributable> input = new ArrayList<>();

        //10000.99
        KeyItem item1 = new KeyItem();
        item1.setSourceValue(new BigDecimal(10000.99));
        input.add(item1);

        //0.99
        KeyItem item2 = new KeyItem();
        item2.setSourceValue(new BigDecimal(0.99));
        input.add(item2);

        //1.99
        for (int i = 2; i < 14; i = i + 1) {
            KeyItem item = new KeyItem();
            item.setSourceValue(new BigDecimal(1.99));
            input.add(item);
        }

        //theoretical max rounding error: 14*0.0005 = +/- 0.007
        //in this example here we get to -0.006

        //when
        List<Distributable> output = distributionService.distribute(input, new BigDecimal(1000), 3);
        BigDecimal sumRoundedValues = BigDecimal.ZERO;
        for (Distributable object: output) {
            sumRoundedValues = sumRoundedValues.add(object.getValue());
        }

        //then
        Assertions.assertThat(output.size()).isEqualTo(14);
        Assertions.assertThat(output.get(0).getValue()).isEqualTo(new BigDecimal(997.519).setScale(3, BigDecimal.ROUND_HALF_UP));
        Assertions.assertThat(output.get(1).getValue()).isEqualTo(new BigDecimal(0.099).setScale(3, BigDecimal.ROUND_HALF_UP));

        for (int i = 2; i < 8; i = i + 1) {
            Assertions.assertThat(output.get(i).getValue()).isEqualTo(new BigDecimal(0.198).setScale(3, BigDecimal.ROUND_HALF_UP));
        }

        for (int i = 8; i < 14; i = i + 1) {
            Assertions.assertThat(output.get(i).getValue()).isEqualTo(new BigDecimal(0.199).setScale(3, BigDecimal.ROUND_HALF_UP));
        }

        Assertions.assertThat(sumRoundedValues.setScale(3, BigDecimal.ROUND_HALF_UP)).isEqualTo(new BigDecimal(1000.000).setScale(3, BigDecimal.ROUND_HALF_UP));

    }

    @Test
    public void generateKeyValuesRoundingBy3DecimalsPositiveDelta() {

        //given
        DistributionService distributionService = new DistributionService();
        List<Distributable> input = new ArrayList<>();

        //10000.99
        KeyItem item1 = new KeyItem();
        item1.setSourceValue(new BigDecimal(10000.99));
        input.add(item1);

        //100.01
        KeyItem item2 = new KeyItem();
        item2.setSourceValue(new BigDecimal(100.01));
        input.add(item2);

        //1.99
        for (int i = 2; i < 14; i = i + 1) {
            KeyItem item = new KeyItem();
            item.setSourceValue(new BigDecimal(1.99));
            input.add(item);
        }

        //theoretical max rounding error: 14*0.0005 = +/- 0.007
        //in this example here we get to + 0.006

        //when
        List<Distributable> output = distributionService.distribute(input, new BigDecimal(1000), 3);
        BigDecimal sumRoundedValues = BigDecimal.ZERO;
        for (Distributable object: output) {
            sumRoundedValues = sumRoundedValues.add(object.getValue());
        }

        //then
        Assertions.assertThat(output.size()).isEqualTo(14);
        Assertions.assertThat(output.get(0).getValue()).isEqualTo(new BigDecimal(987.764).setScale(3, BigDecimal.ROUND_HALF_UP));
        Assertions.assertThat(output.get(1).getValue()).isEqualTo(new BigDecimal(9.878).setScale(3, BigDecimal.ROUND_HALF_UP));

        for (int i = 2; i < 8; i = i + 1) {
            Assertions.assertThat(output.get(i).getValue()).isEqualTo(new BigDecimal(0.197).setScale(3, BigDecimal.ROUND_HALF_UP));
        }

        for (int i = 8; i < 14; i = i + 1) {
            Assertions.assertThat(output.get(i).getValue()).isEqualTo(new BigDecimal(0.196).setScale(3, BigDecimal.ROUND_HALF_UP));
        }

        Assertions.assertThat(sumRoundedValues.setScale(3, BigDecimal.ROUND_HALF_UP)).isEqualTo(new BigDecimal(1000.000).setScale(3, BigDecimal.ROUND_HALF_UP));

    }

    @Test
    public void generateKeyValuesRoundingBy2DecimalsNegativeDelta() {

        //given
        DistributionService distributionService = new DistributionService();
        List<Distributable> input = new ArrayList<>();

        //10000.99
        KeyItem item1 = new KeyItem();
        item1.setSourceValue(new BigDecimal(10000.99));
        input.add(item1);

        //200.09
        KeyItem item2 = new KeyItem();
        item2.setSourceValue(new BigDecimal(200.09));
        input.add(item2);

        //1.99
        for (int i = 2; i < 14; i = i + 1) {
            KeyItem item = new KeyItem();
            item.setSourceValue(new BigDecimal(1.99));
            input.add(item);
        }

        //theoretical max rounding error: 14*0.005 = +/-0.07
        //in this example here we get to -0.05

        //when
        List<Distributable> output = distributionService.distribute(input, new BigDecimal(1000), 2);
        BigDecimal sumRoundedValues = BigDecimal.ZERO;
        for (Distributable object: output) {
            sumRoundedValues = sumRoundedValues.add(object.getValue(), MathContext.DECIMAL32);
        }

        //then
        Assertions.assertThat(output.size()).isEqualTo(14);
        Assertions.assertThat(output.get(0).getValue()).isEqualTo(new BigDecimal(978.10).setScale(2, BigDecimal.ROUND_HALF_UP));
        Assertions.assertThat(output.get(1).getValue()).isEqualTo(new BigDecimal(19.57).setScale(2, BigDecimal.ROUND_HALF_UP));

        for (int i = 2; i < 9; i = i + 1) {
            Assertions.assertThat(output.get(i).getValue()).isEqualTo(new BigDecimal(0.19).setScale(2, BigDecimal.ROUND_HALF_UP));
        }

        for (int i = 9; i < 14; i = i + 1) {
            Assertions.assertThat(output.get(i).getValue()).isEqualTo(new BigDecimal(0.20).setScale(2, BigDecimal.ROUND_HALF_UP));
        }

        Assertions.assertThat(sumRoundedValues.setScale(2, BigDecimal.ROUND_HALF_UP)).isEqualTo(new BigDecimal(1000.00).setScale(2, BigDecimal.ROUND_HALF_UP));
    }

    @Test
    public void generateKeyValuesRoundingBy2DecimalsPositiveDelta() {

        //given
        DistributionService distributionService = new DistributionService();
        List<Distributable> input = new ArrayList<>();

        //10000.99
        KeyItem item1 = new KeyItem();
        item1.setSourceValue(new BigDecimal(10000.99));
        input.add(item1);

        //100.09
        KeyItem item2 = new KeyItem();
        item2.setSourceValue(new BigDecimal(100.09));
        input.add(item2);

        //1.99
        for (int i = 2; i < 14; i = i + 1) {
            KeyItem item = new KeyItem();
            item.setSourceValue(new BigDecimal(1.99));
            input.add(item);
        }

        //theoretical max rounding error: 14*0.005 = +/- 0.07
        //in this example here we get to + 0.05

        //when
        List<Distributable> output = distributionService.distribute(input, new BigDecimal(1000), 2);
        BigDecimal sumRoundedValues = BigDecimal.ZERO;
        for (Distributable object: output) {
            sumRoundedValues = sumRoundedValues.add(object.getValue(), MathContext.DECIMAL32);
        }

        //then
        Assertions.assertThat(output.size()).isEqualTo(14);
        Assertions.assertThat(output.get(0).getValue()).isEqualTo(new BigDecimal(987.76).setScale(2, BigDecimal.ROUND_HALF_UP));
        Assertions.assertThat(output.get(1).getValue()).isEqualTo(new BigDecimal(9.89).setScale(2, BigDecimal.ROUND_HALF_UP));

        for (int i = 2; i < 9; i = i + 1) {
            Assertions.assertThat(output.get(i).getValue()).isEqualTo(new BigDecimal(0.20).setScale(2, BigDecimal.ROUND_HALF_UP));
        }

        for (int i = 9; i < 14; i = i + 1) {
            Assertions.assertThat(output.get(i).getValue()).isEqualTo(new BigDecimal(0.19).setScale(2, BigDecimal.ROUND_HALF_UP));
        }

        Assertions.assertThat(sumRoundedValues.setScale(2, BigDecimal.ROUND_HALF_UP)).isEqualTo(new BigDecimal(1000.00).setScale(2, BigDecimal.ROUND_HALF_UP));

    }

    @Test
    public void generateKeyValuesRoundingBy3DecimalsPositiveDeltaPercent() {

        //given
        DistributionService distributionService = new DistributionService();
        List<Distributable> input = new ArrayList<>();

        //10000.99
        KeyItem item1 = new KeyItem();
        item1.setSourceValue(new BigDecimal(10000.99));
        input.add(item1);

        //0.99
        KeyItem item2 = new KeyItem();
        item2.setSourceValue(new BigDecimal(0.99));
        input.add(item2);

        //1.99
        for (int i = 2; i < 14; i = i + 1) {
            KeyItem item = new KeyItem();
            item.setSourceValue(new BigDecimal(1.99));
            input.add(item);
        }

        //theoretical max rounding error: 14*0.0005 = +/- 0.007
        //in this example here we get to 0.002

        //when
        List<Distributable> output = distributionService.distribute(input, new BigDecimal(100), 3);
        BigDecimal sumRoundedValues = BigDecimal.ZERO;
        for (Distributable object: output) {
            sumRoundedValues = sumRoundedValues.add(object.getValue(), MathContext.DECIMAL64);
        }

        //then

        Assertions.assertThat(output.size()).isEqualTo(14);
        Assertions.assertThat(output.get(0).getValue()).isEqualTo(new BigDecimal(99.752).setScale(3, BigDecimal.ROUND_HALF_UP));
        Assertions.assertThat(output.get(1).getValue()).isEqualTo(new BigDecimal(0.010).setScale(3, BigDecimal.ROUND_HALF_UP));

        for (int i = 2; i < 12; i = i + 1) {
            Assertions.assertThat(output.get(i).getValue()).isEqualTo(new BigDecimal(0.020).setScale(3, BigDecimal.ROUND_HALF_UP));
        }

        for (int i = 12; i < 14; i = i + 1) {
            Assertions.assertThat(output.get(i).getValue()).isEqualTo(new BigDecimal(0.019).setScale(3, BigDecimal.ROUND_HALF_UP));
        }

        // Corrected Rounding Error for 3 decimals
        Assertions.assertThat(sumRoundedValues.setScale(3, BigDecimal.ROUND_HALF_UP)).isEqualTo(new BigDecimal(100.000).setScale(3, BigDecimal.ROUND_HALF_UP));

    }

    @Test
    public void generateKeyValuesRoundingBy3DecimalsNegativeDeltaPercent() {

        //given
        DistributionService distributionService = new DistributionService();
        List<Distributable> input = new ArrayList<>();

        //10000.99
        KeyItem item1 = new KeyItem();
        item1.setSourceValue(new BigDecimal(10000.99));
        input.add(item1);

        //200.99
        KeyItem item2 = new KeyItem();
        item2.setSourceValue(new BigDecimal(200.99));
        input.add(item2);

        //1.99
        for (int i = 2; i < 14; i = i + 1) {
            KeyItem item = new KeyItem();
            item.setSourceValue(new BigDecimal(1.99));
            input.add(item);
        }

        //theoretical max rounding error: 14*0.0005 = +/- 0.007
        //in this example here we get to -0.005

        //when
        List<Distributable> output = distributionService.distribute(input, new BigDecimal(100), 3);
        BigDecimal sumRoundedValues = BigDecimal.ZERO;
        for (Distributable object: output) {
            sumRoundedValues = sumRoundedValues.add(object.getValue(), MathContext.DECIMAL64);
        }

        //then

        Assertions.assertThat(output.size()).isEqualTo(14);
        Assertions.assertThat(output.get(0).getValue()).isEqualTo(new BigDecimal(97.801).setScale(3, BigDecimal.ROUND_HALF_UP));
        Assertions.assertThat(output.get(1).getValue()).isEqualTo(new BigDecimal(1.966).setScale(3, BigDecimal.ROUND_HALF_UP));

        for (int i = 2; i < 9; i = i + 1) {
            Assertions.assertThat(output.get(i).getValue()).isEqualTo(new BigDecimal(0.019).setScale(3, BigDecimal.ROUND_HALF_UP));
        }

        for (int i = 9; i < 14; i = i + 1) {
            Assertions.assertThat(output.get(i).getValue()).isEqualTo(new BigDecimal(0.020).setScale(3, BigDecimal.ROUND_HALF_UP));
        }

        // Corrected Rounding Error for 3 decimals
        Assertions.assertThat(sumRoundedValues.setScale(3, BigDecimal.ROUND_HALF_UP)).isEqualTo(new BigDecimal(100.000).setScale(3, BigDecimal.ROUND_HALF_UP));
    }

    @Test
    public void generateKeyValuesRoundingBy6DecimalsPositiveDeltaPercent() {

        //given
        DistributionService distributionService = new DistributionService();
        List<Distributable> input = new ArrayList<>();

        for (int i = 2; i <= 25; i = i + 1) {
            KeyItem item = new KeyItem();
            item.setSourceValue(new BigDecimal(i).multiply(new BigDecimal(100).setScale(2)));
            input.add(item);
        }

        //when
        List<Distributable> output = distributionService.distribute(input, new BigDecimal(1000), 6);
        BigDecimal sumRoundedValues = BigDecimal.ZERO;
        for (Distributable object: output) {
            sumRoundedValues = sumRoundedValues.add(object.getValue(), MathContext.DECIMAL64);
        }

        Assertions.assertThat(output.size()).isEqualTo(24);
        Assertions.assertThat(sumRoundedValues.setScale(6, BigDecimal.ROUND_HALF_UP)).isEqualTo(new BigDecimal(1000).setScale(6, BigDecimal.ROUND_HALF_UP));

    }

    @Test
    public void generateKeyValuesRoundingBy6DecimalsPositiveDeltaPercent2() {

        //given
        DistributionService distributionService = new DistributionService();
        List<Distributable> input = new ArrayList<>();

        for (int i = 1; i <= 25; i = i + 1) {
            KeyItem item = new KeyItem();
            item.setSourceValue(new BigDecimal(i).multiply(new BigDecimal(100).setScale(2)));
            input.add(item);
        }

        //when
        List<Distributable> output = distributionService.distribute(input, new BigDecimal(1000, MathContext.DECIMAL64), 5);
        BigDecimal sumRoundedValues = BigDecimal.ZERO;
        for (Distributable object: output) {
            sumRoundedValues = sumRoundedValues.add(object.getValue(), MathContext.DECIMAL64);
        }

        Assertions.assertThat(output.size()).isEqualTo(25);
        Assertions.assertThat(sumRoundedValues.setScale(5, BigDecimal.ROUND_HALF_UP)).isEqualTo(new BigDecimal(1000).setScale(5, BigDecimal.ROUND_HALF_UP));

    }


    @Test
    public void generateKeyValuesRoundingBy6DecimalsPositiveDelta() {

        //given
        DistributionService distributionService = new DistributionService();
        List<Distributable> input = new ArrayList<>();

        //10000.99 1 item
        KeyItem item1 = new KeyItem();
        item1.setSourceValue(new BigDecimal(10000.99));
        input.add(item1);

        //0.99 1 item
        KeyItem item2 = new KeyItem();
        item2.setSourceValue(new BigDecimal(0.99));
        input.add(item2);

        //1.99 10 items
        for (int i = 1; i < 11; i = i + 1) {
            KeyItem item = new KeyItem();
            item.setSourceValue(new BigDecimal(1.99));
            input.add(item);
        }

        //0 2 items
        for (int i = 11; i < 13; i = i + 1) {
            KeyItem item = new KeyItem();
            item.setSourceValue(BigDecimal.ZERO);
            input.add(item);
        }


        //theoretical max rounding error: 14*0.0000005 = +/- 0.000007
        //in this example here we get to + 0.000005

        //when
        List<Distributable> output = distributionService.distribute(input, new BigDecimal(1000), 6);
        BigDecimal sumRoundedValues = BigDecimal.ZERO;
        for (Distributable object: output) {
            sumRoundedValues = sumRoundedValues.add(object.getValue());
        }

        //then
        Assertions.assertThat(output.size()).isEqualTo(14);
        Assertions.assertThat(output.get(0).getValue()).isEqualTo(new BigDecimal(997.915561).setScale(6, BigDecimal.ROUND_HALF_UP));
        Assertions.assertThat(output.get(1).getValue()).isEqualTo(new BigDecimal(0.098784).setScale(6, BigDecimal.ROUND_HALF_UP));

        // 5 items not rounded
        for (int i = 2; i < 7; i = i + 1) {
            Assertions.assertThat(output.get(i).getValue()).isEqualTo(new BigDecimal(0.198566).setScale(6, BigDecimal.ROUND_HALF_UP));
        }

        // 5 items rounded down
        for (int i = 7; i < 12; i = i + 1) {
            Assertions.assertThat(output.get(i).getValue()).isEqualTo(new BigDecimal(0.198565).setScale(6, BigDecimal.ROUND_HALF_UP));
        }

        for (int i = 12; i < 13; i = i + 1) {
            Assertions.assertThat(output.get(i).getValue()).isEqualTo(BigDecimal.ZERO.setScale(6, BigDecimal.ROUND_HALF_UP));
        }

        Assertions.assertThat(sumRoundedValues.setScale(6, BigDecimal.ROUND_HALF_UP)).isEqualTo(new BigDecimal(1000.000000).setScale(6, BigDecimal.ROUND_HALF_UP));

    }

    @Test
    public void roundingTest() {

        BigDecimal one = new BigDecimal(86.15385).setScale(5, BigDecimal.ROUND_HALF_UP);
        BigDecimal two = new BigDecimal(800).multiply(new BigDecimal(1000), MathContext.DECIMAL64).divide(new BigDecimal(32500), MathContext.DECIMAL64).setScale(5, BigDecimal.ROUND_HALF_UP);
        BigDecimal sum = one.add(two, MathContext.DECIMAL64).setScale(5, BigDecimal.ROUND_HALF_UP);

        Assertions.assertThat(sum).isEqualTo(new BigDecimal(110.76923).setScale(5, BigDecimal.ROUND_HALF_UP));

    }

    @Test
    public void justZeroSourceValues() {

        //given
        DistributionService distributionService = new DistributionService();
        List<Distributable> input = new ArrayList<>();

        for (int i = 1; i <= 5; i = i + 1) {
            KeyItem item = new KeyItem();
            item.setSourceValue(BigDecimal.ZERO);
            input.add(item);
        }

        //when
        List<Distributable> output = distributionService.distribute(input, BigDecimal.ONE, 3);
        BigDecimal sumRoundedValues = BigDecimal.ZERO;
        for (Distributable object: output) {
            sumRoundedValues = sumRoundedValues.add(object.getValue(), MathContext.DECIMAL64);
        }

        //then
        Assertions.assertThat(output.size()).isEqualTo(5);
        Assertions.assertThat(sumRoundedValues.setScale(3, BigDecimal.ROUND_HALF_UP)).isEqualTo(BigDecimal.ZERO.setScale(3, BigDecimal.ROUND_HALF_UP));
    }

    @Test
    public void oneNonZeroSourceValue() {

        //given
        DistributionService distributionService = new DistributionService();
        List<Distributable> input = new ArrayList<>();

        for (int i = 1; i <= 5; i = i + 1) {
            KeyItem item = new KeyItem();
            item.setSourceValue(BigDecimal.ZERO);
            input.add(item);
        }
        KeyItem item = new KeyItem();
        item.setSourceValue(BigDecimal.ONE);
        input.add(item);

        //when
        List<Distributable> output = distributionService.distribute(input, BigDecimal.ONE, 3);
        BigDecimal sumRoundedValues = BigDecimal.ZERO;
        for (Distributable object: output) {
            sumRoundedValues = sumRoundedValues.add(object.getValue(), MathContext.DECIMAL64);
        }

        //then
        Assertions.assertThat(output.size()).isEqualTo(6);
        Assertions.assertThat(sumRoundedValues.setScale(3, BigDecimal.ROUND_HALF_UP)).isEqualTo(BigDecimal.ONE.setScale(3, BigDecimal.ROUND_HALF_UP));
    }

    @Test
    public void originalOrderPreserved() {

        //given
        DistributionService distributionService = new DistributionService();
        List<Distributable> input = new ArrayList<>();

        for (int i = 0; i <= 25; i = i + 1) {
            KeyItem item = new KeyItem();
            BigDecimal sourceValue = new BigDecimal(i).multiply(new BigDecimal(100).setScale(2));
            item.setSourceValue(sourceValue);
            input.add(item);
        }

        //when
        List<Distributable> output = distributionService.distribute(input, BigDecimal.ONE, 3);

        //then
        int i = 0;
        for (Distributable outputItem : output) {
            BigDecimal sourceValue = new BigDecimal(i).multiply(new BigDecimal(100).setScale(2));
            Assertions.assertThat(outputItem.getSourceValue()).isEqualTo(sourceValue);
            i ++;
        }

    }

    @Test
    public void noDeltaAssignedToItemsWithZeroSourceValue() {

        //given
        DistributionService distributionService = new DistributionService();
        List<Distributable> input = new ArrayList<>();

        //10000.99
        KeyItem item1 = new KeyItem();
        item1.setSourceValue(new BigDecimal(10000.99));
        input.add(item1);

        //0.99
        KeyItem item2 = new KeyItem();
        item2.setSourceValue(new BigDecimal(0.99));
        input.add(item2);

        //1.99
        for (int i = 2; i < 11; i = i + 1) {
            KeyItem item = new KeyItem();
            item.setSourceValue(new BigDecimal(1.99));
            input.add(item);
        }

        //0
        for (int i = 11; i < 14; i = i + 1) {
            KeyItem item = new KeyItem();
            item.setSourceValue(BigDecimal.ZERO);
            input.add(item);
        }

        //theoretical max rounding error: 14*0.0005 = +/- 0.007
        //in this example here we get to +0.004

        //when
        List<Distributable> output = distributionService.distribute(input, new BigDecimal(1000), 3);
        BigDecimal sumRoundedValues = BigDecimal.ZERO;
        for (Distributable object: output) {
            sumRoundedValues = sumRoundedValues.add(object.getValue());
        }

        //then
        Assertions.assertThat(output.size()).isEqualTo(14);
        Assertions.assertThat(output.get(0).getValue()).isEqualTo(new BigDecimal(998.114).setScale(3, BigDecimal.ROUND_HALF_UP));
        Assertions.assertThat(output.get(1).getValue()).isEqualTo(new BigDecimal(0.099).setScale(3, BigDecimal.ROUND_HALF_UP));

        for (int i = 2; i < 7; i = i + 1) {
            Assertions.assertThat(output.get(i).getValue()).isEqualTo(new BigDecimal(0.199).setScale(3, BigDecimal.ROUND_HALF_UP));
        }

        for (int i = 7; i < 11; i = i + 1) {
            Assertions.assertThat(output.get(i).getValue()).isEqualTo(new BigDecimal(0.198).setScale(3, BigDecimal.ROUND_HALF_UP));
        }

        for (int i = 11; i < 14; i = i + 1) {
            Assertions.assertThat(output.get(i).getValue()).isEqualTo(new BigDecimal(0.000).setScale(3, BigDecimal.ROUND_HALF_UP));
        }

        Assertions.assertThat(sumRoundedValues.setScale(3, BigDecimal.ROUND_HALF_UP)).isEqualTo(new BigDecimal(1000.000).setScale(3, BigDecimal.ROUND_HALF_UP));

    }


}
