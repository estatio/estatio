package org.estatio.dom.budgeting.viewmodels;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import org.estatio.dom.asset.Unit;
import org.estatio.dom.budgeting.ChargeForTesting;
import org.estatio.dom.budgeting.UnitForTesting;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.lease.Occupancy;

import static org.assertj.core.api.Assertions.assertThat;

public class BudgetOverviewTest {

    BudgetOverview budgetOverview = new BudgetOverview();

    @Test
    public void testAggregateByChargeNoOccupancy(){

        // given
        Unit unit = new UnitForTesting();
        Charge charge = new ChargeForTesting();
        BigDecimal amount = new BigDecimal("100");
        BudgetOverviewLine newLine1 = new BudgetOverviewLine(null, unit, charge, amount);
        List<BudgetOverviewLine> lines = new ArrayList<>();

        // when
        lines = budgetOverview.aggregateByCharge(newLine1, lines);

        // then
        assertThat(lines).hasSize(1);
        assertThat(lines.get(0).getUnit()).isEqualTo(unit);
        assertThat(lines.get(0).getCharge()).isEqualTo(charge);
        assertThat(lines.get(0).getAmount()).isEqualTo(amount);

        // and when
        BudgetOverviewLine newLine2 = new BudgetOverviewLine(null, unit, charge, amount);
        lines = budgetOverview.aggregateByCharge(newLine2, lines);

        // then
        assertThat(lines).hasSize(1);
        assertThat(lines.get(0).getUnit()).isEqualTo(unit);
        assertThat(lines.get(0).getCharge()).isEqualTo(charge);
        assertThat(lines.get(0).getAmount()).isEqualTo(new BigDecimal("200"));

        // and when
        Charge someOtherCharge = new ChargeForTesting();
        BudgetOverviewLine newLine3 = new BudgetOverviewLine(null, unit, someOtherCharge, amount);
        lines = budgetOverview.aggregateByCharge(newLine3, lines);

        // then
        assertThat(lines).hasSize(2);
        assertThat(lines.get(0).getUnit()).isEqualTo(unit);
        assertThat(lines.get(0).getCharge()).isEqualTo(charge);
        assertThat(lines.get(0).getAmount()).isEqualTo(new BigDecimal("200"));
        assertThat(lines.get(1).getUnit()).isEqualTo(unit);
        assertThat(lines.get(1).getCharge()).isEqualTo(someOtherCharge);
        assertThat(lines.get(1).getAmount()).isEqualTo(new BigDecimal("100"));

        // and when
        Unit someOtherUnit = new UnitForTesting();
        BudgetOverviewLine newLine4 = new BudgetOverviewLine(null, someOtherUnit, charge, amount);
        lines = budgetOverview.aggregateByCharge(newLine4, lines);

        // then
        assertThat(lines).hasSize(3);
        assertThat(lines.get(2).getUnit()).isEqualTo(someOtherUnit);
        assertThat(lines.get(2).getCharge()).isEqualTo(charge);
        assertThat(lines.get(2).getAmount()).isEqualTo(new BigDecimal("100"));
    }

    @Test
    public void testAggregateByChargeWithOccupancy(){

        // given
        Occupancy occupancy = new Occupancy();
        Unit unit = new UnitForTesting();
        occupancy.setUnit(unit);
        Charge charge = new ChargeForTesting();
        BigDecimal amount = new BigDecimal("100");
        BudgetOverviewLine newLine1 = new BudgetOverviewLine(occupancy, occupancy.getUnit(), charge, amount);
        List<BudgetOverviewLine> lines = new ArrayList<>();

        // when
        lines = budgetOverview.aggregateByCharge(newLine1, lines);

        // then
        assertThat(lines).hasSize(1);
        assertThat(lines.get(0).getOccupancy()).isEqualTo(occupancy);
        assertThat(lines.get(0).getCharge()).isEqualTo(charge);
        assertThat(lines.get(0).getAmount()).isEqualTo(amount);

        // and when
        BudgetOverviewLine newLine2 = new BudgetOverviewLine(occupancy, occupancy.getUnit(), charge, amount);
        lines = budgetOverview.aggregateByCharge(newLine2, lines);

        // then
        assertThat(lines).hasSize(1);
        assertThat(lines.get(0).getOccupancy()).isEqualTo(occupancy);
        assertThat(lines.get(0).getCharge()).isEqualTo(charge);
        assertThat(lines.get(0).getAmount()).isEqualTo(new BigDecimal("200"));

        // and when
        Charge someOtherCharge = new ChargeForTesting();
        BudgetOverviewLine newLine3 = new BudgetOverviewLine(occupancy, occupancy.getUnit(), someOtherCharge, amount);
        lines = budgetOverview.aggregateByCharge(newLine3, lines);

        // then
        assertThat(lines).hasSize(2);
        assertThat(lines.get(0).getOccupancy()).isEqualTo(occupancy);
        assertThat(lines.get(0).getCharge()).isEqualTo(charge);
        assertThat(lines.get(0).getAmount()).isEqualTo(new BigDecimal("200"));
        assertThat(lines.get(1).getOccupancy()).isEqualTo(occupancy);
        assertThat(lines.get(1).getCharge()).isEqualTo(someOtherCharge);
        assertThat(lines.get(1).getAmount()).isEqualTo(new BigDecimal("100"));

        // and when
        Occupancy someOtherOccupancy = new Occupancy();
        someOtherOccupancy.setUnit(unit);
        BudgetOverviewLine newLine4 = new BudgetOverviewLine(someOtherOccupancy, someOtherOccupancy.getUnit(), charge, amount);
        lines = budgetOverview.aggregateByCharge(newLine4, lines);

        // then
        assertThat(lines).hasSize(3);
        assertThat(lines.get(2).getOccupancy()).isEqualTo(someOtherOccupancy);
        assertThat(lines.get(2).getCharge()).isEqualTo(charge);
        assertThat(lines.get(2).getAmount()).isEqualTo(new BigDecimal("100"));
    }

}
