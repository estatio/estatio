/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
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
package org.estatio.integration.tests.assets;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.estatio.dom.asset.FixedAssetRole;
import org.estatio.dom.asset.FixedAssetRoleType;
import org.estatio.dom.asset.FixedAssetRoles;
import org.estatio.dom.asset.Properties;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.fixture.EstatioTransactionalObjectsFixture;
import org.estatio.integration.tests.EstatioIntegrationTest;

public class PropertiesTest_finders extends EstatioIntegrationTest {

    private Properties properties;
    private Parties parties;
    private FixedAssetRoles fixedAssetRoles;

    @BeforeClass
    public static void setupTransactionalData() {
        scenarioExecution().install(new EstatioTransactionalObjectsFixture());
    }

    @Before
    public void setUp() throws Exception {
        properties = service(Properties.class);
        parties = service(Parties.class);
        fixedAssetRoles = service(FixedAssetRoles.class);
    }
    
    @Test
    public void findPropertiesByReference() throws Exception {
        assertNotNull(properties.findPropertiesByReference("OXF"));
    }

    @Test
    public void allProperties_property_getUnits() throws Exception {
        List<Property> allProperties = properties.allProperties();
        Property property = allProperties.get(0);
        Set<Unit> units = property.getUnits();
        assertThat(units.size(), is(25));
    }

    @Test
    public void findPropertyByReference() throws Exception {
        Assert.assertNotNull(properties.findPropertyByReference("OXF"));
    }

    @Test
    public void findRole() throws Exception {
        Party party = parties.findPartyByReference("HELLOWORLD");
        Property property = properties.findPropertyByReference("OXF");
        FixedAssetRole propertyActor = fixedAssetRoles.findRole(property, party, FixedAssetRoleType.PROPERTY_OWNER);
        Assert.assertNotNull(propertyActor);
    }

}
