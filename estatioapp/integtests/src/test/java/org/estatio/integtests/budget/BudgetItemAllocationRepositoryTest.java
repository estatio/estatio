package org.estatio.integtests.budget;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budget.BudgetRepository;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.estatio.dom.budgeting.keytable.KeyTableRepository;
import org.estatio.dom.budgeting.allocation.BudgetItemAllocation;
import org.estatio.dom.budgeting.allocation.BudgetItemAllocationRepository;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.Charges;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForOxfGb;
import org.estatio.fixture.budget.BudgetItemAllocationsForOxf;
import org.estatio.fixture.charge.ChargeRefData;
import org.estatio.integtests.EstatioIntegrationTest;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class BudgetItemAllocationRepositoryTest extends EstatioIntegrationTest {

    @Inject
    BudgetItemAllocationRepository budgetItemAllocationRepository;

    @Inject
    BudgetRepository budgetRepository;

    @Inject
    PropertyRepository propertyRepository;

    @Inject
    KeyTableRepository keytablesRepository;

    @Inject
    Charges chargeRepository;


    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
                executionContext.executeChild(this, new BudgetItemAllocationsForOxf());
            }
        });
    }

    public static class validateNewBudgetItemAllocation extends BudgetItemAllocationRepositoryTest {

        @Test
        public void doubleBudgetItemAllocation() throws Exception {

            // given
            Property property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
            Budget budget = budgetRepository.findByProperty(property).get(0);
            BudgetItemAllocation budgetItemAllocation = budget.getItems().first().getBudgetItemAllocations().first();

            //when, then
            assertThat(budgetItemAllocationRepository
                    .validateNewBudgetItemAllocation(
                            budgetItemAllocation.getCharge(),
                            budgetItemAllocation.getKeyTable(),
                            budgetItemAllocation.getBudgetItem(),
                            null)
            ).isEqualTo("This schedule item already exists");

        }

    }

    public static class FindByBudgetItemAllocation extends BudgetItemAllocationRepositoryTest {

        @Test
        public void happyCase() throws Exception {
            // given
            Property property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
            Budget budget = budgetRepository.findByPropertyAndStartDate(property, new LocalDate(2015, 01, 01));
            BudgetItem budgetItem = budget.getItems().last();
            // when
            final List<BudgetItemAllocation> budgetItemAllocationList = budgetItemAllocationRepository.findByBudgetItem(budgetItem);
            // then
            assertThat(budgetItemAllocationList.size()).isEqualTo(2);

        }

    }


    public static class FindByKeyTable extends BudgetItemAllocationRepositoryTest {

        @Test
        public void happyCase() throws Exception {
            // given
            Property property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
            Budget budget = budgetRepository.findByProperty(property).get(0);
            KeyTable keyTable = keytablesRepository.findByBudget(budget).get(0);
            // when
            final List<BudgetItemAllocation> budgetItemAllocationList = budgetItemAllocationRepository.findByKeyTable(keyTable);
            // then
            assertThat(budgetItemAllocationList.size()).isEqualTo(2);

        }

    }

    public static class FindByBudgetItemAllocationAndKeyTable extends BudgetItemAllocationRepositoryTest {

        @Test
        public void happyCase() throws Exception {
            // given
            Property property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);

            Budget budget = budgetRepository.findByProperty(property).get(0);
            BudgetItem budgetItem = budget.getItems().first();
            KeyTable keyTable = keytablesRepository.findByBudget(budget).get(0);
            Charge charge = chargeRepository.findByReference(ChargeRefData.GB_SERVICE_CHARGE);
            // when
            final BudgetItemAllocation budgetItemAllocation = budgetItemAllocationRepository.findByChargeAndBudgetItemAndKeyTable(charge , budgetItem, keyTable);
            // then
            assertThat(budgetItemAllocation.getBudgetItem()).isEqualTo(budgetItem);
            assertThat(budgetItemAllocation.getKeyTable()).isEqualTo(keyTable);
        }

    }

}
