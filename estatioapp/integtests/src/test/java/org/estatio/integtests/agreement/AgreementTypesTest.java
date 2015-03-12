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
import org.estatio.fixture.lease.LeaseForKalPoison001Nl;
import org.estatio.fixture.lease._LeaseForOxfMediaX002Gb;
import org.estatio.fixture.lease._LeaseForOxfMiracl005Gb;
import org.estatio.fixture.lease._LeaseForOxfPoison003Gb;
import org.estatio.fixture.lease._LeaseForOxfPret004Gb;
import org.estatio.fixture.lease._LeaseForOxfTopModel001Gb;
import org.estatio.integtests.EstatioIntegrationTest;

public class AgreementTypesTest extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());

                // 5 oxford leases, 1 kal
                executionContext.executeChild(this, new _LeaseForOxfTopModel001Gb());
                executionContext.executeChild(this, new _LeaseForOxfMediaX002Gb());
                executionContext.executeChild(this, new _LeaseForOxfPoison003Gb());
                executionContext.executeChild(this, new _LeaseForOxfPret004Gb());
                executionContext.executeChild(this, new _LeaseForOxfMiracl005Gb());
                executionContext.executeChild(this, new LeaseForKalPoison001Nl());
            }
        });

        lease = leases.findLeaseByReference(_LeaseForOxfTopModel001Gb.REF);
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
