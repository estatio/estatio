package org.estatio.module.budget.integtests.budget;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budget.BudgetRepository;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.budget.dom.budgetitem.BudgetItem;
import org.estatio.module.budget.dom.budgetitem.BudgetItemRepository;
import org.estatio.module.budget.fixtures.budgets.enums.Budget_enum;
import org.estatio.module.budget.integtests.BudgetModuleIntegTestAbstract;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.charge.fixtures.charges.enums.Charge_enum;

import static org.assertj.core.api.Assertions.assertThat;
import static org.incode.module.base.integtests.VT.bd;

public class BudgetItemRepository_IntegTest extends BudgetModuleIntegTestAbstract {

    @Inject
    BudgetItemRepository budgetItemRepository;

    @Inject
    BudgetRepository budgetRepository;

    @Inject
    PropertyRepository propertyRepository;

    @Inject
    ChargeRepository chargeRepository;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, Budget_enum.OxfBudget2015.builder());
                executionContext.executeChild(this, Budget_enum.OxfBudget2016.builder());

            }
        });
    }

    public static class FindByProperty extends BudgetItemRepository_IntegTest {

        @Test
        public void happyCase() throws Exception {
            // given
            Property property = Property_enum.OxfGb.findUsing(serviceRegistry);
            // when
            final List<BudgetItem> budgetItemList = budgetItemRepository.findByProperty(property);
            // then
            assertThat(budgetItemList.size()).isEqualTo(4);
            assertThat(budgetItemList.get(0).getBudget().getStartDate()).isEqualTo(
                    Budget_enum.OxfBudget2016.getStartDate());

        }

    }

    public static class FindByBudgetAndCharge extends BudgetItemRepository_IntegTest {

        @Test
        public void happyCase() throws Exception {
            // given
            Budget budget = Budget_enum.OxfBudget2016.findUsing(serviceRegistry);
            Charge charge = Charge_enum.GbIncomingCharge1.findUsing(serviceRegistry);
            // when
            final BudgetItem item = budgetItemRepository.findByBudgetAndCharge(budget, charge);
            // then
            assertThat(item.getBudget()).isEqualTo(budget);
            assertThat(item.getCharge()).isEqualTo(charge);

        }

    }

    public static class NewItem extends BudgetItemRepository_IntegTest {


        @Test
        public void happyCase() {

            BudgetItem budgetItem;
            BigDecimal budgetedValue;

            // given
            Budget budget = Budget_enum.OxfBudget2016.findUsing(serviceRegistry);
            Charge charge = Charge_enum.GbIncomingCharge3.findUsing(serviceRegistry);
            budgetedValue = bd("1234.56");

            assertThat(budget.getItems().size()).isEqualTo(2);

            // when
            budgetItem = budgetItemRepository.newBudgetItem(budget, budgetedValue, charge);

            // then
            assertThat(budget.getItems().size()).isEqualTo(3);
            assertThat(budgetItem.getValues().size()).isEqualTo(1);
            assertThat(budgetItem.getValues().first().getValue()).isEqualTo(budgetedValue);
            assertThat(budgetItem.getValues().first().getType()).isEqualTo(BudgetCalculationType.BUDGETED);
            assertThat(budgetItem.getValues().first().getDate()).isEqualTo(budget.getStartDate());
            assertThat(budgetItem.getValues().first().getBudgetItem()).isEqualTo(budgetItem);
        }

        @Rule
        public ExpectedException expectedException = ExpectedException.none();

        @Test
        public void sadCase() {
            
            BigDecimal budgetedValue;

            // given
            Budget budget = Budget_enum.OxfBudget2016.findUsing(serviceRegistry);
            Charge charge = Charge_enum.GbIncomingCharge1.findUsing(serviceRegistry);
            budgetedValue = bd("1234.56");

            // when
            final String reason = budgetItemRepository.validateNewBudgetItem(budget, budgetedValue, charge);

            // then
            assertThat(reason).isEqualTo("There is already an item with this charge.");

        }

    }

}
