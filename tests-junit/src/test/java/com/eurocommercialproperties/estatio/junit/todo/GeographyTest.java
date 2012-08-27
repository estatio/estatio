package com.eurocommercialproperties.estatio.junit.todo;

import org.junit.Before;
import org.junit.Test;

import com.eurocommercialproperties.estatio.dom.geography.Country;
import com.eurocommercialproperties.estatio.fixture.EstatioRefDataFixture;
import com.eurocommercialproperties.estatio.junit.AbstractTest;

import org.apache.isis.progmodel.wrapper.applib.DisabledException;
import org.apache.isis.viewer.junit.Fixture;
import org.apache.isis.viewer.junit.Fixtures;

@Fixtures({ @Fixture(EstatioRefDataFixture.class) })
public class GeographyTest extends AbstractTest {

    private Country wrappedCountry;

    @Before
    public void setUp() throws Exception {
        wrappedCountry = wrapped(countries.newCountry("NLD", "Netherlands"));
    }

    @Test(expected = DisabledException.class)
    public void cannotChangeCode() throws Exception {
        wrappedCountry.setReference("OTHER-1");
    }

}
