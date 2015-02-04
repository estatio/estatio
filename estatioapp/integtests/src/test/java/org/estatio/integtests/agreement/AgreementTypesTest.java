package org.estatio.integtests.agreement;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import javax.inject.Inject;

import org.junit.Before;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.dom.agreement.AgreementType;
import org.estatio.dom.agreement.AgreementTypes;
import org.estatio.dom.agreement.Agreements;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.Leases;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.lease.LeaseForKalPoison001;
import org.estatio.fixture.lease.LeaseForOxfMediaX002;
import org.estatio.fixture.lease.LeaseForOxfMiracl005;
import org.estatio.fixture.lease.LeaseForOxfPoison003;
import org.estatio.fixture.lease.LeaseForOxfPret004;
import org.estatio.fixture.lease.LeaseForOxfTopModel001;
import org.estatio.integtests.EstatioIntegrationTest;

public class AgreementTypesTest extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        runScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());

                // 5 oxford leases, 1 kal
                executionContext.executeChild(this, new LeaseForOxfTopModel001());
                executionContext.executeChild(this, new LeaseForOxfMediaX002());
                executionContext.executeChild(this, new LeaseForOxfPoison003());
                executionContext.executeChild(this, new LeaseForOxfPret004());
                executionContext.executeChild(this, new LeaseForOxfMiracl005());
                executionContext.executeChild(this, new LeaseForKalPoison001());
            }
        });

        lease = leases.findLeaseByReference(LeaseForOxfTopModel001.LEASE_REFERENCE);
    }

    @Inject
    Agreements agreements;

    @Inject
    AgreementTypes agreementTypes;

    @Inject
    Leases leases;

    Lease lease;

    public static class Test extends AgreementTypesTest {

        @org.junit.Test
        public void happyCase() throws Exception {
            AgreementType agreementType = agreementTypes.find(lease.getType().getTitle());
            assertThat(agreementType, is(lease.getType()));
        }
    }

}
