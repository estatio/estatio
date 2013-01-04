package org.estatio.junit.todo;

import org.junit.Before;
import org.junit.Test;


import org.apache.isis.progmodel.wrapper.applib.DisabledException;
import org.apache.isis.viewer.junit.Fixture;
import org.apache.isis.viewer.junit.Fixtures;
import org.estatio.dom.asset.Property;
import org.estatio.fixture.EstatioFixture;
import org.estatio.junit.AbstractTest;

@Fixtures({ @Fixture(EstatioFixture.class) })
public class PropertyTest extends AbstractTest {

    private Property wrappedProperty;

    @Before
    public void setUp() throws Exception {
        wrappedProperty = wrapped(properties.newProperty("CODE-1", "Some name"));
    }

    @Test(expected = DisabledException.class)
    public void cannotChangeCode() throws Exception {
        wrappedProperty.setReference("OTHER-1");
    }

}
