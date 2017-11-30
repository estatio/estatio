package org.estatio.module.lease.integtests.agreement;

import javax.inject.Inject;

import org.junit.Before;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.agreement.dom.AgreementRepository;
import org.estatio.module.agreement.dom.type.AgreementType;
import org.estatio.module.agreement.dom.type.AgreementTypeRepository;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.fixtures.lease.personas.LeaseForKalPoison001Nl;
import org.estatio.module.lease.fixtures.lease.personas.LeaseForOxfMediaX002Gb;
import org.estatio.module.lease.fixtures.lease.personas.LeaseForOxfMiracl005Gb;
import org.estatio.module.lease.fixtures.lease.personas.LeaseForOxfPoison003Gb;
import org.estatio.module.lease.fixtures.lease.personas.LeaseForOxfPret004Gb;
import org.estatio.module.lease.fixtures.lease.personas.LeaseForOxfTopModel001Gb;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AgreementTypeRepository_IntegTest extends LeaseModuleIntegTestAbstract {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                // 5 oxford leases, 1 kal
                executionContext.executeChild(this, new LeaseForOxfTopModel001Gb());
                executionContext.executeChild(this, new LeaseForOxfMediaX002Gb());
                executionContext.executeChild(this, new LeaseForOxfPoison003Gb());
                executionContext.executeChild(this, new LeaseForOxfPret004Gb());
                executionContext.executeChild(this, new LeaseForOxfMiracl005Gb());
                executionContext.executeChild(this, new LeaseForKalPoison001Nl());
            }
        });

        lease = leaseRepository.findLeaseByReference(LeaseForOxfTopModel001Gb.REF);
    }

    @Inject
    AgreementRepository agreementRepository;

    @Inject
    AgreementTypeRepository agreementTypeRepository;

    @Inject
    LeaseRepository leaseRepository;

    Lease lease;

    public static class Test extends AgreementTypeRepository_IntegTest {

        @org.junit.Test
        public void happyCase() throws Exception {
            AgreementType agreementType = agreementTypeRepository.find(lease.getType().getTitle());
            assertThat(agreementType, is(lease.getType()));
        }
    }

}
