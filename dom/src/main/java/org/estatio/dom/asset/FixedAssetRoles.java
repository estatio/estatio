package org.estatio.dom.asset;

import java.util.List;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotContributed;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.filter.Filter;
import org.estatio.dom.party.Party;

import org.joda.time.LocalDate;

import com.google.common.base.Objects;

@Hidden
@Named("Asset Roles")
public class FixedAssetRoles extends AbstractFactoryAndRepository {

    @Override
    public String getId() {
        return "fixedAssetRoles";
    }

    public String iconName() {
        return "FixedAssetRole";
    }

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

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    @NotContributed
    public FixedAssetRole findRole(final FixedAsset asset, final Party party, final FixedAssetRoleType type) {
        return firstMatch(FixedAssetRole.class, new Filter<FixedAssetRole>() {
            @Override
            public boolean accept(final FixedAssetRole role) {
                return Objects.equal(role.getAsset(), asset) && Objects.equal(role.getParty(), party);
            }
        });
    }

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "3")
    @NotContributed
    public FixedAssetRole findRole(final FixedAsset asset, final Party party, final FixedAssetRoleType type, final @Named("Start Date") LocalDate startDate, final @Named("End Date") LocalDate endDate) {
        return firstMatch(FixedAssetRole.class, new Filter<FixedAssetRole>() {
            @Override
            public boolean accept(final FixedAssetRole role) {
                return role.getAsset().equals(asset) && 
                       role.getParty().equals(party) && 
                       role.getType().equals(type) && 
                       role.getStartDate().equals(startDate) &&
                       role.getEndDate().equals(endDate);
            }
        });
    }

    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "4")
    public List<FixedAssetRole> allRoles() {
        return allInstances(FixedAssetRole.class);
    }

}
