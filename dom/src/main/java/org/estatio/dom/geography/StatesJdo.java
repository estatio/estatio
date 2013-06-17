package org.estatio.dom.geography;

import java.util.List;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.query.QueryDefault;

public class StatesJdo extends States {

    @Override
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "3")
    public List<State> findByCountry(Country country) {
        if (country == null)
            return null;
        return findStatesForCountry(country);
    }

    @Hidden
    public List<State> findStatesForCountry(final Country country) {
        if (country == null)
            return null;
        return allMatches(queryForFindStatesForCountry(country));
    }

    private static QueryDefault<State> queryForFindStatesForCountry(Country country) {
        return new QueryDefault<State>(State.class, "state_findStatesByCountry", "country", country);
    }
}
