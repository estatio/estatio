package org.estatio.dom.geography;

import java.util.List;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.utils.StringUtils;

public class States extends EstatioDomainService<State> {

    public States() {
        super(States.class, State.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(name="Other", sequence = "geography.states.1")
    public State newState(final @Named("Reference") String reference, final @Named("Name") String name, final Country country) {
        final State state = newTransientInstance();
        state.setReference(reference);
        state.setName(name);
        state.setCountry(country);
        persist(state);
        return state;
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(name="Other", sequence = "geography.states.2")
    public State findStateByReference(final @Named("Reference") String reference) {
        return firstMatch("findByReference", "reference", StringUtils.wildcardToRegex(reference));
    }

    @ActionSemantics(Of.SAFE)
    @MemberOrder(name="Other", sequence = "geography.states.3")
    public List<State> findStatesByCountry(final Country country) {
        return allMatches("findByCountry", "country", country);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @Prototype
    @MemberOrder(name="Other", sequence = "geography.states.99")
    public List<State> allStates() {
        return allInstances();
    }

}
