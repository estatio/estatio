package com.eurocommercialproperties.estatio.dom.geography;

import java.util.List;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.QueryOnly;

@Named("States")
public interface States {

    @QueryOnly
    @MemberOrder(sequence = "1")
    public State newState(@Named("Reference") String reference, @Named("Name") String name, Country country);

    @QueryOnly
    @MemberOrder(sequence = "2")
    public State findByReference(String reference);

    @QueryOnly
    @MemberOrder(sequence = "3")
    public List<State> findByCountry(Country country);

    
    List<State> allInstances();



}
