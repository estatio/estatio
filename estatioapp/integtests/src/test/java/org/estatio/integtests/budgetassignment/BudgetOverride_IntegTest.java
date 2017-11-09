package org.estatio.integtests.budgetassignment;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.budgetassignment.dom.override.BudgetOverrideForFixed;
import org.estatio.module.budgetassignment.dom.override.BudgetOverrideRepository;
import org.estatio.module.budgetassignment.dom.override.BudgetOverrideValueRepository;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.application.fixtures.EstatioBaseLineFixture;
import org.estatio.module.charge.fixtures.ChargeRefData;
import org.estatio.module.application.fixtures.lease.LeaseForOxfTopModel001Gb;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class BudgetOverride_IntegTest extends EstatioIntegrationTest {

    @Inject
    BudgetOverrideRepository budgetOverrideRepository;

    @Inject
    BudgetOverrideValueRepository budgetOverrideValueRepository;

    @Inject
    LeaseRepository leaseRepository;

    @Inject
    ChargeRepository chargeRepository;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
                executionContext.executeChild(this, new LeaseForOxfTopModel001Gb());
            }
        });
    }

    BudgetOverrideForFixed budgetOverrideForFixed;

    @Test
    public void findOrCreateValuesTest(){

        // given
        Lease leaseTopModel = leaseRepository.findLeaseByReference(LeaseForOxfTopModel001Gb.REF);
        Charge invoiceCharge = chargeRepository.findByReference(ChargeRefData.GB_SERVICE_CHARGE);
        BigDecimal overrideValue = new BigDecimal("1234.56");
        String reason = "Some reason";
        budgetOverrideForFixed = wrap(budgetOverrideRepository).newBudgetOverrideForFixed(overrideValue, leaseTopModel, null, null, invoiceCharge, null, null, reason);
        assertThat(budgetOverrideValueRepository.allBudgetOverrideValues().size()).isEqualTo(0);

        // when
        budgetOverrideForFixed.findOrCreateValues(new LocalDate(2015, 01,01));

        // then
        assertThat(budgetOverrideForFixed.getValues().size()).isEqualTo(2);
        assertThat(budgetOverrideForFixed.getValues().first().getType()).isEqualTo(BudgetCalculationType.BUDGETED);
        assertThat(budgetOverrideForFixed.getValues().last().getType()).isEqualTo(BudgetCalculationType.ACTUAL);

        // and when again
        budgetOverrideForFixed.findOrCreateValues(new LocalDate(2015, 01,01));

        // then still
        assertThat(budgetOverrideForFixed.getValues().size()).isEqualTo(2);

    }



}
