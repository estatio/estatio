package org.estatio.module.lease.integtests.lease;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.LeaseType;
import org.estatio.module.lease.dom.LeaseTypeRepository;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.fixtures.leaseitems.deposits.personas.LeaseItemAndLeaseTermForDepositForOxfTopModel001Gb;
import org.estatio.module.lease.fixtures.leaseitems.discount.personas.LeaseItemAndLeaseTermForDiscountForOxfTopModel001Gb;
import org.estatio.module.lease.fixtures.leaseitems.entryfee.personas.LeaseItemAndLeaseTermForEntryFeeForOxfTopModel001Gb;
import org.estatio.module.lease.fixtures.leaseitems.marketing.personas.LeaseItemAndLeaseTermForMarketingForOxfTopModel001Gb;
import org.estatio.module.lease.fixtures.leaseitems.percentage.personas.LeaseItemAndLeaseTermForPercentageForOxfTopModel001Gb;
import org.estatio.module.lease.fixtures.leaseitems.rent.personas.LeaseItemAndLeaseTermForRentForOxfTopModel001Gb;
import org.estatio.module.lease.fixtures.leaseitems.servicecharge.personas.LeaseItemAndLeaseTermForServiceChargeForOxfTopModel001Gb;
import org.estatio.module.lease.fixtures.leaseitems.svcchgbudgeted.personas.LeaseItemForServiceChargeBudgetedForOxfTopModel001Gb;
import org.estatio.module.lease.fixtures.leaseitems.tax.personas.LeaseItemAndLeaseTermForTaxForOxfTopModel001Gb;
import org.estatio.module.lease.fixtures.leaseitems.turnoverrent.personas.LeaseItemAndLeaseTermForTurnoverRentForOxfTopModel001Gb;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LeaseTypeRepository_IntegTest extends LeaseModuleIntegTestAbstract {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {

                executionContext.executeChild(this, new LeaseItemAndLeaseTermForRentForOxfTopModel001Gb());
                executionContext.executeChild(this, new LeaseItemAndLeaseTermForServiceChargeForOxfTopModel001Gb());
                executionContext.executeChild(this, new LeaseItemForServiceChargeBudgetedForOxfTopModel001Gb());
                executionContext.executeChild(this, new LeaseItemAndLeaseTermForTurnoverRentForOxfTopModel001Gb());
                executionContext.executeChild(this, new LeaseItemAndLeaseTermForPercentageForOxfTopModel001Gb());
                executionContext.executeChild(this, new LeaseItemAndLeaseTermForDiscountForOxfTopModel001Gb());
                executionContext.executeChild(this, new LeaseItemAndLeaseTermForEntryFeeForOxfTopModel001Gb());
                executionContext.executeChild(this, new LeaseItemAndLeaseTermForTaxForOxfTopModel001Gb());
                executionContext.executeChild(this, new LeaseItemAndLeaseTermForDepositForOxfTopModel001Gb());
                executionContext.executeChild(this, new LeaseItemAndLeaseTermForMarketingForOxfTopModel001Gb());

            }
        });
    }

    @Inject
    LeaseRepository leaseRepository;

    @Inject
    LeaseTypeRepository leaseTypeRepository;

    Lease lease;

    @Before
    public void setUp() throws Exception {
        lease = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
    }

    public static class FindByReference extends LeaseTypeRepository_IntegTest {

        @Test
        public void findByReference() throws Exception {
            LeaseType leaseType = leaseTypeRepository.findByReference(lease.getLeaseType().getReference());
            assertThat(leaseType, is(lease.getLeaseType()));
        }
    }

}
