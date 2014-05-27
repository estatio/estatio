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

import javax.inject.Inject;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseItem;
import org.estatio.dom.lease.LeaseItemType;
import org.estatio.dom.lease.Leases;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForKal;
import org.estatio.fixture.asset.PropertyForOxf;
import org.estatio.fixture.lease.*;
import org.estatio.fixture.party.*;
import org.estatio.integtests.EstatioIntegrationTest;
import org.estatio.integtests.VT;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import static org.junit.Assert.assertNotNull;

public class LeaseTermFixedTest_changeDates extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        scenarioExecution().install(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                execute(new EstatioBaseLineFixture(), executionContext);

                execute(new PersonForJohnDoe(), executionContext);
                execute(new PersonForLinusTorvalds(), executionContext);

                execute(new OrganisationForHelloWorld(), executionContext);
                execute(new PropertyForOxf(), executionContext);

                execute(new OrganisationForAcme(), executionContext);
                execute(new PropertyForKal(), executionContext);


                execute(new OrganisationForTopModel(), executionContext);
                execute(new LeasesEtcForOxfTopModel001(), executionContext);

                execute(new OrganisationForMediaX(), executionContext);
                execute(new LeasesEtcForOxfMediax002(), executionContext);

                execute(new OrganisationForPoison(), executionContext);
                execute(new LeasesEtcForOxfPoison003(), executionContext);
                execute(new LeasesEtcForKalPoison001(), executionContext);

                execute(new OrganisationForPret(), executionContext);
                execute(new LeasesEtcForOxfPret004(), executionContext);

                execute(new OrganisationForMiracle(), executionContext);
                execute(new LeasesEtcForOxfMiracl005(), executionContext);
            }
        });
    }

    @Inject
    private Leases leases;

    private Lease lease;
    private LeaseItem leaseTopModelRentItem;

    @Before
    public void setup() {
        lease = leases.findLeaseByReference("OXF-TOPMODEL-001");
        leaseTopModelRentItem = lease.findItem(LeaseItemType.RENT, VT.ld(2010, 7, 15), VT.bi(1));
        assertNotNull(leaseTopModelRentItem);
    }

    /**
     *<pre>
     *    Given
     *    a lease term with fixed invoicing frequency (eg LeaseTermForFixed, LeaseTermForTax)
     *    with start and end dates
     *    and with at least one invoice
     *
     *    When
     *    I attempt to change the start dates
     *    Or
     *    I attempt to change the end dates
     *
     *    Then
     *    this is disallowed
     *</pre>
     */
    @Ignore("EST-371")
    @Test
    public void xxx() throws Exception {

    }

    /**
     *<pre>
     *    Given
     *     a lease term with fixed invoicing frequency (eg LeaseTermForFixed, LeaseTermForTax)
     *     with start and end dates
     *     and NO invoices
     *
     *     When
     *     I change the start dates
     *     Or
     *     I change the end dates
     *
     *     Then
     *     this is allowed and the date changes accordingly
     *
     *</pre>
     */
    @Ignore("EST-371")
    @Test
    public void yyy() throws Exception {

    }

    /**
     *<pre>
     *     Given
     *     a lease term with some non-fixed invoicing frequency (eg LeaseTermForIndexableRent)
     *     with start and end dates
     *     and at least one invoice
     *
     *     When
     *     I change the start dates
     *     Or
     *     I change the end dates
     *
     *     Then
     *     this is allowed and the date changes accordingly
     *</pre>
     */
    @Ignore("EST-371")
    @Test
    public void zzz() throws Exception {

    }

}
