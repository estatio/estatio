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
import javax.inject.Inject;
import org.estatio.dom.asset.Properties;
import org.estatio.dom.asset.Property;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForKal;
import org.estatio.fixture.asset.PropertyForOxf;
import org.estatio.fixture.party.*;
import org.estatio.integtests.EstatioIntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class PropertiesTest_findProperties extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        scenarioExecution().install(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                execute(new PropertyForOxf(), executionContext);
                execute(new PropertyForKal(), executionContext);
            }
        });
    }

    @Inject
    private Properties properties;

    @Test
    public void withReference() throws Exception {
        final List<Property> props = properties.findProperties("OXF");
        assertNotNull(props);
        assertThat(props.size(), is(1));
    }

    @Test
    public void withName() throws Exception {
        final List<Property> props = properties.findProperties("Oxford Super Mall");
        assertNotNull(props);
        assertThat(props.size(), is(1));
    }
    
    @Test
    public void withWildcard() throws Exception {
        final List<Property> props = properties.findProperties("Oxford*");
        assertNotNull(props);
        assertThat(props.size(), is(1));
    }
    
    @Test
    public void withWildcard_returningMultiple() throws Exception {
        final List<Property> props = properties.findProperties("*");
        assertNotNull(props);
        assertThat(props.size(), is(2));
    }

}
