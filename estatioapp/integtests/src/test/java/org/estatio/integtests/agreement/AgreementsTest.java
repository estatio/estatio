/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.integtests.agreement;

import java.util.List;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.estatio.dom.agreement.Agreement;
import org.estatio.dom.agreement.AgreementRoleTypes;
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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class AgreementsTest extends EstatioIntegrationTest {

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
    AgreementRoleTypes agreementRoleTypes;

    @Inject
    Leases leases;

    Lease lease;

    public static class FindByTypeAndReferenceOrName extends AgreementsTest {

        @Test
        public void whenPresent() throws Exception {
            final AgreementType type = agreementTypes.find("Lease");
            assertNotNull(type);
            final List<Agreement> results = agreements.findByTypeAndReferenceOrName(type, ".*OXF.*");
            assertThat(results.size(), is(5));
        }
    }

    public static class FindAgreementByReference extends AgreementsTest {

        @Test
        public void happyCase() throws Exception {
            Agreement agreement = agreements.findAgreementByReference(lease.getReference());
            assertThat(agreement.getName(), is(lease.getName()));
        }
    }

}
