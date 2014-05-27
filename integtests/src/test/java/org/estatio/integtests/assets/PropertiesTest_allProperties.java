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
package org.estatio.integtests.assets;

import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import org.estatio.dom.asset.Properties;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.Unit;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertiesAndUnitsForKal;
import org.estatio.fixture.asset.PropertiesAndUnitsForOxf;
import org.estatio.fixture.party.*;
import org.estatio.integtests.EstatioIntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PropertiesTest_allProperties extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        scenarioExecution().install(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                execute(new EstatioBaseLineFixture(), executionContext);

                execute(new PersonForJohnDoe(), executionContext);
                execute(new PersonForLinusTorvalds(), executionContext);

                execute(new OrganisationAndCommunicationChannelsForHelloWorld(), executionContext);
                execute(new PropertiesAndUnitsForOxf(), executionContext);

                execute(new OrganisationAndCommunicationChannelsForAcme(), executionContext);
                execute(new PropertiesAndUnitsForKal(), executionContext);

                execute(new OrganisationAndCommunicationChannelsForTopModel(), executionContext);
                execute(new OrganisationAndCommunicationChannelsForMediaX(), executionContext);
                execute(new OrganisationAndCommunicationChannelsForPoison(), executionContext);
                execute(new OrganisationAndCommunicationChannelsForPret(), executionContext);
                execute(new OrganisationAndCommunicationChannelsForMiracle(), executionContext);
            }
        });
    }

    @Inject
    private Properties properties;

    @Test
    public void whenReturnsInstance_thenCanTraverseUnits() throws Exception {
        // when
        List<Property> allProperties = properties.allProperties();
        // then
        Property property = allProperties.get(0);

        // and when
        Set<Unit> units = property.getUnits();
        // not sure why this is there; this is as much a test of the fixture as of the code
        assertThat(units.size(), is(25));
    }

}
