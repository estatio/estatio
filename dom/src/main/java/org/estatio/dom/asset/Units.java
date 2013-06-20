package org.estatio.dom.asset;

import java.util.List;

import org.apache.commons.lang.NotImplementedException;

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

@Named("Units")

public class Units extends EstatioDomainService {

    private final Class<? extends Unit> unitClass;
    
    public Units() {
        super(Units.class, Unit.class);
        this.unitClass = UnitForLease.class;
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "1")
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
    @MemberOrder(sequence = "2")
    public List<Unit> findUnitsByReference(final @Named("Reference") String reference) {
        return (List)allMatches(queryForFindByReference(reference));
    }

    @ActionSemantics(Of.SAFE)
    @Hidden
    public Unit findUnitByReference(final @Named("Reference") String reference) {
        return firstMatch(queryForFindByReference(reference));
    }

    private static QueryDefault<UnitForLease> queryForFindByReference(String reference) {
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
    @MemberOrder(sequence = "3")
    public List<Unit> allUnits() {
        return allInstances(Unit.class);
    }

}
