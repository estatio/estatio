package org.estatio.dom.asset;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.NotContributed;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.party.Party;

@Hidden
public class FixedAssetRoles extends EstatioDomainService<FixedAssetRole> {

    public FixedAssetRoles() {
        super(FixedAssetRoles.class, FixedAssetRole.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.NON_IDEMPOTENT)
    public FixedAssetRole newRole(final FixedAsset asset, final Party party, final FixedAssetRoleType type, LocalDate startDate, LocalDate endDate) {
        final FixedAssetRole role = newTransientInstance();
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
    public FixedAssetRole findRole(final FixedAsset asset, final Party party, final FixedAssetRoleType type) {
        return firstMatch(newQueryDefault("findRoleByAssetAndPartyAndType", "asset", asset, "party", party, "type", type));
    }

    @ActionSemantics(Of.SAFE)
    public FixedAssetRole findRole(final FixedAsset asset, final Party party, final FixedAssetRoleType type, final LocalDate startDate, final LocalDate endDate) {
        return firstMatch("findRoleByAssetAndPartyAndType", "asset", asset, "party", party, "type", type);
    }
    
    @ActionSemantics(Of.SAFE)
    @NotContributed
    public FixedAssetRole findByAssetAndPartyAndTypeAndStartDate(final FixedAsset asset, final Party party, final FixedAssetRoleType type, final LocalDate startDate) {
        return firstMatch("findRoleByAssetAndPartyAndTypeAndStartDate", "asset", asset, "party", party, "type", type, "startDate", startDate);
    }
    
    @ActionSemantics(Of.SAFE)
    @NotContributed
    public FixedAssetRole findByAssetAndPartyAndTypeAndEndDate(final FixedAsset asset, final Party party, final FixedAssetRoleType type, final LocalDate endDate) {
        return firstMatch("findRoleByAssetAndPartyAndTypeAndEndDate", "asset", asset, "party", party, "type", type, "endDate", endDate);
    }

}
