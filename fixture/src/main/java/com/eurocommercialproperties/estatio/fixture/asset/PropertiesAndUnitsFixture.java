package com.eurocommercialproperties.estatio.fixture.asset;


import org.apache.isis.applib.fixtures.AbstractFixture;

import com.eurocommercialproperties.estatio.dom.asset.Properties;
import com.eurocommercialproperties.estatio.dom.asset.Property;
import com.eurocommercialproperties.estatio.dom.asset.PropertyType;

public class PropertiesAndUnitsFixture extends AbstractFixture {

    @Override
    public void install() {
    	createPropertyAndUnits("KAL", "Kalvertoren", PropertyType.COMMERCIAL, 4);
    	createPropertyAndUnits("OXF", "Oxford", PropertyType.COMMERCIAL, 3);
    }

    private Property createPropertyAndUnits(final String reference, String name, PropertyType type, int numberOfUnits) {
        Property property = properties.newProperty(reference, name, type);
        for (int i = 0; i < numberOfUnits; i++) {
        	int unitNumber = i+1;
			property.newUnit(String.format("%s-%03d",reference, unitNumber), "Unit " + unitNumber);
		}
		return property;
    }

    private Properties properties;

    public void setPropertyRepository(final Properties properties) {
        this.properties = properties;
    }
}


