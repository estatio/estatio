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
import org.estatio.dom.agreement.AgreementType;
import org.estatio.dom.agreement.AgreementTypes;
import org.estatio.dom.agreement.Agreements;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.lease.*;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class AgreementsTest extends EstatioIntegrationTest {

    public static class FindByTypeAndReferenceOrName extends AgreementsTest {

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
        }

        @Inject
        Agreements agreements;
        @Inject
        AgreementTypes agreementTypes;


        @Test
        public void whenPresent() {
            final AgreementType type = agreementTypes.find("Lease");
            assertNotNull(type);
            final List<Agreement> results = agreements.findByTypeAndReferenceOrName(type, ".*OXF.*");
            assertThat(results.size(), is(5));
        }
    }
}
