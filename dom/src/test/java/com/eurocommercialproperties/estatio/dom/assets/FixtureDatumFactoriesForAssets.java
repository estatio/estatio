package com.eurocommercialproperties.estatio.dom.assets;

import com.danhaywood.testsupport.coverage.PojoTester.FixtureDatumFactory;
import com.eurocommercialproperties.estatio.dom.asset.Property;

public class FixtureDatumFactoriesForAssets {

	public static FixtureDatumFactory<Property> properties() {
		Property prop1 = new Property();
		prop1.setName("Property 1");
		Property prop2 = new Property();
		prop2.setName("Property 2");
		return new FixtureDatumFactory<Property>(Property.class, prop1, prop2);
	}


}
