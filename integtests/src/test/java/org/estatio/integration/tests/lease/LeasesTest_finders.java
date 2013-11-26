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
package org.estatio.integration.tests.lease;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.estatio.dom.asset.Properties;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.Leases;
import org.estatio.fixture.EstatioTransactionalObjectsFixture;
import org.estatio.integration.tests.EstatioIntegrationTest;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class LeasesTest_finders extends EstatioIntegrationTest {

    @BeforeClass
    public static void setupTransactionalData() {
        scenarioExecution().install(new EstatioTransactionalObjectsFixture());
    }

    private Leases leases;

    private Properties properties;

    @Before
    public void setup() {
        leases = service(Leases.class);
        properties = service(Properties.class);
    }

    @Test
    public void findLeaseByReference() throws Exception {
        final Lease lease = leases.findLeaseByReference("OXF-TOPMODEL-001");
        Assert.assertEquals("OXF-TOPMODEL-001", lease.getReference());
    }

    @Test
    public void findLeasesByReference_whenWildcard() throws Exception {
        final List<Lease> matchingLeases = leases.findLeases("OXF*");
            assertThat(matchingLeases.size(), is(5));
    }

    @Test
    public void findLeaseByProperty() throws Exception {
        final List<Lease> matchingLeases = leases.findLeasesByProperty(properties.findPropertyByReference("OXF"));
        assertThat(matchingLeases.size(), is(4));
    }

    @Test
    public void findLeasesAboutToExpireOnDate() throws Exception {
        final List<Lease> matchingLeases = leases.findAboutToExpireOnDate(new LocalDate(2020, 1, 1));
        assertThat(matchingLeases.size(), is(4));
    }

}
