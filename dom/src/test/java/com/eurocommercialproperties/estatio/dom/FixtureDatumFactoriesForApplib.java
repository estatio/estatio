package com.eurocommercialproperties.estatio.dom;

import org.apache.isis.applib.value.Date;

import com.danhaywood.testsupport.coverage.PojoTester.FixtureDatumFactory;

public class FixtureDatumFactoriesForApplib {

	public static FixtureDatumFactory<Date> dates() {
		return new FixtureDatumFactory<Date>(Date.class, new Date(2012, 7, 19), new Date(2012, 7, 20), new Date(2012, 8, 19), new Date(2013, 7, 19));
	}


}
