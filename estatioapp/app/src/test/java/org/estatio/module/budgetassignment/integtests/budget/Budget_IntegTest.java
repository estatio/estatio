package org.estatio.module.budgetassignment.integtests.budget;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.wrapper.InvalidException;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.asset.fixtures.property.personas.PropertyAndUnitsAndOwnerAndManagerForBudNl;
import org.estatio.module.asset.fixtures.property.personas.PropertyAndUnitsAndOwnerAndManagerForOxfGb;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budget.BudgetRepository;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.budget.dom.keytable.FoundationValueType;
import org.estatio.module.budget.dom.keytable.KeyTable;
import org.estatio.module.budget.fixtures.BudgetsForOxf;
import org.estatio.module.budget.fixtures.PartitioningAndItemsForOxf;
import org.estatio.module.budgetassignment.fixtures.BudgetForBud;
import org.estatio.module.budgetassignment.fixtures.PartitioningAndItemsForBud;
import org.estatio.module.budgetassignment.integtests.BudgetAssignmentModuleIntegTestAbstract;
import org.estatio.module.lease.fixtures.lease.LeaseItemForServiceChargeBudgetedForOxfTopModel001Gb;

public class Budget_IntegTest extends BudgetAssignmentModuleIntegTestAbstract {

    @Inject
    BudgetRepository budgetRepository;

    @Inject
    PropertyRepository propertyRepository;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, new PartitioningAndItemsForOxf());
                executionContext.executeChild(this, new PartitioningAndItemsForBud());
                executionContext.executeChild(this, new LeaseItemForServiceChargeBudgetedForOxfTopModel001Gb());
            }
        });
    }

    public static class NextBudgetTest extends Budget_IntegTest {

        Property propertyBud;
        List<Budget> budgetsForBud;
        Budget budget2015;
        Budget budget2016;

        @Before
        public void setUp() throws Exception {
            propertyBud = propertyRepository.findPropertyByReference(PropertyAndUnitsAndOwnerAndManagerForBudNl.REF);
            budgetsForBud = budgetRepository.findByProperty(propertyBud);
            budget2015 = budgetRepository.findByPropertyAndStartDate(propertyBud, BudgetForBud.BUDGET_2015_START_DATE);
        }

        @Test
        public void nextBudgetTest() throws Exception {

            // given
            Assertions.assertThat(budgetsForBud.size()).isEqualTo(1);

            // when
            budget2016 = wrap(budget2015).createNextBudget();

            // then
            Assertions.assertThat(budgetRepository.findByProperty(propertyBud).size()).isEqualTo(2);
            Assertions.assertThat(budget2016.getProperty()).isEqualTo(propertyBud);
            Assertions.assertThat(budget2016.getStartDate()).isEqualTo(new LocalDate(2016, 01, 01));
            Assertions.assertThat(budget2016.getEndDate()).isEqualTo(new LocalDate(2016, 12, 31));
            Assertions.assertThat(budget2016.getPartitionings().size()).isEqualTo(1);
            Assertions.assertThat(budget2016.getPartitionings().first().getType()).isEqualTo(BudgetCalculationType.BUDGETED);
            Assertions.assertThat(budget2016.getPartitionings().first().getStartDate()).isEqualTo(budget2016.getStartDate());
            Assertions.assertThat(budget2016.getPartitionings().first().getEndDate()).isEqualTo(budget2016.getEndDate());

            Assertions.assertThat(budget2016.getItems().size()).isEqualTo(budget2015.getItems().size());
            // this serves as createCopyFor test for budgetItem
            Assertions.assertThat(budget2016.getItems().first().getBudget()).isEqualTo(budget2016);
            Assertions.assertThat(budget2016.getItems().first().getCharge()).isEqualTo(budget2015.getItems().first().getCharge());
            Assertions
                    .assertThat(budget2016.getItems().first().getApplicationTenancy()).isEqualTo(budget2015.getItems().first().getApplicationTenancy());
            Assertions.assertThat(budget2016.getItems().first().getValues().size()).isEqualTo(1);
            Assertions.assertThat(budget2016.getItems().first().getValues().first().getValue()).isEqualTo(budget2015.getItems().first().getValues().first().getValue());
            Assertions.assertThat(budget2016.getItems().first().getPartitionItems().size()).isEqualTo(1);
            Assertions.assertThat(budget2016.getItems().first().getPartitionItems().get(0).getPartitioning()).isEqualTo(budget2016.getPartitionings().first());
            Assertions.assertThat(budget2016.getItems().first().getPartitionItems().get(0).getCharge()).isEqualTo(budget2015.getItems().first().getPartitionItems().get(0).getCharge());
            Assertions.assertThat(budget2016.getItems().first().getPartitionItems().get(0).getPercentage()).isEqualTo(budget2015.getItems().first().getPartitionItems().get(0).getPercentage());
            Assertions.assertThat(budget2016.getItems().first().getPartitionItems().get(0).getKeyTable().getName()).isEqualTo(budget2015.getItems().first().getPartitionItems().get(0).getKeyTable().getName());

            Assertions.assertThat(budget2016.getKeyTables().size()).isEqualTo(budget2015.getKeyTables().size());
            // this serves as createCopyFor test for keyTable
            KeyTable firstNewKeyTable = budget2016.getKeyTables().first();
            KeyTable lastNewKeyTable = budget2016.getKeyTables().last();
            Assertions.assertThat(firstNewKeyTable.getName()).isEqualTo(budget2015.getKeyTables().first().getName());
            Assertions.assertThat(lastNewKeyTable.getName()).isEqualTo(budget2015.getKeyTables().last().getName());
            Assertions.assertThat(firstNewKeyTable.getFoundationValueType()).isEqualTo(FoundationValueType.AREA);
            Assertions.assertThat(lastNewKeyTable.getFoundationValueType()).isEqualTo(FoundationValueType.COUNT);
            Assertions.assertThat(firstNewKeyTable.getItems().size()).isEqualTo(budget2015.getKeyTables().first().getItems().size());
            Assertions.assertThat(firstNewKeyTable.getItems().first().getValue()).isEqualTo(new BigDecimal("35.714286"));

        }

    }

    public static class NextBudgetExistsAlready extends Budget_IntegTest {

        Property propertyOxf;
        List<Budget> budgetsForOxf;
        Budget budget2015;

        @Before
        public void setUp() throws Exception {
            propertyOxf = propertyRepository.findPropertyByReference(PropertyAndUnitsAndOwnerAndManagerForOxfGb.REF);
            budgetsForOxf = budgetRepository.findByProperty(propertyOxf);
            budget2015 = budgetRepository.findByPropertyAndStartDate(propertyOxf, BudgetsForOxf.BUDGET_2015_START_DATE);
        }

        @Rule
        public ExpectedException expectedException = ExpectedException.none();

        @Test
        public void nextBudgetAlreadyExistsTest(){

            //then
            expectedException.expect(InvalidException.class);
            expectedException.expectMessage("Reason: This budget already exists");
            // when
            wrap(budget2015).createNextBudget();
        }

    }

}
