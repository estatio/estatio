package org.estatio.module.lease.integtests.agreement;

import javax.inject.Inject;

import org.junit.Before;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.agreement.dom.AgreementRepository;
import org.estatio.module.agreement.dom.type.AgreementType;
import org.estatio.module.agreement.dom.type.AgreementTypeRepository;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
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
                executionContext.executeChild(this, Lease_enum.OxfTopModel001Gb.toBuilderScript());
                executionContext.executeChild(this, Lease_enum.OxfMediaX002Gb.toBuilderScript());
                executionContext.executeChild(this, Lease_enum.OxfPoison003Gb.toBuilderScript());
                executionContext.executeChild(this, Lease_enum.OxfPret004Gb.toBuilderScript());
                executionContext.executeChild(this, Lease_enum.OxfMiracl005Gb.toBuilderScript());
                executionContext.executeChild(this, Lease_enum.KalPoison001Nl.toBuilderScript());
            }
        });

        lease = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
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
