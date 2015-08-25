package org.estatio.integtests.budget;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.dom.asset.Properties;
import org.estatio.dom.asset.Property;
import org.estatio.dom.budget.Budget;
import org.estatio.dom.budget.Budgets;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.Charges;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseItemType;
import org.estatio.dom.lease.LeaseTermForServiceCharge;
import org.estatio.dom.lease.Leases;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset._PropertyForOxfGb;
import org.estatio.fixture.budget.BudgetItemForOxf;
import org.estatio.fixture.charge.ChargeRefData;
import org.estatio.fixture.lease.LeaseItemAndTermsForOxfMediax002Gb;
import org.estatio.fixture.lease.LeaseItemAndTermsForOxfMiracl005Gb;
import org.estatio.fixture.lease.LeaseItemAndTermsForOxfPoison003Gb;
import org.estatio.fixture.lease.LeaseItemAndTermsForOxfTopModel001;
import org.estatio.fixture.lease._LeaseForOxfMediaX002Gb;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by jodo on 19/08/15.
 */
public class BudgetTest extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
                executionContext.executeChild(this, new BudgetItemForOxf());
                executionContext.executeChild(this, new LeaseItemAndTermsForOxfMediax002Gb());
                executionContext.executeChild(this, new LeaseItemAndTermsForOxfMiracl005Gb());
                executionContext.executeChild(this, new LeaseItemAndTermsForOxfPoison003Gb());
                executionContext.executeChild(this, new LeaseItemAndTermsForOxfTopModel001());
            }
        });
    }

    public static class allocateBudget extends BudgetTest {

        @Inject
        Leases leases;

        @Inject
        Charges charges;

        @Inject
        Properties properties;

        @Inject
        Budgets budgets;

        Lease leaseForMediax = new Lease();
        Charge charge = new Charge();

        @Test
        public void whenSetUp() throws Exception {

            // Given
            final Property property = properties.findPropertyByReference(_PropertyForOxfGb.REF);
            final Budget budget = budgets.findBudgetByProperty(property).get(0);
            leaseForMediax = leases.findLeaseByReference(_LeaseForOxfMediaX002Gb.REF);
            charge = charges.findByReference(ChargeRefData.IT_SERVICE_CHARGE);

            // When no budget is allocated
            // Then
            assertThat(leaseForMediax.findFirstItemOfTypeAndCharge(LeaseItemType.SERVICE_CHARGE, charge).getTerms().size()).isEqualTo(1);

            // When first budgetItem is generated and allocated
            budget.getBudgetItems().first().generateBudgetLines(true);
            budget.allocateBudget(true);
            LeaseTermForServiceCharge leaseTermForServiceCharge = (LeaseTermForServiceCharge) leaseForMediax.findFirstItemOfTypeAndCharge(LeaseItemType.SERVICE_CHARGE, charge).getTerms().last();

            // Then
            // a new term is created
            assertThat(leaseForMediax.findFirstItemOfTypeAndCharge(LeaseItemType.SERVICE_CHARGE, charge).getTerms().size()).isEqualTo(2);
            assertThat(leaseTermForServiceCharge.getBudgetedValue().setScale(2, BigDecimal.ROUND_HALF_UP)).isEqualTo(new BigDecimal(184.62).setScale(2, BigDecimal.ROUND_HALF_UP));

            // When second budgetItem is also generated and allocated
            budget.getBudgetItems().last().generateBudgetLines(true);
            budget.allocateBudget(true);

            // Then
            // the new term is overwritten with the new budgeted value
            assertThat(leaseForMediax.findFirstItemOfTypeAndCharge(LeaseItemType.SERVICE_CHARGE, charge).getTerms().size()).isEqualTo(2);
            assertThat(leaseTermForServiceCharge.getBudgetedValue().setScale(2, BigDecimal.ROUND_HALF_UP)).isEqualTo(new BigDecimal(1784.62).setScale(2, BigDecimal.ROUND_HALF_UP));


        }

    }

}
