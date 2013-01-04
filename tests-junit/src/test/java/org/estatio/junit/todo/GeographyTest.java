package org.estatio.junit.todo;

import org.junit.Before;
import org.junit.Test;


import org.apache.isis.progmodel.wrapper.applib.DisabledException;
import org.apache.isis.viewer.junit.Fixture;
import org.apache.isis.viewer.junit.Fixtures;
import org.estatio.dom.geography.Country;
import org.estatio.fixture.EstatioFixture;
import org.estatio.junit.AbstractTest;

@Fixtures({ @Fixture(EstatioFixture.class) })
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
