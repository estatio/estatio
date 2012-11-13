package com.eurocommercialproperties.estatio.dom;

import org.joda.time.LocalDate;

import org.apache.isis.applib.value.Date;

import com.danhaywood.testsupport.coverage.PojoTester.FixtureDatumFactory;

public class FixtureDatumFactoriesForJoda {

	public static FixtureDatumFactory<LocalDate> dates() {
		return new FixtureDatumFactory<LocalDate>(LocalDate.class, new LocalDate(2012, 7, 19), new LocalDate(2012, 7, 20), new LocalDate(2012, 8, 19), new LocalDate(2013, 7, 19));
	}


}
