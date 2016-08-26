/*
 *
 *  Copyright 2012-2015 Eurocommercial Properties NV
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

package org.estatio.integtests.party;

import java.util.SortedSet;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.wrapper.InvalidException;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.apptenancy.EstatioApplicationTenancyRepository;
import org.estatio.dom.geography.CountryRepository;
import org.estatio.dom.party.Organisation;
import org.estatio.dom.party.OrganisationPreviousName;
import org.estatio.dom.party.OrganisationRepository;
import org.estatio.dom.party.PartyRepository;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.geography.CountriesRefData;
import org.estatio.fixture.numerator.NumeratorForOrganisationFra;
import org.estatio.fixture.party.OrganisationForTopModelGb;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class OrganisationTest extends EstatioIntegrationTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Inject
    PartyRepository partyRepository;

    @Inject
    EstatioApplicationTenancyRepository estatioApplicationTenancyRepository;

    @Inject
    CountryRepository countryRepository;

    @Inject
    OrganisationRepository organisationRepository;

    Organisation organisation;

    public static class ChangeName extends OrganisationTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override protected void execute(final ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());
                    executionContext.executeChild(this, new OrganisationForTopModelGb());
                }
            });

            organisation = (Organisation) partyRepository.findPartyByReference(OrganisationForTopModelGb.REF);
        }

        @Test
        public void happyCase() throws Exception {
            // given
            SortedSet<OrganisationPreviousName> previousNames = organisation.getPreviousNames();
            assertThat(previousNames).isEmpty();

            // when
            wrap(organisation).changeName("New name", new LocalDate(2012, 1, 1));
            previousNames = organisation.getPreviousNames();

            // then
            assertThat(previousNames).hasSize(1);
        }

        @Test
        public void endDateInFuture() throws Exception {
            // then
            exception.expect(InvalidException.class);
            exception.expectMessage("You can not select a future end date");

            // when
            wrap(organisation).changeName("New name", new LocalDate(2050, 1, 1));
        }

        @Test
        public void newNameIsSameAsCurrentName() throws Exception {
            // then
            exception.expect(InvalidException.class);
            exception.expectMessage("New name must be different from the current name");

            // when
            wrap(organisation).changeName(organisation.getName(), new LocalDate(2050, 1, 1));
        }

        @Test
        public void changeTest() throws Exception {
            // given
            Assertions.assertThat(organisation.getChamberOfCommerceCode()).isEqualTo(null);
            String cocCode = "0608869419";

            // when
            wrap(organisation).change(null, null, cocCode);

            //then
            Assertions.assertThat(organisation.getChamberOfCommerceCode()).isEqualTo(cocCode);
            Assertions.assertThat(organisation.default2Change()).isEqualTo(cocCode);
        }
    }

    public static class NewOrganisationUsingNumerator extends OrganisationTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override protected void execute(final ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());
                    executionContext.executeChild(this, new NumeratorForOrganisationFra());
                }
            });

        }

        Organisation organisation1;
        Organisation organisation2;

        @Test
        public void newOrganisationUsingNumerator() throws Exception {

            // given
            ApplicationTenancy applicationTenancyForFra = estatioApplicationTenancyRepository.findOrCreateTenancyFor(countryRepository.findCountry(CountriesRefData.FRA));

            // when
            organisation1 = wrap(organisationRepository).newOrganisation(null, true, "SOME_NAME", applicationTenancyForFra);
            organisation2 = wrap(organisationRepository).newOrganisation(null, true, "SOME_OTHER_NAME", applicationTenancyForFra);

            // then
            Assertions.assertThat(organisation1.getReference()).isEqualTo("FRCL0001");
            Assertions.assertThat(organisation2.getReference()).isEqualTo("FRCL0002");

        }

        @Test
        public void noNumeratorFound() throws Exception {

            // given
            ApplicationTenancy applicationTenancyForGbr = estatioApplicationTenancyRepository.findOrCreateTenancyFor(countryRepository.findCountry(CountriesRefData.GBR));

            // then
            exception.expect(InvalidException.class);
            exception.expectMessage("No numerator found");

            // when
            wrap(organisationRepository).newOrganisation(null, true, "SOME_NAME", applicationTenancyForGbr);

        }

    }
}
