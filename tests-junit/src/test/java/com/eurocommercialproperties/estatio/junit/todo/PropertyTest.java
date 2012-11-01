package com.eurocommercialproperties.estatio.junit.todo;

import org.junit.Before;
import org.junit.Test;

import com.eurocommercialproperties.estatio.dom.asset.Property;
import com.eurocommercialproperties.estatio.fixture.EstatioFixture;
import com.eurocommercialproperties.estatio.junit.AbstractTest;

import org.apache.isis.progmodel.wrapper.applib.DisabledException;
import org.apache.isis.viewer.junit.Fixture;
import org.apache.isis.viewer.junit.Fixtures;

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
