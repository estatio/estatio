/*
 *  Copyright 2012-2013 Eurocommercial Properties NV
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
package org.estatio.integtest.specs.agreement;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.List;

import com.google.common.collect.Lists;

import cucumber.api.DataTable;
import cucumber.api.Transform;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.deps.com.thoughtworks.xstream.annotations.XStreamConverter;

import org.joda.time.LocalDate;

import org.estatio.dom.agreement.Agreement;
import org.estatio.dom.agreement.AgreementRole;
import org.estatio.dom.agreement.AgreementRoleType;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.party.Organisation;
import org.estatio.dom.party.Party;
import org.estatio.fixture.EstatioTransactionalObjectsFixture;
import org.estatio.integtest.AbstractEstatioCukeStepDefs;
import org.estatio.integtest.specs.C;
import org.estatio.integtest.specs.D;
import org.estatio.integtest.specs.EstatioScenario;

public class AgreementStepDefs extends AbstractEstatioCukeStepDefs {

    public AgreementStepDefs(EstatioScenario scenario) {
        super(scenario);
    }

    @Given(".*usual transactional data$")
    public void given_transactional_data() throws Throwable {
        app.install(new EstatioTransactionalObjectsFixture());
    }


    @Given(".*there is.* lease \"([^\"]*)\"$")
    public void given_lease(String leaseReference) throws Throwable {
        
        final Lease lease = app.leases.findLeaseByReference(leaseReference);
        scenario.put("lease", leaseReference, lease);
    }

    
    @Given(".*there is.* organisation \"([^\"]*)\"$")
    public void given_organisation(String organisationReference) throws Throwable {
        final Organisation organisation = app.organisations.findOrganisation(organisationReference);
        scenario.put("organisation", organisationReference, organisation);
    }

    @Given(".*lease has no.* roles$")
    public void assuming_lease_has_no_roles() throws Throwable {
        Lease lease = scenario.get("lease", null, Lease.class);
        assertThat(lease.getRoles().size(), is(0));
    }

    // //////////////////////////////////////

    
    @When("^.* add.* agreement role.*type \"([^\"]*)\".* start date \"([^\"]*)\".* end date \"([^\"]*)\".* party \"([^\"]*)\"$")
    public void add_agreement_role_with_type_with_start_date_and_end_date(
            @Transform(D.AgreementRoleType.class) AgreementRoleType type, 
            @Transform(C.LocalDate.class) LocalDate startDate, 
            @Transform(C.LocalDate.class) LocalDate endDate,
            @Transform(D.Organisation.class) Organisation organisation) throws Throwable {
        
        Lease lease = scenario.get("lease", null, Lease.class);
        lease.addRole(organisation, type, startDate, endDate);
    }
    
    // //////////////////////////////////////

    @Then("^.*lease's roles collection should contain:$")
    public void leases_roles_collection_should_contain(
            final List<AgreementRoleDesc> expected) throws Throwable {
        final Lease lease = scenario.get("lease", null, Lease.class);
        final List<AgreementRole> actual = Lists.newArrayList(lease.getRoles());
        

        assertThat(actual.size(), is(expected.size()));

        for (int i=0; i<actual.size(); i++) {
            
            // TODO: do this reflectively as a utility, using expectedTableColumns...
            final DataTable expectedTable = DataTable.create(expected);
            final String[] expectedTableColumns = expectedTable.topCells().toArray(new String[]{});
            
            assertThat(actual.get(i).getType(), is(expected.get(i).type));
            assertThat(actual.get(i).getAgreement(), is((Agreement)expected.get(i).agreement));
            assertThat(actual.get(i).getParty(), is((Party)expected.get(i).party));
            if(!"null".equals(expected.get(i).startDate)) {
                assertThat(actual.get(i).getStartDate(), is(expected.get(i).startDate));
            } else {
                assertThat(actual.get(i).getStartDate(), is(nullValue()));
            }
            if(!"null".equals(expected.get(i).endDate)) {
                assertThat(actual.get(i).getEndDate(), is(expected.get(i).endDate));
            } else {
                assertThat(actual.get(i).getEndDate(), is(nullValue()));
            }
        }
    }

    
    public static class AgreementRoleDesc {
        @XStreamConverter(D.AgreementRoleType.class) private AgreementRoleType type;
        @XStreamConverter(C.LocalDate.class) private LocalDate startDate;
        @XStreamConverter(C.LocalDate.class) private LocalDate endDate;
        @XStreamConverter(D.Lease.class) private Lease agreement;
        @XStreamConverter(D.Organisation.class) private Organisation party;
    }

    
    // //////////////////////////////////////
    // boilerplate
    
    @Before
    public void beginTran() {
        super.beginTran();
    }

    @After
    public void endTran(cucumber.api.Scenario sc) {
        super.endTran(sc);
    }

}
