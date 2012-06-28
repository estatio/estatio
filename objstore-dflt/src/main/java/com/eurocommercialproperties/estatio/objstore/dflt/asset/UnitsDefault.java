package com.eurocommercialproperties.estatio.objstore.dflt.asset;

import java.util.List;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.filter.Filter;

import com.eurocommercialproperties.estatio.dom.asset.Unit;
import com.eurocommercialproperties.estatio.dom.asset.UnitType;
import com.eurocommercialproperties.estatio.dom.asset.Units;
import com.google.common.base.Objects;

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
    public Unit newUnit(final String code, String name, UnitType type) {
        final Unit unit = newTransientInstance(Unit.class);
        unit.setCode(code);
        unit.setName(name);
        unit.setType(type);
        persist(unit);
        return unit;
    }
    // }}

	/* (non-Javadoc)
	 * @see com.eurocommercialproperties.estatio.dom.asset.Units#newUnit(java.lang.String, java.lang.String, com.eurocommercialproperties.estatio.dom.asset.UnitType)
	 */

}
