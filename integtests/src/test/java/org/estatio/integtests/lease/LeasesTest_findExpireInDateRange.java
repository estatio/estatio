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
package org.estatio.integtests.lease;

import java.util.List;
import javax.inject.Inject;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.Leases;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertiesAndUnitsForAll;
import org.estatio.fixture.lease.LeasesEtcForAll;
import org.estatio.fixture.party.PersonsAndOrganisationsAndCommunicationChannelsForAll;
import org.estatio.integtests.EstatioIntegrationTest;
import org.estatio.integtests.VT;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.CompositeFixtureScript;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LeasesTest_findExpireInDateRange extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        scenarioExecution().install(new CompositeFixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                execute(new EstatioBaseLineFixture(), executionContext);
                execute("parties", new PersonsAndOrganisationsAndCommunicationChannelsForAll(), executionContext);
                execute("properties", new PropertiesAndUnitsForAll(), executionContext);
                execute("leases", new LeasesEtcForAll(), executionContext);
            }
        });
    }

    @Inject
    private Leases leases;

    @Test
    public void whenLeasesExpiringInRange() {
        // given
        final LocalDate startDate = VT.ld(2020, 1, 1);
        final LocalDate endDate   = VT.ld(2030, 1, 1);
        // when
        final List<Lease> matchingLeases = leases.findExpireInDateRange(startDate, endDate);
        // then
        assertThat(matchingLeases.size(), is(4));
    }

}
