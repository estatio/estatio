package com.eurocommercialproperties.estatio.dom.assets;

import com.danhaywood.testsupport.coverage.PojoTester.FixtureDatumFactory;
import com.eurocommercialproperties.estatio.dom.asset.Property;
import com.eurocommercialproperties.estatio.dom.asset.PropertyActor;
import com.eurocommercialproperties.estatio.dom.asset.Unit;

public class FixtureDatumFactoriesForAssets {

	public static FixtureDatumFactory<Property> properties() {
		Property prop1 = new Property();
		prop1.setName("Property 1");
		Property prop2 = new Property();
		prop2.setName("Property 2");
		return new FixtureDatumFactory<Property>(Property.class, prop1, prop2);
	}

    public static FixtureDatumFactory<Unit> units() {
        Unit unit1 = new Unit();
        unit1.setName("Unit 1");
        Unit unit2 = new Unit();
        unit2.setName("Unit 2");
        return new FixtureDatumFactory<Unit>(Unit.class, unit1, unit2);
    }

	public static FixtureDatumFactory<PropertyActor> propertyActors() {
	    PropertyActor pa1 = new PropertyActor();
	    PropertyActor pa2 = new PropertyActor();
        return new FixtureDatumFactory<PropertyActor>(PropertyActor.class, pa1, pa2);
    }


}
