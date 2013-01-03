package com.eurocommercialproperties.estatio.dom.geography;

import java.util.List;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.filter.Filter;


@Named("States")
public class States extends AbstractFactoryAndRepository {

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
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "1")
    public State newState(
            final @Named("Reference") String reference, 
            final @Named("Name") String name, 
            final Country country) {
        final State state = newTransientInstance(State.class);
        state.setReference(reference);
        state.setName(name);
        state.setCountry(country);
        persist(state);
        return state;
    }

    // }}

    // {{ findByReference
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public State findByReference(
            final @Named("Reference") String reference) {
        if (reference == null) return null;
        return firstMatch(State.class, new Filter<State>() {
            @Override
            public boolean accept(final State state) {
                return reference.equals(state.getReference());
            }
        });
    }

    // }}

    // {{ findByCountry
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "3")
    public List<State> findByCountry(
            final Country country){
        if (country == null) return null;
        return allMatches(State.class, new Filter<State>() {
            @Override
            public boolean accept(final State state) {
                return state.getCountry().equals(country);
            }
        });
    }

    // }}

    // {{ allStates
    @ActionSemantics(Of.SAFE)
    @Prototype
    public List<State> allStates() {
        return allInstances(State.class);
    }
    // }}

}
