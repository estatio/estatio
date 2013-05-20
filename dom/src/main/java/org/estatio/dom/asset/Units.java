package org.estatio.dom.asset;

import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;

@Named("Units")
public class Units extends AbstractFactoryAndRepository {

    private final Class<? extends Unit> unitClass;
    
    public Units(Class<? extends Unit> unitClass) {
        this.unitClass = unitClass;
    }
    
    @Override
    public String getId() {
        return "units";
    }

    public String iconName() {
        return "Unit";
    }

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

    
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public List<Unit> findUnitsByReference(final @Named("Reference") String reference) {
        throw new NotImplementedException();
    }

    @ActionSemantics(Of.SAFE)
    @Hidden
    public Unit findUnitByReference(final @Named("Reference") String reference) {
        throw new NotImplementedException();
    }

    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "3")
    public List<Unit> allUnits() {
        return allInstances(Unit.class);
    }

    @Hidden
    public List<Unit> autoComplete(String searchPhrase) {
        return findUnitsByReference("*".concat(searchPhrase).concat("*"));
    }

}
