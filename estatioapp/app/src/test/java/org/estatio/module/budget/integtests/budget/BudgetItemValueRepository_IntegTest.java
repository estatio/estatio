package org.estatio.module.budget.integtests.budget;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budget.BudgetRepository;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.budget.dom.budgetitem.BudgetItem;
import org.estatio.module.budget.dom.budgetitem.BudgetItemValue;
import org.estatio.module.budget.dom.budgetitem.BudgetItemValueRepository;
import org.estatio.module.budget.fixtures.budgets.enums.Budget_enum;
import org.estatio.module.budget.integtests.BudgetModuleIntegTestAbstract;

import static org.assertj.core.api.Assertions.assertThat;

public class BudgetItemValueRepository_IntegTest extends BudgetModuleIntegTestAbstract {

    @Inject PropertyRepository propertyRepository;

    @Inject BudgetRepository budgetRepository;

    @Inject BudgetItemValueRepository budgetItemValueRepository;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, Budget_enum.OxfBudget2015.toBuilderScript());
                executionContext.executeChild(this, Budget_enum.OxfBudget2016.toBuilderScript());

            }
        });
    }

    @Test
    public void findByBudgetItemAndType() {

        // given
        Property property = Property_enum.OxfGb.findUsing(serviceRegistry);
        Budget budget = budgetRepository.findByPropertyAndStartDate(property, new LocalDate(2015, 01, 01));
        BudgetItem budgetItem = budget.getItems().first();

        assertThat(budgetItem.getValues().size()).isEqualTo(1);
        assertThat(budgetItem.getValues().first().getType()).isEqualTo(BudgetCalculationType.BUDGETED);

        // when
        List<BudgetItemValue> results = budgetItemValueRepository.findByBudgetItemAndType(budgetItem, BudgetCalculationType.BUDGETED);

        // then
        assertThat(results.size()).isEqualTo(1);
    }

    @Test
    public void findUniqueTest(){

        // given
        Property property = Property_enum.OxfGb.findUsing(serviceRegistry);
        Budget budget = budgetRepository.findByPropertyAndStartDate(property, new LocalDate(2015, 01, 01));
        BudgetItem budgetItem = budget.getItems().first();

        // when
        BudgetItemValue result = budgetItemValueRepository.findUnique(budgetItem, new LocalDate(2015,01,01), BudgetCalculationType.BUDGETED);

        // then
        assertThat(result.getDate()).isEqualTo(new LocalDate(2015, 01, 01));

        // and when
        result = budgetItemValueRepository.findUnique(budgetItem, new LocalDate(2015,01,02), BudgetCalculationType.BUDGETED);

        // then
        assertThat(result).isNull();

    }

    @Test
    public void updateOrCreateTest_Update(){

        // given
        LocalDate budgetStart = new LocalDate(2015, 01, 01);
        Property property = Property_enum.OxfGb.findUsing(serviceRegistry);
        Budget budget = budgetRepository.findByPropertyAndStartDate(property, budgetStart);
        BudgetItem budgetItem = budget.getItems().first();

        assertThat(budgetItem.getValues().size()).isEqualTo(1);
        assertThat(budgetItem.getValues().first().getType()).isEqualTo(BudgetCalculationType.BUDGETED);
        assertThat(budgetItem.getValues().first().getValue()).isEqualTo(new BigDecimal("30000.55"));

        // when
        BudgetItemValue result = wrap(budgetItemValueRepository).updateOrCreateBudgetItemValue(new BigDecimal("33333.00"), budgetItem, budgetStart, BudgetCalculationType.BUDGETED);

        // then
        assertThat(budgetItem.getValues().size()).isEqualTo(1);
        assertThat(result.getValue()).isEqualTo(new BigDecimal("33333.00"));

    }

    @Test
    public void updateOrCreateTest_Create(){

        // given
        LocalDate budgetStart = new LocalDate(2015, 1, 1);
        Property property = Property_enum.OxfGb.findUsing(serviceRegistry);
        Budget budget = budgetRepository.findByPropertyAndStartDate(property, budgetStart);
        BudgetItem budgetItem = budget.getItems().first();

        assertThat(budgetItem.getValues().size()).isEqualTo(1);
        assertThat(budgetItem.getValues().first().getType()).isEqualTo(BudgetCalculationType.BUDGETED);
        assertThat(budgetItem.getValues().first().getValue()).isEqualTo(new BigDecimal("30000.55"));

        // when
        BudgetItemValue result = wrap(budgetItemValueRepository).updateOrCreateBudgetItemValue(new BigDecimal("33333.00"), budgetItem, budgetStart, BudgetCalculationType.ACTUAL);
        transactionService.flushTransaction();

        // then
        assertThat(budgetItem.getValues().size()).isEqualTo(2);
        assertThat(result.getValue()).isEqualTo(new BigDecimal("33333.00"));
        assertThat(result.getType()).isEqualTo(BudgetCalculationType.ACTUAL);

    }

}
