package com.eurocommercialproperties.estatio.fixture;


import org.apache.isis.applib.fixtures.AbstractFixture;

import com.eurocommercialproperties.estatio.dom.asset.Property;
import com.eurocommercialproperties.estatio.dom.asset.Properties;
import com.eurocommercialproperties.estatio.dom.asset.PropertyType;
import com.eurocommercialproperties.estatio.dom.asset.Unit;
import com.eurocommercialproperties.estatio.dom.asset.UnitType;
import com.eurocommercialproperties.estatio.dom.asset.Units;
import com.eurocommercialproperties.estatio.fixture.asset.PropertiesAndUnitsFixture;
import com.eurocommercialproperties.estatio.fixture.party.PartiesFixture;

public class EstatioRefDataFixture extends AbstractFixture {
	
	public EstatioRefDataFixture() {
		addFixture(new PartiesFixture());
		addFixture(new PropertiesAndUnitsFixture());
	}

}


