package org.estatio.dom.party;

import java.util.List;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Prototype;

import org.estatio.dom.EstatioDomainService;

public class Persons extends EstatioDomainService<Person> {

    public Persons() {
        super(Persons.class, Person.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(name="Parties", sequence = "1")
    public Person newPerson(final @Named("initials") @Optional String initials, final @Named("firstName") @Optional String firstName, final @Named("lastName") String lastName) {
        final Person person = newTransientInstance(Person.class);
        person.setInitials(initials);
        person.setLastName(lastName);
        person.setFirstName(firstName);
        person.updating();
        persist(person);
        return person;
    }
    
    // //////////////////////////////////////

    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(name="Parties", sequence = "99.2")
    public List<Person> allPersons() {
        return allInstances();
    }


}
