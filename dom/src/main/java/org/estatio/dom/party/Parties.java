package org.estatio.dom.party;

import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.filter.Filter;


@Named("Parties")
public class Parties extends AbstractFactoryAndRepository {

    // {{ Id, iconName
    @Override
    public String getId() {
        return "parties";
    }

    public String iconName() {
        return "Party";
    }
    // }}

    // {{ newPerson
    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public Person newPerson(
            final @Named("initials") @Optional String initials, 
            final @Named("firstName") @Optional String firstName, 
            final @Named("lastName") String lastName) {
        final Person person = newTransientInstance(Person.class);
        person.setInitials(initials);
        person.setLastName(lastName);
        person.setFirstName(firstName);
        persist(person);
        return person;
    }
    // }}

    // {{ newOrganisation
    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(sequence = "2")
    public Organisation newOrganisation(
            final @Named("name") String name) {
        final Organisation org = newTransientInstance(Organisation.class);
        org.setName(name);
        persist(org);
        return org;
    }
    // }}

    // {{ findOrganisationByReference
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "4")
    public Organisation findOrganisationByReference(
            @Named("Reference") final String reference) {
        return firstMatch(Organisation.class, new Filter<Organisation>() {
            @Override
            public boolean accept(final Organisation organisation) {
                return organisation.getReference().contains(reference);
            }
        });
    }
    // }}

    // {{ findOrganisationByName
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "5")
    public Organisation findOrganisationByName(
            @Named("Name") final String name){
        return firstMatch(Organisation.class, new Filter<Organisation>() {
            @Override
            public boolean accept(final Organisation organisation) {
                return organisation.getName().contains(name);
            }
        });
    }
    // }}

    // {{ findPerson
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "3")
    public Person findPerson(
            final @Named("First Name") @Optional String firstName, 
            final @Named("Last Name") @Optional String lastName) {
        return firstMatch(Person.class, new Filter<Person>() {
            @Override
            public boolean accept(final Person person) {
                return person.getLastName().contains(lastName) || person.getFirstName().contains(firstName);
            }
        });
    }
    // }}

    // {{ findPartiesByReference
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "6")
    public List<Party> findPartiesByReference(@Named("Reference") final String reference) {
        throw new NotImplementedException();
    }
    // }}
    
    // {{ findPartyByReference
    @Hidden
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "6")
    public Party findPartyByReference(
            @Named("Reference") final String reference) {
        return firstMatch(Party.class, new Filter<Party>() {
            @Override
            public boolean accept(final Party party) {
                return reference.contains(party.getReference());
            }
        });
    }
    // }}

    // {{ allParties
    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "7")
    public List<Party> allParties() {
        return allInstances(Party.class);
    }
    // }}
}
