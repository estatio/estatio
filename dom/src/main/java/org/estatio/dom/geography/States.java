package org.estatio.dom.geography;

import java.util.List;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.query.QueryDefault;

import org.estatio.dom.EstatioDomainService;

@Named("States")
public class States extends EstatioDomainService {

    public States() {
        super(States.class, State.class);
    }
    

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "1")
    public State newState(final @Named("Reference") String reference, final @Named("Name") String name, final Country country) {
        final State state = newTransientInstance(State.class);
        state.setReference(reference);
        state.setName(name);
        state.setCountry(country);
        persist(state);
        return state;
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public State findByReference(final @Named("Reference") String reference) {
        return (State) firstMatch(new QueryDefault<Geography>(Geography.class, "findByReference", "reference", reference));
    }

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "3")
    public List<State> findByCountry(final Country country) {
        return allMatches(new QueryDefault<State>(State.class, "findByCountry", "country", country));
    }

    @ActionSemantics(Of.SAFE)
    @Prototype
    public List<State> allStates() {
        return allInstances(State.class);
    }

}
