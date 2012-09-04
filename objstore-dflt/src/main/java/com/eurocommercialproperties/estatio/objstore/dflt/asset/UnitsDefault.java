package com.eurocommercialproperties.estatio.objstore.dflt.asset;

import java.util.List;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.QueryOnly;
import org.apache.isis.applib.filter.Filter;

import com.eurocommercialproperties.estatio.dom.asset.Property;
import com.eurocommercialproperties.estatio.dom.asset.Unit;
import com.eurocommercialproperties.estatio.dom.asset.UnitType;
import com.eurocommercialproperties.estatio.dom.asset.Units;

public class UnitsDefault extends AbstractFactoryAndRepository implements Units {

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
    @Override
    public Unit newUnit(final String code, String description) {
        return newUnit(code, description, UnitType.BOUTIQUE);
    }
    // }}

    // {{ NewUnit  (hidden)
    @Override
    public Unit newUnit(final String reference, String name, UnitType type) {
        final Unit unit = newTransientInstance(Unit.class);
        unit.setReference(reference);
        unit.setName(name);
        unit.setType(type);
        //getContainer().flush();
        persist(unit);
        return unit;
    }
    // }}

    // {{ AllInstances
    @Override
    @ActionSemantics(Of.SAFE)
    public List<Unit> allInstances() {
    	return allInstances(Unit.class);
    }
    // }}

    // {{ findByReference
    @Override
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public Unit findByReference(@Named("Reference") final String reference) {
        return firstMatch(Unit.class, new Filter<Unit>() {
            @Override
            public boolean accept(final Unit unit) {
                return reference.equals(unit.getReference());
            }
        });
    }

}
