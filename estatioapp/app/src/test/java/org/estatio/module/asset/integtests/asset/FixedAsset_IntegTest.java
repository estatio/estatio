package org.estatio.module.asset.integtests.asset;

import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.wrapper.InvalidException;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.asset.dom.role.FixedAssetRole;
import org.estatio.module.asset.dom.role.FixedAssetRoleRepository;
import org.estatio.module.asset.dom.role.FixedAssetRoleTypeEnum;
import org.estatio.module.asset.fixtures.property.enums.PropertyAndUnitsAndOwnerAndManager_enum;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.asset.integtests.AssetModuleIntegTestAbstract;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.fixtures.organisation.enums.OrganisationAndComms_enum;

import static org.hamcrest.CoreMatchers.is;
import static org.incode.module.base.integtests.VT.ld;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class FixedAsset_IntegTest extends AssetModuleIntegTestAbstract {

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
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, PropertyAndUnitsAndOwnerAndManager_enum.KalNl.builder());
            }
        });
    }

    @Before
    public void setUp() {
        party = OrganisationAndComms_enum.AcmeNl.findUsing(serviceRegistry);
        property = Property_enum.KalNl.findUsing(serviceRegistry);

        List<FixedAssetRole> allFixedAssetRoles = fixedAssetRoles.findAllForProperty(property);
        assertThat(allFixedAssetRoles.size(), is(2));

        assertThat(allFixedAssetRoles.get(0).getStartDate(), is(ld(1999,1,1)));
        assertThat(allFixedAssetRoles.get(0).getEndDate(), is(ld(2000,1,1)));
        assertThat(allFixedAssetRoles.get(0).getType(), is(FixedAssetRoleTypeEnum.PROPERTY_OWNER) );

        assertNull(allFixedAssetRoles.get(1).getStartDate());
        assertNull(allFixedAssetRoles.get(1).getEndDate());
        assertThat(allFixedAssetRoles.get(1).getType(), is(FixedAssetRoleTypeEnum.ASSET_MANAGER) );
    }

    public static class NewRole extends FixedAsset_IntegTest {

        @Test
        public void new_type_of_role() throws Exception {
            // when
            wrap(property).newRole(
                    FixedAssetRoleTypeEnum.PROPERTY_CONTACT, party, ld(2014, 1, 1), ld(2014, 12, 31));

            // then
            assertThat(fixedAssetRoles.findAllForProperty(property).size(), is(3));
        }

        @Test
        public void when_comes_directly_after() throws Exception {

            // when
            wrap(property).newRole(
                    FixedAssetRoleTypeEnum.PROPERTY_CONTACT, party, ld(2000,1,1).plusDays(1), null);

            // then
            assertThat(fixedAssetRoles.findAllForProperty(property).size(), is(3));
        }

        @Test
        public void when_comes_sometime_after() throws Exception {

            // when
            wrap(property).newRole(
                    FixedAssetRoleTypeEnum.PROPERTY_CONTACT, party, ld(2015, 1, 1), null);

            // then
            assertThat(fixedAssetRoles.findAllForProperty(property).size(), is(3));
        }

        @Test
        public void when_overlaps() throws Exception {

            // expect
            expectedExceptions.expect(InvalidException.class);
            expectedExceptions.expectMessage("The provided dates overlap with a current role of this type and party.");

            // when
            wrap(property).newRole(
                    FixedAssetRoleTypeEnum.PROPERTY_OWNER, party, ld(2000,1,1), ld(2014, 12, 31));

            // then
            assertThat(fixedAssetRoles.findAllForProperty(property).size(), is(2));
        }

        @Test
        public void when_finishes_before() throws Exception {

            // when
            wrap(property).newRole(
                    FixedAssetRoleTypeEnum.ASSET_MANAGER, party, null, ld(2003,12,1));

            // then
            assertThat(fixedAssetRoles.findAllForProperty(property).size(), is(3));
        }
    }
}
