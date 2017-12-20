package org.incode.module.country.dom.impl;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

@Mixin
public class Country_states {

    private final Country country;

    public Country_states(final Country country) {
        this.country = country;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<State> states() {
        return stateRepository.findStatesByCountry(country);
    }


    @Inject
    StateRepository stateRepository;

}
