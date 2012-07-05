package com.eurocommercialproperties.estatio.objstore.dflt.geography;

import java.util.List;

import org.apache.isis.applib.AbstractFactoryAndRepository;

import com.eurocommercialproperties.estatio.dom.geography.Country;
import com.eurocommercialproperties.estatio.dom.geography.State;
import com.eurocommercialproperties.estatio.dom.geography.States;

public class RegionsDefault extends AbstractFactoryAndRepository implements States {

    // {{ Id, iconName
    @Override
    public String getId() {
        return "regions";
    }

    public String iconName() {
        return "Region";
    }

    // }}

    // {{ NewRegion  (hidden)
    @Override
    public State newState(final String reference, String name, Country country) {
        final State region = newTransientInstance(State.class);
        region.setReference(reference);
        region.setName(name);
        region.setCountry(country);
        persist(region);
        return region;
    }
    // }}

    // {{ AllInstances
    @Override
    public List<State> allInstances() {
    	return allInstances(State.class);
    }
    // }}

}
