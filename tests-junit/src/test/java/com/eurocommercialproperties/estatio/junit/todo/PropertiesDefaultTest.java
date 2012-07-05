package com.eurocommercialproperties.estatio.junit.todo;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.apache.isis.viewer.junit.Fixture;
import org.apache.isis.viewer.junit.Fixtures;
import org.junit.Test;

import com.eurocommercialproperties.estatio.dom.asset.Property;
import com.eurocommercialproperties.estatio.fixture.EstatioRefDataFixture;
import com.eurocommercialproperties.estatio.junit.AbstractTest;

@Fixtures({ @Fixture(EstatioRefDataFixture.class) })
public class PropertiesDefaultTest extends AbstractTest {

    @Test
    public void canCreateToDoItem() throws Exception {
        final Property property = properties.newProperty("CODE-1", "Some name");
        
        assertThat(property, is(not(nullValue())));
        assertThat(property.getReference(), is("CODE-1"));
        assertThat(property.getName(), is("Some name"));
        assertThat(getDomainObjectContainer().isPersistent(property), is(true));
    }

}
