package org.estatio.dom.asset;

import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotContributed;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.query.QueryDefault;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.party.Party;

@Hidden
@Named("Asset Roles")
public class FixedAssetRoles extends EstatioDomainService {

    public FixedAssetRoles() {
        super(FixedAssetRoles.class, FixedAssetRole.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "1")
    @NotContributed
    public FixedAssetRole newRole(final FixedAsset asset, final Party party, final FixedAssetRoleType type, final @Named("Start Date") LocalDate startDate, final @Named("End Date") LocalDate endDate) {
        final FixedAssetRole role = newTransientInstance(FixedAssetRole.class);
        role.setParty(party);
        role.setAsset(asset);
        role.setStartDate(startDate);
        role.setEndDate(endDate);
        role.setType(type);
        persist(role);
        return role;
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    @NotContributed
    public FixedAssetRole findRole(final FixedAsset asset, final Party party, final FixedAssetRoleType type) {
        return firstMatch(queryForFindByPropertyParty(asset, party, type));
    }

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "3")
    @NotContributed
    public FixedAssetRole findRole(final FixedAsset asset, final Party party, final FixedAssetRoleType type, final @Named("Start Date") LocalDate startDate, final @Named("End Date") LocalDate endDate) {
        // TODO: need to also search by dates
        return firstMatch(queryForFindByPropertyParty(asset, party, type));
    }

    private static QueryDefault<FixedAssetRole> queryForFindByPropertyParty(FixedAsset asset, Party party, FixedAssetRoleType type) {
        return new QueryDefault<FixedAssetRole>(FixedAssetRole.class, "fixedAssetRole_findByAssetParty", "asset", asset, "party", party, "type", type);
    }

    // //////////////////////////////////////

    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "4")
    public List<FixedAssetRole> allRoles() {
        return allInstances(FixedAssetRole.class);
    }

    
    // //////////////////////////////////////




    
}
