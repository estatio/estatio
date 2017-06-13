package org.estatio.integtests.assets;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.wrapper.InvalidException;

import org.estatio.dom.asset.role.FixedAssetRole;
import org.estatio.dom.asset.role.FixedAssetRoleRepository;
import org.estatio.dom.asset.role.FixedAssetRoleTypeEnum;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.party.PartyRepository;
import org.estatio.dom.party.Party;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForKalNl;
import org.estatio.fixture.party.OrganisationForAcmeNl;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class FixedAsset_IntegTest extends EstatioIntegrationTest {

    @Inject
    PartyRepository partyRepository;

    @Inject
    PropertyRepository properties;

    @Inject
    FixedAssetRoleRepository fixedAssetRoles;

    Party party;

    Property property;

    @Before
    public void setupData() {
        runScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
                executionContext.executeChild(this, new PropertyForKalNl());
            }
        });
    }

    @Before
    public void setUp() {
        party = partyRepository.findPartyByReferenceOrNull(OrganisationForAcmeNl.REF);
        assertNotNull(party);

        property = properties.findPropertyByReference(PropertyForKalNl.REF);
        assertThat(property.getReference(), is(PropertyForKalNl.REF));

        List<FixedAssetRole> allFixedAssetRoles = fixedAssetRoles.findAllForProperty(property);
        assertThat(allFixedAssetRoles.size(), is(2));
        assertThat(allFixedAssetRoles.get(0).getStartDate(), is(new LocalDate(1999, 1, 1)));
        assertNull(allFixedAssetRoles.get(1).getStartDate());
    }

    public static class NewRole extends FixedAsset_IntegTest {

        @Test
        public void happyCase() throws Exception {
            // when
            wrap(property).newRole(
                    FixedAssetRoleTypeEnum.PROPERTY_OWNER, party, new LocalDate(2014, 1, 1), new LocalDate(2014, 12, 31));
            wrap(property).newRole(FixedAssetRoleTypeEnum.PROPERTY_OWNER, party, new LocalDate(2015, 1, 1), null);

            // then
            assertThat(fixedAssetRoles.findAllForProperty(property).size(), is(4));
        }

        @Test(expected = InvalidException.class)
        public void sadCase() throws Exception {
            // when
            wrap(property).newRole(
                    FixedAssetRoleTypeEnum.PROPERTY_OWNER, party, new LocalDate(2014, 1, 1), new LocalDate(2014, 12, 31));
            wrap(property).newRole(FixedAssetRoleTypeEnum.PROPERTY_OWNER, party, new LocalDate(2014, 12, 31), null);

            // then
            assertThat(fixedAssetRoles.findAllForProperty(property).size(), is(2));
        }
    }
}
