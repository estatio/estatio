package org.estatio.dom.asset;

import org.estatio.dom.asset.Property;

import com.danhaywood.testsupport.coverage.PojoTester.FixtureDatumFactory;

public class FixtureDatumFactoriesForIndex {

	public static FixtureDatumFactory<Property> properties() {
		Property prop1 = new Property();
		prop1.setName("Property 1");
		Property prop2 = new Property();
		prop2.setName("Property 2");
		return new FixtureDatumFactory<Property>(Property.class, prop1, prop2);
	}


}
