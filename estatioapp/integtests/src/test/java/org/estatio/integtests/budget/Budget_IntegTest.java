package org.estatio.integtests.budget;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.wrapper.InvalidException;

import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budget.BudgetRepository;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationType;
import org.estatio.dom.budgeting.keytable.FoundationValueType;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForBudNl;
import org.estatio.fixture.asset.PropertyForOxfGb;
import org.estatio.fixture.budget.BudgetForBud;
import org.estatio.fixture.budget.BudgetsForOxf;
import org.estatio.fixture.budget.PartitioningAndItemsForBud;
import org.estatio.fixture.budget.PartitioningAndItemsForOxf;
import org.estatio.fixture.lease.LeaseItemForServiceChargeBudgetedForOxfTopModel001Gb;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class Budget_IntegTest extends EstatioIntegrationTest {

    @Inject
    BudgetRepository budgetRepository;

    @Inject
    PropertyRepository propertyRepository;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
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
            propertyBud = propertyRepository.findPropertyByReference(PropertyForBudNl.REF);
            budgetsForBud = budgetRepository.findByProperty(propertyBud);
            budget2015 = budgetRepository.findByPropertyAndStartDate(propertyBud, BudgetForBud.BUDGET_2015_START_DATE);
        }

        @Test
        public void nextBudgetTest() throws Exception {

            // given
            assertThat(budgetsForBud.size()).isEqualTo(1);

            // when
            budget2016 = wrap(budget2015).createNextBudget();

            // then
            assertThat(budgetRepository.findByProperty(propertyBud).size()).isEqualTo(2);
            assertThat(budget2016.getProperty()).isEqualTo(propertyBud);
            assertThat(budget2016.getStartDate()).isEqualTo(new LocalDate(2016, 01, 01));
            assertThat(budget2016.getEndDate()).isEqualTo(new LocalDate(2016, 12, 31));
            assertThat(budget2016.getPartitionings().size()).isEqualTo(1);
            assertThat(budget2016.getPartitionings().first().getType()).isEqualTo(BudgetCalculationType.BUDGETED);
            assertThat(budget2016.getPartitionings().first().getStartDate()).isEqualTo(budget2016.getStartDate());
            assertThat(budget2016.getPartitionings().first().getEndDate()).isEqualTo(budget2016.getEndDate());

            assertThat(budget2016.getItems().size()).isEqualTo(budget2015.getItems().size());
            // this serves as createCopyFor test for budgetItem
            assertThat(budget2016.getItems().first().getBudget()).isEqualTo(budget2016);
            assertThat(budget2016.getItems().first().getCharge()).isEqualTo(budget2015.getItems().first().getCharge());
            assertThat(budget2016.getItems().first().getApplicationTenancy()).isEqualTo(budget2015.getItems().first().getApplicationTenancy());
            assertThat(budget2016.getItems().first().getValues().size()).isEqualTo(1);
            assertThat(budget2016.getItems().first().getValues().first().getValue()).isEqualTo(budget2015.getItems().first().getValues().first().getValue());
            assertThat(budget2016.getItems().first().getPartitionItems().size()).isEqualTo(1);
            assertThat(budget2016.getItems().first().getPartitionItems().get(0).getPartitioning()).isEqualTo(budget2016.getPartitionings().first());
            assertThat(budget2016.getItems().first().getPartitionItems().get(0).getCharge()).isEqualTo(budget2015.getItems().first().getPartitionItems().get(0).getCharge());
            assertThat(budget2016.getItems().first().getPartitionItems().get(0).getPercentage()).isEqualTo(budget2015.getItems().first().getPartitionItems().get(0).getPercentage());
            assertThat(budget2016.getItems().first().getPartitionItems().get(0).getKeyTable().getName()).isEqualTo(budget2015.getItems().first().getPartitionItems().get(0).getKeyTable().getName());

            assertThat(budget2016.getKeyTables().size()).isEqualTo(budget2015.getKeyTables().size());
            // this serves as createCopyFor test for keyTable
            KeyTable firstNewKeyTable = budget2016.getKeyTables().first();
            KeyTable lastNewKeyTable = budget2016.getKeyTables().last();
            assertThat(firstNewKeyTable.getName()).isEqualTo(budget2015.getKeyTables().first().getName());
            assertThat(lastNewKeyTable.getName()).isEqualTo(budget2015.getKeyTables().last().getName());
            assertThat(firstNewKeyTable.getFoundationValueType()).isEqualTo(FoundationValueType.AREA);
            assertThat(lastNewKeyTable.getFoundationValueType()).isEqualTo(FoundationValueType.COUNT);
            assertThat(firstNewKeyTable.getItems().size()).isEqualTo(budget2015.getKeyTables().first().getItems().size());
            assertThat(firstNewKeyTable.getItems().first().getValue()).isEqualTo(new BigDecimal("35.714286"));

        }

    }

    public static class NextBudgetExistsAlready extends Budget_IntegTest {

        Property propertyOxf;
        List<Budget> budgetsForOxf;
        Budget budget2015;

        @Before
        public void setUp() throws Exception {
            propertyOxf = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
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
