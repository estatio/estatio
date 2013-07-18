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
package org.estatio.integration.glue.lease;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import com.google.common.collect.Lists;

import cucumber.api.Transform;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.deps.com.thoughtworks.xstream.annotations.XStreamConverter;

import org.joda.time.LocalDate;

import org.apache.isis.core.specsupport.specs.CukeGlueAbstract;
import org.apache.isis.core.specsupport.specs.V;

import org.estatio.dom.agreement.Agreement;
import org.estatio.dom.agreement.AgreementRole;
import org.estatio.dom.agreement.AgreementRoleType;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.party.Party;
import org.estatio.integration.spectransformers.ERD;
import org.estatio.integration.spectransformers.ETO;

public class LeaseGlue_roles extends CukeGlueAbstract {

    public static class AgreementRoleDesc {
        @XStreamConverter(ERD.AgreementRoleType.class) private AgreementRoleType type;
        @XStreamConverter(V.LyyyyMMdd.class) private LocalDate startDate;
        @XStreamConverter(V.LyyyyMMdd.class) private LocalDate endDate;
        @XStreamConverter(ETO.Lease.class) private Lease agreement;
        @XStreamConverter(ETO.Party.class) private Party party;
    }
    
    
    
    // //////////////////////////////////////

    @Given(".*lease has no.* roles$")
    public void given_lease_has_no_roles() throws Throwable {
        Lease lease = getVar("lease", null, Lease.class);
        assertThat(lease.getRoles().size(), equalTo(0));
    }

    @Given("^the lease.* roles collection contains:$")
    public void given_the_lease_s_roles_collection_contains(final List<AgreementRoleDesc> listOfActuals) throws Throwable {
        final Lease lease = getVar("lease", null, Lease.class);
        assertThat(lease.getRoles().isEmpty(), is(true));
        for (AgreementRoleDesc ard : listOfActuals) {
            lease.addRole(ard.type, ard.party, ard.startDate, ard.endDate);
        }
    }

    @When("^.* add.* agreement role.*type \"([^\"]*)\".* start date \"([^\"]*)\".* end date \"([^\"]*)\".* this party$")
    public void when_add_agreement_role_with_type_with_start_date_and_end_date(
            @Transform(ERD.AgreementRoleType.class) final AgreementRoleType type, 
            @Transform(V.LyyyyMMdd.class) final LocalDate startDate, 
            @Transform(V.LyyyyMMdd.class) final LocalDate endDate) throws Throwable {
      
        nextTransaction();
        
        final Lease lease = getVar("lease", null, Lease.class);
        final Party party = getVar("party", null, Party.class);

        try {
            wrap(lease).addRole(type, party, startDate, endDate);
        } catch(Exception ex) {
            putVar("exception", "exception", ex);
        }
    }

    @When("^.*remove.* agreement role.*type \"([^\"]*)\".* start date \"([^\"]*)\".*  party \"([^\"]*)\"$")
    public void when_remove_agreement_role_with_type_with_start_date_and_party(
            @Transform(ERD.AgreementRoleType.class) final AgreementRoleType type, 
            @Transform(V.LyyyyMMdd.class) final LocalDate startDate, 
            @Transform(ETO.Party.class) final Party party) throws Throwable {
      
        nextTransaction();

        final Lease lease = getVar("lease", null, Lease.class);

        final AgreementRole existingRole = findAgreementRole(lease, type, startDate, party);
        assertThat("Could not locate role in lease", existingRole, is(not(nullValue())));
        
        try {
            wrap(lease).removeRole(existingRole);
        } catch(Exception ex) {
            putVar("exception", "exception", ex);
        }
    }

    @When("^.* remove.* (\\d+).* agreement role$")
    public void when_remove_nth_agreement_role(
            final int index) throws Throwable {

        nextTransaction();

        final Lease lease = getVar("lease", null, Lease.class);

        final AgreementRole existingRole = findAgreementRole(lease, index);
        assertThat("Could not locate role in lease", existingRole, is(not(nullValue())));
        
        try {
            wrap(lease).removeRole(existingRole);
        } catch(Exception ex) {
            putVar("exception", "exception", ex);
        }
    }

    /**
     * @param index - 1-based index into {@link Agreement#getRoles()}
     */
    private AgreementRole findAgreementRole(final Agreement<?> agreement, int index) {
        int i = 0;
        for (AgreementRole ar : agreement.getRoles()) {
            if(++i == index) {
                return ar;
            }
        }
        return null;
    }

    private AgreementRole findAgreementRole(final Agreement<?> agreement, final AgreementRoleType type, final LocalDate startDate, final Party party) {
        for (AgreementRole ar : agreement.getRoles()) {
            if(ar.getType() == type && ar.getParty() == party && ar.getAgreement() == agreement && ar.getStartDate() == startDate) {
                return ar;
            }
        }
        return null;
    }


    // //////////////////////////////////////

    @Then("^.*lease's roles collection should contain:$")
    public void then_leases_roles_collection_should_contain(
            final List<AgreementRoleDesc> listOfExpecteds) throws Throwable {
        
        nextTransaction();

        final Lease lease = getVar("lease", null, Lease.class);
        
        final SortedSet<AgreementRole> roles = lease.getRoles();
        final ArrayList<AgreementRole> rolesList = Lists.newArrayList(roles);
        assertTableEquals(listOfExpecteds, rolesList);
    }


}
