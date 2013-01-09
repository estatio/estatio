package org.estatio.junit.todo;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.apache.isis.viewer.junit.Fixture;
import org.apache.isis.viewer.junit.Fixtures;
import org.estatio.dom.asset.Property;
import org.estatio.fixture.EstatioFixture;
import org.estatio.junit.AbstractTest;
import org.junit.Ignore;
import org.junit.Test;


@Fixtures({ @Fixture(EstatioFixture.class) })
public class PropertiesDefaultTest extends AbstractTest {

    @Ignore
    @Test
    public void canCreateProperty() throws Exception {
        final Property property = properties.newProperty("CODE-1", "Some name");
        
        assertThat(property, is(not(nullValue())));
        assertThat(property.getReference(), is("CODE-1"));
        assertThat(property.getName(), is("Some name"));
        assertThat(getDomainObjectContainer().isPersistent(property), is(true));
    }

}
