package org.estatio.integtests.budget;

import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyMenu;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budget.Budgets;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForOxfGb;
import org.estatio.fixture.budget.BudgetItemForOxf;
import org.estatio.fixture.lease.LeaseItemAndTermsForOxfMediax002Gb;
import org.estatio.fixture.lease.LeaseItemAndTermsForOxfMiracl005Gb;
import org.estatio.fixture.lease.LeaseItemAndTermsForOxfPoison003Gb;
import org.estatio.fixture.lease.LeaseItemAndTermsForOxfTopModel001;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by jodo on 19/08/15.
 */
public class BudgetRepositoryTest extends EstatioIntegrationTest {

    @Inject
    Budgets budgetRepository;

    @Inject
    PropertyRepository propertyRepository;

    @Inject

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, new BudgetItemForOxf());
            }
        });
    }

    public static class FindByProperty extends BudgetRepositoryTest {

        @Test
        public void xxx() throws Exception {
            // given
            Property property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
            // when
            final List<Budget> budgetList = budgetRepository.findBudgetByProperty(property);
            // then
            assertThat(budgetList.size()).isEqualTo(1);

        }





    }


}
