package org.estatio.dom.budgeting;

import org.assertj.core.api.Assertions;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.budgeting.keyitem.KeyItem;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.estatio.dom.budgeting.keytable.KeyTableForTesting;
import org.estatio.dom.budgeting.keytable.KeyValueMethod;
import org.estatio.dom.budgeting.schedule.Schedule;
import org.estatio.dom.budgeting.scheduleitem.ScheduleItem;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jodo on 11/08/15.
 */
public class BudgetCalculationContributionServicesTest {

    Unit unit1 = new UnitForTesting();
    Unit unit2 = new UnitForTesting();
    Unit unit3 = new UnitForTesting();;


    @Test
    public void distributionOverUnitsForScheduleItemTest() {

        //given
        BudgetCalculationContributionServices service = new BudgetCalculationContributionServices();
        ScheduleItem scheduleItem = setupScheduleItemForTest("keytable1");

        //when
        List<BudgetCalculation> budgetCalculations = service.distributionOverUnits(scheduleItem);

        //then
        Assertions.assertThat(budgetCalculations.size()).isEqualTo(3);
        Assertions.assertThat(budgetCalculations.get(0).getValue()).isEqualTo(BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP));
        Assertions.assertThat(budgetCalculations.get(0).getUnit()).isEqualTo(unit1);
        Assertions.assertThat(budgetCalculations.get(1).getValue()).isEqualTo(new BigDecimal(333.33).setScale(2,BigDecimal.ROUND_HALF_UP));
        Assertions.assertThat(budgetCalculations.get(1).getUnit()).isEqualTo(unit2);
        Assertions.assertThat(budgetCalculations.get(2).getValue()).isEqualTo(new BigDecimal(666.67).setScale(2,BigDecimal.ROUND_HALF_UP));
        Assertions.assertThat(budgetCalculations.get(2).getUnit()).isEqualTo(unit3);


    }

    @Test
    public void distributionOverUnitsForScheduleItemTest98Percent() {

        //given
        BudgetCalculationContributionServices service = new BudgetCalculationContributionServices();
        ScheduleItem scheduleItem = setupScheduleItemForTest("keytable1");
        scheduleItem.setPercentage(new BigDecimal(98).setScale(2));

        //when
        List<BudgetCalculation> budgetCalculations = service.distributionOverUnits(scheduleItem);

        //then
        Assertions.assertThat(budgetCalculations.size()).isEqualTo(3);
        Assertions.assertThat(budgetCalculations.get(0).getValue()).isEqualTo(BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP));
        Assertions.assertThat(budgetCalculations.get(0).getUnit()).isEqualTo(unit1);
        Assertions.assertThat(budgetCalculations.get(1).getValue()).isEqualTo(new BigDecimal(326.67).setScale(2,BigDecimal.ROUND_HALF_UP));
        Assertions.assertThat(budgetCalculations.get(1).getUnit()).isEqualTo(unit2);
        Assertions.assertThat(budgetCalculations.get(2).getValue()).isEqualTo(new BigDecimal(653.33).setScale(2,BigDecimal.ROUND_HALF_UP));
        Assertions.assertThat(budgetCalculations.get(2).getUnit()).isEqualTo(unit3);


    }

    @Test
    public void distributionOverUnitsForScheduleItemTest0Percent() {

        //given
        BudgetCalculationContributionServices service = new BudgetCalculationContributionServices();
        ScheduleItem scheduleItem = setupScheduleItemForTest("keytable1");
        scheduleItem.setPercentage(BigDecimal.ZERO.setScale(2));

        //when
        List<BudgetCalculation> budgetCalculations = service.distributionOverUnits(scheduleItem);

        //then
        Assertions.assertThat(budgetCalculations.size()).isEqualTo(3);
        Assertions.assertThat(budgetCalculations.get(0).getValue()).isEqualTo(BigDecimal.ZERO);
        Assertions.assertThat(budgetCalculations.get(0).getUnit()).isEqualTo(unit1);
        Assertions.assertThat(budgetCalculations.get(1).getValue()).isEqualTo(BigDecimal.ZERO);
        Assertions.assertThat(budgetCalculations.get(1).getUnit()).isEqualTo(unit2);
        Assertions.assertThat(budgetCalculations.get(2).getValue()).isEqualTo(BigDecimal.ZERO);
        Assertions.assertThat(budgetCalculations.get(2).getUnit()).isEqualTo(unit3);


    }


    @Test
    public void distributionOverUnitsWithKeySumKeyTableIsZero() {

        //given
        BudgetCalculationContributionServices service = new BudgetCalculationContributionServices();
        ScheduleItem scheduleItem = setupScheduleItemWithKeySumZero();

        //when
        List<BudgetCalculation> budgetCalculations = service.distributionOverUnits(scheduleItem);

        //then budgetCalculations does not throw java.lang.ArithmeticException: Division undefined
        Assertions.assertThat(budgetCalculations.size()).isEqualTo(1);
        Assertions.assertThat(budgetCalculations.get(0).getValue()).isEqualTo(BigDecimal.ZERO);
        Assertions.assertThat(budgetCalculations.get(0).getUnit()).isEqualTo(unit1);

    }

    @Test
    public void distributionOverUnitsForScheduleTest() {

        //given
        BudgetCalculationContributionServices service = new BudgetCalculationContributionServices();
        Schedule schedule = new Schedule();

        ScheduleItem scheduleItem1 = setupScheduleItemForTest("keytable1");
        scheduleItem1.setPercentage(new BigDecimal(100).setScale(2));
        scheduleItem1.setSchedule(schedule);
        schedule.getScheduleItems().add(scheduleItem1);

        ScheduleItem scheduleItem2 = setupScheduleItemForTest("keytable2");
        scheduleItem2.setPercentage(new BigDecimal(98).setScale(2));
        scheduleItem2.setSchedule(schedule);
        schedule.getScheduleItems().add(scheduleItem2);

        // when
        List<BudgetCalculation> budgetCalculations = service.distributionOverUnits(schedule);

        // then
        Assertions.assertThat(budgetCalculations.size()).isEqualTo(3);
        Assertions.assertThat(budgetCalculations.get(0).getValue()).isEqualTo(BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP));
        Assertions.assertThat(budgetCalculations.get(1).getValue()).isEqualTo(new BigDecimal(660.00).setScale(2,BigDecimal.ROUND_HALF_UP));
        Assertions.assertThat(budgetCalculations.get(1).getUnit()).isEqualTo(unit2);
        Assertions.assertThat(budgetCalculations.get(2).getValue()).isEqualTo(new BigDecimal(1320.00).setScale(2,BigDecimal.ROUND_HALF_UP));
        Assertions.assertThat(budgetCalculations.get(2).getUnit()).isEqualTo(unit3);

    }

    @Test
    public void mergeTest() {

        //given
        BudgetCalculationContributionServices service = new BudgetCalculationContributionServices();

        List<BudgetCalculation> list1 = new ArrayList<>();
        List<BudgetCalculation> list2 = new ArrayList<>();

        BudgetCalculation budgetCalculation1 = new BudgetCalculation(unit1, new BigDecimal(1), new BigDecimal(10));
        BudgetCalculation budgetCalculation2 = new BudgetCalculation(unit2, new BigDecimal(2), new BigDecimal(20));
        BudgetCalculation budgetCalculation3 = new BudgetCalculation(unit3, new BigDecimal(3), new BigDecimal(30));

        // list1
        list1.add(budgetCalculation1);
        list1.add(budgetCalculation2);

        //list2
        list2.add(budgetCalculation1);
        list2.add(budgetCalculation2);
        list2.add(budgetCalculation3);

        //when
        List<BudgetCalculation> mergedList = service.merge(list1, list2);

        //then
        Assertions.assertThat(mergedList.size()).isEqualTo(3);
        Assertions.assertThat(mergedList.get(0).getUnit()).isEqualTo(unit1);
        Assertions.assertThat(mergedList.get(0).getValue()).isEqualTo(new BigDecimal(2));
        Assertions.assertThat(mergedList.get(0).getSourceValue()).isEqualTo(new BigDecimal(20));
        Assertions.assertThat(mergedList.get(1).getUnit()).isEqualTo(unit2);
        Assertions.assertThat(mergedList.get(1).getValue()).isEqualTo(new BigDecimal(4));
        Assertions.assertThat(mergedList.get(1).getSourceValue()).isEqualTo(new BigDecimal(40));
        Assertions.assertThat(mergedList.get(2).getUnit()).isEqualTo(unit3);
        Assertions.assertThat(mergedList.get(2).getValue()).isEqualTo(new BigDecimal(3));
        Assertions.assertThat(mergedList.get(2).getSourceValue()).isEqualTo(new BigDecimal(30));

    }


    private ScheduleItem setupScheduleItemForTest(String keytableName) {

        BigDecimal percentage = new BigDecimal(100);
        BigDecimal budgetedValue = new BigDecimal(1000).setScale(2);

        // setup keyItems
        List<KeyItem> keyItems= new ArrayList<>();
        keyItems.add(setupKeyItem(unit1, BigDecimal.ZERO));
        keyItems.add(setupKeyItem(unit2, new BigDecimal(1)));
        keyItems.add(setupKeyItem(unit3, new BigDecimal(2)));


        // setup keyTable
        KeyTable keyTable = setupKeyTable(keytableName,keyItems);
        keyTable.setKeyValueMethod(KeyValueMethod.PERCENT);
        keyTable.setPrecision(6);

        // setup budgetItem
        BudgetItem budgetItem = new BudgetItem();
        budgetItem.setBudgetedValue(budgetedValue);

        // setup scheduleItem
        ScheduleItem scheduleItem = new ScheduleItem();
        scheduleItem.setKeyTable(keyTable);
        scheduleItem.setPercentage(percentage);
        scheduleItem.setBudgetItem(budgetItem);

        return scheduleItem;
    }

    private ScheduleItem setupScheduleItemWithKeySumZero() {

        unit1 = new UnitForTesting();
        BigDecimal percentage = new BigDecimal(100);
        BigDecimal anyBudgetedValueWillDo = new BigDecimal(100);

        // setup keyItems
        KeyItem keyItem = setupKeyItem(unit1, BigDecimal.ZERO);
        List<KeyItem> keyItems= new ArrayList<>();
        keyItems.add(keyItem);

        // setup keyTable
        KeyTable keyTable = setupKeyTable("keytable1",keyItems);
        keyTable.setKeyValueMethod(KeyValueMethod.PERCENT);
        keyTable.setPrecision(6);

        // setup budgetItem
        BudgetItem budgetItem = new BudgetItem();
        budgetItem.setBudgetedValue(anyBudgetedValueWillDo);

        // setup scheduleItem
        ScheduleItem scheduleItem = new ScheduleItem();
        scheduleItem.setKeyTable(keyTable);
        scheduleItem.setPercentage(percentage);
        scheduleItem.setBudgetItem(budgetItem);

        return scheduleItem;
    }

    private KeyItem setupKeyItem(Unit unit, BigDecimal value) {
        KeyItem keyItem = new KeyItem();
        keyItem.setUnit(unit);
        keyItem.setValue(value);
        return keyItem;
    }

    private KeyTable setupKeyTable(String name, List<KeyItem> keyItems) {
        KeyTable keyTable = new KeyTableForTesting();
        keyTable.setName(name);
        for (KeyItem item : keyItems) {
            item.setKeyTable(keyTable);
            keyTable.getItems().add(item);
        }
        return keyTable;
    }


}
