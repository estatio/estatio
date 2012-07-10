package com.eurocommercialproperties.estatio.objstore.dflt.geography;

import java.util.List;

import com.eurocommercialproperties.estatio.dom.geography.Country;
import com.eurocommercialproperties.estatio.dom.geography.State;
import com.eurocommercialproperties.estatio.dom.geography.States;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.filter.Filter;

public class StatesDefault extends AbstractFactoryAndRepository implements States {

    // {{ Id, iconName
    @Override
    public String getId() {
        return "states";
    }

    public String iconName() {
        return "State";
    }

    // }}

    // {{ NewState (hidden)
    @Override
    public State newState(final String reference, String name, Country country) {
        final State state = newTransientInstance(State.class);
        state.setReference(reference);
        state.setName(name);
        state.setCountry(country);
        persist(state);
        return state;
    }

    // }}

    // {{ findByReference
    @Override
    public State findByReference(final String reference) {
        return firstMatch(State.class, new Filter<State>() {
            @Override
            public boolean accept(final State state) {
                return reference.equals(state.getReference());
            }
        });
    }

    // }}

    // {{ findByCountry
    @Override
    public List<State> findByCountry(final Country country) {
        return allMatches(State.class, new Filter<State>() {
            @Override
            public boolean accept(final State state) {
                return country.equals(state.getCountry());
            }
        });
    }

    // }}

    // {{ AllInstances
    @Override
    public List<State> allInstances() {
        return allInstances(State.class);
    }
    // }}

}
