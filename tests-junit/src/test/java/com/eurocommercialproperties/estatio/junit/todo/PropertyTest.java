package com.eurocommercialproperties.estatio.junit.todo;

import org.apache.isis.progmodel.wrapper.applib.DisabledException;
import org.apache.isis.progmodel.wrapper.applib.InvalidException;
import org.apache.isis.viewer.junit.Fixture;
import org.apache.isis.viewer.junit.Fixtures;
import org.junit.Before;
import org.junit.Test;

import com.eurocommercialproperties.estatio.dom.asset.Property;
import com.eurocommercialproperties.estatio.dom.party.Owner;
import com.eurocommercialproperties.estatio.fixture.EstatioRefDataFixture;
import com.eurocommercialproperties.estatio.junit.AbstractTest;

@Fixtures({ @Fixture(EstatioRefDataFixture.class) })
public class PropertyTest extends AbstractTest {

	private Property wrappedProperty;
	
	@Before
	public void setUp() throws Exception {
		wrappedProperty = wrapped(properties.newProperty("CODE-1", "Some name"));
	}

    @Test(expected=DisabledException.class)
    public void cannotChangeCode() throws Exception {
        wrappedProperty.setCode("OTHER-1");
    }
	
    @Test
    public void canAddOwner() throws Exception {
    	// given
        Owner someOwner = owners.allInstances().get(0);
        
        // when
        wrappedProperty.addOwner(someOwner);
    }

    @Test(expected=InvalidException.class)
    public void cannotAddTheSameOwnerTwice() throws Exception {
    	// given
        Owner someOwner = owners.allInstances().get(0);
        wrappedProperty.addOwner(someOwner);
        
        // when
        wrappedProperty.addOwner(someOwner);
    }

}
