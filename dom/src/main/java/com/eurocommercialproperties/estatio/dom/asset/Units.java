package com.eurocommercialproperties.estatio.dom.asset;

import java.util.List;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.filter.Filter;

@Named("Units")
public class Units extends AbstractFactoryAndRepository {

    // {{ Id, iconName
    @Override
    public String getId() {
        return "units";
    }

    public String iconName() {
        return "Unit";
    }

    // }}

     // {{ NewUnit  (action)
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "1")
    public Unit newUnit(
            final @Named("Reference") String reference, 
            final @Named("Name") String name) {
        return newUnit(reference, name, UnitType.BOUTIQUE);
    }
    // }}

    // {{ NewUnit  (hidden)
    // for use by fixtures
    @Hidden
    public Unit newUnit(
            final String reference, 
            final String name, 
            final UnitType type) {
        final Unit unit = newTransientInstance(Unit.class);
        unit.setReference(reference);
        unit.setName(name);
        unit.setType(type);
        //getContainer().flush();
        persist(unit);
        return unit;
    }
    // }}

    // {{ findByReference
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public Unit findByReference(
            final @Named("Reference") String reference) {
        return firstMatch(Unit.class, new Filter<Unit>() {
            @Override
            public boolean accept(final Unit unit) {
                return reference.equals(unit.getReference());
            }
        });
    }

    // {{ allUnits
    @Prototype
    @ActionSemantics(Of.SAFE)
    public List<Unit> allUnits() {
        return allInstances(Unit.class);
    }
    // }}

}
