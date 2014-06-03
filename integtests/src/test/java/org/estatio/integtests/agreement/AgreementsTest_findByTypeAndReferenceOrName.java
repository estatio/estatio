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
import org.estatio.dom.agreement.Agreement;
import org.estatio.dom.agreement.AgreementType;
import org.estatio.dom.agreement.AgreementTypes;
import org.estatio.dom.agreement.Agreements;
import org.estatio.fixture.lease.*;
import org.estatio.integtests.EstatioIntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class AgreementsTest_findByTypeAndReferenceOrName extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        scenarioExecution().install(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {

                // 5 oxford leases, 1 kal
                execute(new LeaseForOxfTopModel001(), executionContext);
                execute(new LeaseForOxfMediaX002(), executionContext);
                execute(new LeaseForOxfPoison003(), executionContext);
                execute(new LeaseForOxfPret004(), executionContext);
                execute(new LeaseForOxfMiracl005(), executionContext);
                execute(new LeaseForKalPoison001(), executionContext);
            }
        });
    }

    @Test
    public void whenPresent() {
        final AgreementType type = agreementTypes.find("Lease");
        assertNotNull(type);
        final List<Agreement> results = agreements.findByTypeAndReferenceOrName(type, ".*OXF.*");
        assertThat(results.size(), is(5));
    }

    @Inject
    private Agreements agreements;
    @Inject
    private AgreementTypes agreementTypes;


}
