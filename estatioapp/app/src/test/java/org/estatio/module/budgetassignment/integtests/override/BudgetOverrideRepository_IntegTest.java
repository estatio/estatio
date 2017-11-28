package org.estatio.module.budgetassignment.integtests.override;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.wrapper.InvalidException;

import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.budgetassignment.dom.override.BudgetOverrideForFixed;
import org.estatio.module.budgetassignment.dom.override.BudgetOverrideRepository;
import org.estatio.module.budgetassignment.integtests.BudgetAssignmentModuleIntegTestAbstract;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.charge.fixtures.charges.refdata.ChargeRefData;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.fixtures.lease.LeaseForOxfTopModel001Gb;

import static org.assertj.core.api.Assertions.assertThat;

public class BudgetOverrideRepository_IntegTest extends BudgetAssignmentModuleIntegTestAbstract {

    @Inject
    BudgetOverrideRepository budgetOverrideRepository;

    @Inject
    PropertyRepository propertyRepository;

    @Inject
    LeaseRepository leaseRepository;

    @Inject
    ChargeRepository chargeRepository;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, new LeaseForOxfTopModel001Gb());
            }
        });
    }

    public static class NewBudgetOverride extends BudgetOverrideRepository_IntegTest {

        @Test
        public void newBudgetOverrideWorks() {

            BigDecimal overrideValue;
            String reason;

            // given
            Lease leaseTopModel = leaseRepository.findLeaseByReference(LeaseForOxfTopModel001Gb.REF);
            Charge invoiceCharge = chargeRepository.findByReference(ChargeRefData.GB_SERVICE_CHARGE);

            overrideValue = new BigDecimal("1234.56");
            reason = "Some reason";

            assertThat(budgetOverrideRepository.allBudgetOverrides().size()).isEqualTo(0);

            // when
            BudgetOverrideForFixed budgetOverrideForFixed = wrap(budgetOverrideRepository).newBudgetOverrideForFixed(overrideValue, leaseTopModel, null, null, invoiceCharge, null, null, reason);

            // then
            assertThat(budgetOverrideRepository.allBudgetOverrides().size()).isEqualTo(1);
            assertThat(budgetOverrideForFixed.getFixedValue()).isEqualTo(overrideValue);
            assertThat(budgetOverrideForFixed.getLease()).isEqualTo(leaseTopModel);
            assertThat(budgetOverrideForFixed.getInvoiceCharge()).isEqualTo(invoiceCharge);
            assertThat(budgetOverrideForFixed.getReason()).isEqualTo(reason);

        }
    }

    public static class ValidateNewBudgetOverride extends BudgetOverrideRepository_IntegTest {

        @Rule
        public ExpectedException expectedException = ExpectedException.none();

        @Test
        public void sameInvoiceChargeAndTypeInOverlappingIntervalIsInvalid() {
            // given
            Lease leaseTopModel = leaseRepository.findLeaseByReference(LeaseForOxfTopModel001Gb.REF);
            Charge invoiceCharge = chargeRepository.findByReference(ChargeRefData.GB_SERVICE_CHARGE);
            BigDecimal overrideValue = new BigDecimal("1234.56");
            String reason = "Some reason";
            LocalDate endDate = new LocalDate(2014,12,31);
            LocalDate startDate = new LocalDate(2015,01,01);
            wrap(budgetOverrideRepository).newBudgetOverrideForFixed(overrideValue, leaseTopModel, null, endDate, invoiceCharge, null, BudgetCalculationType.BUDGETED, reason);
            assertThat(budgetOverrideRepository.allBudgetOverrides().size()).isEqualTo(1);

            // when
            wrap(budgetOverrideRepository).newBudgetOverrideForFixed(overrideValue, leaseTopModel, startDate, null, invoiceCharge, null, BudgetCalculationType.BUDGETED, reason);
            wrap(budgetOverrideRepository).newBudgetOverrideForFixed(overrideValue, leaseTopModel, startDate, null, invoiceCharge, null, BudgetCalculationType.ACTUAL, reason);
            assertThat(budgetOverrideRepository.allBudgetOverrides().size()).isEqualTo(3);

            // and expect
            expectedException.expect(InvalidException.class);
            expectedException.expectMessage("Conflicting budget overrides found");

            // when
            wrap(budgetOverrideRepository).newBudgetOverrideForFixed(overrideValue, leaseTopModel, null, null, invoiceCharge, null, null, reason);

        }

        @Test
        public void sameInvoiceChargeAndIncomingChargeIsInvalid() {
            // given
            Lease leaseTopModel = leaseRepository.findLeaseByReference(LeaseForOxfTopModel001Gb.REF);
            Charge invoiceCharge = chargeRepository.findByReference(ChargeRefData.GB_SERVICE_CHARGE);
            Charge incomingCharge1 = chargeRepository.findByReference(ChargeRefData.GB_INCOMING_CHARGE_1);
            Charge incomingCharge2 = chargeRepository.findByReference(ChargeRefData.GB_INCOMING_CHARGE_2);
            BigDecimal overrideValue = new BigDecimal("1234.56");
            String reason = "Some reason";
            wrap(budgetOverrideRepository).newBudgetOverrideForFixed(overrideValue, leaseTopModel, null, null, invoiceCharge, incomingCharge1, null, reason);
            assertThat(budgetOverrideRepository.allBudgetOverrides().size()).isEqualTo(1);

            // when
            wrap(budgetOverrideRepository).newBudgetOverrideForFixed(overrideValue, leaseTopModel, null, null, invoiceCharge, incomingCharge2, null, reason);

            // then
            assertThat(budgetOverrideRepository.allBudgetOverrides().size()).isEqualTo(2);

            // and expect
            expectedException.expect(InvalidException.class);
            expectedException.expectMessage("Conflicting budget overrides found");

            // when
            wrap(budgetOverrideRepository).newBudgetOverrideForFixed(overrideValue, leaseTopModel, null, null, invoiceCharge, incomingCharge2, null, reason);

        }

    }

    public static class FindByLease extends BudgetOverrideRepository_IntegTest {

        @Test
        public void findByLease() {

            Lease leaseTopModel;

            // given
            leaseTopModel = leaseRepository.findLeaseByReference(LeaseForOxfTopModel001Gb.REF);
            Charge invoiceCharge = chargeRepository.findByReference(ChargeRefData.GB_SERVICE_CHARGE);
            BigDecimal overrideValue = new BigDecimal("1234.56");
            String reason = "Some reason";
            assertThat(budgetOverrideRepository.findByLease(leaseTopModel).size()).isEqualTo(0);

            // when
            BudgetOverrideForFixed budgetOverrideForFixed = wrap(budgetOverrideRepository).newBudgetOverrideForFixed(overrideValue, leaseTopModel, null, null, invoiceCharge, null, null, reason);

            // then
            assertThat(budgetOverrideRepository.findByLease(leaseTopModel).size()).isEqualTo(1);
            assertThat(budgetOverrideForFixed.getLease()).isEqualTo(leaseTopModel);

        }

    }

    @Test
    public void findByLeaseAndInvoiceCharge(){

        // given
        Lease leaseTopModel = leaseRepository.findLeaseByReference(LeaseForOxfTopModel001Gb.REF);
        Charge invoiceCharge = chargeRepository.findByReference(ChargeRefData.GB_SERVICE_CHARGE);
        BigDecimal overrideValue = new BigDecimal("1234.56");
        String reason = "Some reason";

        // when
        BudgetOverrideForFixed budgetOverrideForFixed = wrap(budgetOverrideRepository).newBudgetOverrideForFixed(overrideValue, leaseTopModel, null, null, invoiceCharge, null, null, reason);

        // then
        assertThat(budgetOverrideRepository.findByLeaseAndInvoiceCharge(leaseTopModel, invoiceCharge).size()).isEqualTo(1);
        assertThat(budgetOverrideRepository.findByLeaseAndInvoiceCharge(leaseTopModel, invoiceCharge).get(0)).isEqualTo(budgetOverrideForFixed);

    }





}
