package org.estatio.dom.asset;

import java.util.List;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.query.QueryDefault;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.lease.UnitForLease;
import org.estatio.dom.utils.StringUtils;

public class Units extends EstatioDomainService<Unit> {

    private final Class<? extends Unit> unitClass;
    
    public Units() {
        super(Units.class, Unit.class);
        this.unitClass = UnitForLease.class;
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(name="Assets", sequence = "1")
    public Unit newUnit(final @Named("Reference") String reference, final @Named("Name") String name) {
        return newUnit(reference, name, UnitType.BOUTIQUE);
    }

    @Hidden
    public Unit newUnit(final String reference, final String name, final UnitType type) {
        final Unit unit = newTransientInstance(unitClass);
        unit.setReference(reference);
        unit.setName(name);
        unit.setUnitType(type);
        persist(unit);
        return unit;
    }

    // //////////////////////////////////////
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @ActionSemantics(Of.SAFE)
    @MemberOrder(name="Assets", sequence = "2")
    public List<Unit> findUnitsByReference(final @Named("Reference") String reference) {
        // this currently only looks for UnitsForLease, and no other subtypes (none existent at time of writing)
        return (List)allMatches(queryForFindUnitsForLeaseByReference(reference));
    }

    @ActionSemantics(Of.SAFE)
    @Hidden
    public Unit findUnitByReference(final String reference) {
        // this currently only looks for UnitsForLease, and no other subtypes (none existent at time of writing)
        return firstMatch(queryForFindUnitsForLeaseByReference(reference));
    }

    private QueryDefault<UnitForLease> queryForFindUnitsForLeaseByReference(String reference) {
        return new QueryDefault<UnitForLease>(UnitForLease.class, "units_findUnitsByReference", "r", StringUtils.wildcardToRegex(reference));
    }

    // //////////////////////////////////////

    @Hidden
    public List<Unit> autoComplete(String searchPhrase) {
        return findUnitsByReference("*".concat(searchPhrase).concat("*"));
    }
    
    // //////////////////////////////////////

    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(name="Assets", sequence = "99")
    public List<Unit> allUnits() {
        return allInstances();
    }

}
