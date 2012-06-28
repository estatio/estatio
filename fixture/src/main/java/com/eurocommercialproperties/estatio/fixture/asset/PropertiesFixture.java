package com.eurocommercialproperties.estatio.fixture.asset;


import org.apache.isis.applib.fixtures.AbstractFixture;

import com.eurocommercialproperties.estatio.dom.asset.Property;
import com.eurocommercialproperties.estatio.dom.asset.Properties;
import com.eurocommercialproperties.estatio.dom.asset.PropertyType;

public class PropertiesFixture extends AbstractFixture {

    @Override
    public void install() {
    	createProperty("KAL", "Kalvertoren", PropertyType.COMMERCIAL);
    }

    private Property createProperty(final String code, String name, PropertyType type) {
        return properties.newProperty(code, name, type);
    }

    private Properties properties;

    public void setPropertyRepository(final Properties properties) {
        this.properties = properties;
    }

}
