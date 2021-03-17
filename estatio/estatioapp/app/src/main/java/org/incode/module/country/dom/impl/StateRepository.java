package org.incode.module.country.dom.impl;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.repository.RepositoryService;

@DomainService(nature = NatureOfService.DOMAIN, repositoryFor = State.class)
public class StateRepository {

    @Programmatic
    public State newState(
            final String reference,
            final String name,
            final Country country) {
        final State state = new State(reference, name, country);
        repositoryService.persist(state);
        return state;
    }

    @Programmatic
    public List<State> allStates() {
        return repositoryService.allInstances(State.class);
    }

    @Programmatic
    public State findState(final String reference) {
        return repositoryService.firstMatch(
                new QueryDefault<>(
                        State.class,
                        "findByReference",
                        "reference", reference));
    }

    @Programmatic
    public List<State> findStatesByCountry(final Country country) {
        return country != null
                ? repositoryService.allMatches(
                    new QueryDefault<>(
                            State.class,
                            "findByCountry",
                            "country", country))
                : Collections.<State> emptyList();
    }


    @Inject
    RepositoryService repositoryService;
}
