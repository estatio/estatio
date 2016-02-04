package org.estatio.integtests.budget;

import java.util.List;

import javax.inject.Inject;

import org.estatio.dom.budgeting.budgetitem.BudgetItemRepository;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budget.BudgetRepository;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.Charges;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForOxfGb;
import org.estatio.fixture.budget.BudgetsForOxf;
import org.estatio.fixture.charge.ChargeRefData;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by jodo on 19/08/15.
 */
public class BudgetItemRepositoryTest extends EstatioIntegrationTest {

    @Inject
    BudgetItemRepository budgetItemRepository;

    @Inject
    BudgetRepository budgetRepository;

    @Inject
    PropertyRepository propertyRepository;

    @Inject
    Charges chargesRepository;


    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
                executionContext.executeChild(this, new BudgetsForOxf());
            }
        });
    }

    public static class FindByProperty extends BudgetItemRepositoryTest {

        @Test
        public void happyCase() throws Exception {
            // given
            Property property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
            Budget budget = budgetRepository.findByPropertyAndStartDate(property, new LocalDate(2015,01,01));
            // when
            final List<BudgetItem> budgetList = budgetItemRepository.findByBudget(budget);
            // then
            assertThat(budgetList.size()).isEqualTo(2);

        }

    }

    public static class FindByBudgetAndCharge extends BudgetItemRepositoryTest {

        @Test
        public void happyCase() throws Exception {
            // given
            Property property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
            LocalDate startDate = new LocalDate(2016, 01, 01);
            Budget budget = budgetRepository.findByPropertyAndStartDate(property, startDate);
            Charge charge = chargesRepository.findByReference(ChargeRefData.GB_SERVICE_CHARGE_ONBUDGET1);
            // when
            final BudgetItem item = budgetItemRepository.findByBudgetAndCharge(budget, charge);
            // then
            assertThat(item.getBudget()).isEqualTo(budget);
            assertThat(item.getCharge()).isEqualTo(charge);

        }

    }

    public static class FindByPropertyAndChargeAndStartDate extends BudgetItemRepositoryTest {

        @Test
        public void happyCase() throws Exception {
            // given
            Property property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
            Charge charge = chargesRepository.findByReference(ChargeRefData.GB_SERVICE_CHARGE_ONBUDGET1);
            LocalDate startDate = new LocalDate(2016, 01, 01);
            // when
            final BudgetItem item = budgetItemRepository.findByPropertyAndChargeAndStartDate(property,charge,startDate);
            // then
            assertThat(item.getBudget().getProperty()).isEqualTo(property);
            assertThat(item.getBudget().getStartDate()).isEqualTo(startDate);
            assertThat(item.getCharge()).isEqualTo(charge);
        }

    }


}
