package org.estatio.dom.asset;

import org.estatio.dom.party.Party;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.query.QueryDefault;

public class FixedAssetRolesJdo extends FixedAssetRoles {

    // //////////////////////////////////////

    @Override
    public FixedAssetRole findRole(final FixedAsset asset, final Party party, final FixedAssetRoleType type, final @Named("Start Date") LocalDate startDate, final @Named("End Date") LocalDate endDate) {
        // TODO: need to also search by dates
        return firstMatch(queryForFindByPropertyParty(asset, party, type));
    }

    // //////////////////////////////////////

    @Override
    public FixedAssetRole findRole(final FixedAsset asset, final Party party, final FixedAssetRoleType type) {
        return firstMatch(queryForFindByPropertyParty(asset, party, type));
    }

    private static QueryDefault<FixedAssetRole> queryForFindByPropertyParty(FixedAsset asset, Party party, FixedAssetRoleType type) {
        return new QueryDefault<FixedAssetRole>(FixedAssetRole.class, "fixedAssetRole_findByAssetParty", "asset", asset, "party", party, "type", type);
    }

}
