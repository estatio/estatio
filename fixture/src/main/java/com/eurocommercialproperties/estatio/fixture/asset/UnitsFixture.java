package com.eurocommercialproperties.estatio.fixture.asset;


import org.apache.isis.applib.fixtures.AbstractFixture;

import com.eurocommercialproperties.estatio.dom.asset.Unit;
import com.eurocommercialproperties.estatio.dom.asset.Units;
import com.eurocommercialproperties.estatio.dom.asset.UnitType;

public class UnitsFixture extends AbstractFixture {


	@Override
    public void install() {
    	createUnit("KAL001", "Kalvertoren 001", UnitType.BOUTIQUE);
    }

    private Unit createUnit(final String code, String name, UnitType type) {
        return units.newUnit(code, name, type);
    }

    private Units units;

    public void setUnitRepository(final Units units) {
        this.units = units;
    }

}
