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
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.asset.fixtures.property.personas.PropertyAndUnitsAndOwnerAndManagerForKalNl;
import org.estatio.module.asset.integtests.AssetModuleIntegTestAbstract;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.fixtures.organisation.enums.Organisation_enum;

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
                executionContext.executeChild(this, new PropertyAndUnitsAndOwnerAndManagerForKalNl());
            }
        });
    }

    @Before
    public void setUp() {
        party = Organisation_enum.AcmeNl.findUsing(serviceRegistry);
        property = Property_enum.KalNl.findUsing(serviceRegistry);

        List<FixedAssetRole> allFixedAssetRoles = fixedAssetRoles.findAllForProperty(property);
        assertThat(allFixedAssetRoles.size(), is(2));

        assertNull(allFixedAssetRoles.get(0).getStartDate());
        assertNull(allFixedAssetRoles.get(0).getEndDate());
        assertThat(allFixedAssetRoles.get(0).getType(), is(FixedAssetRoleTypeEnum.PROPERTY_OWNER) );

        assertThat(allFixedAssetRoles.get(1).getStartDate(), is(ld(2003,12,1)));
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
        public void when_comes_after() throws Exception {

            // given
            wrap(property).newRole(
                    FixedAssetRoleTypeEnum.PROPERTY_CONTACT, party, ld(2014, 1, 1), ld(2014, 12, 31));
            assertThat(fixedAssetRoles.findAllForProperty(property).size(), is(3));

            // when
            wrap(property).newRole(
                    FixedAssetRoleTypeEnum.PROPERTY_CONTACT, party, ld(2015, 1, 1), null);

            // then
            assertThat(fixedAssetRoles.findAllForProperty(property).size(), is(4));
        }

        @Test
        public void when_overlaps() throws Exception {

            // expect
            expectedExceptions.expect(InvalidException.class);
            expectedExceptions.expectMessage("The provided dates overlap with a current role of this type and party.");

            // when
            wrap(property).newRole(
                    FixedAssetRoleTypeEnum.PROPERTY_OWNER, party, ld(2014, 1, 1), ld(2014, 12, 31));
            wrap(property).newRole(
                    FixedAssetRoleTypeEnum.PROPERTY_OWNER, party, ld(2014, 12, 31), null);

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
