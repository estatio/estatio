package com.eurocommercialproperties.estatio.objstore.dflt.party;

import java.util.List;

import com.eurocommercialproperties.estatio.dom.party.Organisation;
import com.eurocommercialproperties.estatio.dom.party.Parties;
import com.eurocommercialproperties.estatio.dom.party.Party;
import com.eurocommercialproperties.estatio.dom.party.Person;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.filter.Filter;

public class PartiesDefault extends AbstractFactoryAndRepository implements Parties {

    // {{ Id, iconName
    @Override
    public String getId() {
        return "parties";
    }

    public String iconName() {
        return "Party";
    }

    // }}

    // {{ NewOwner (action)
    @Override
    public Person newPerson(String initials, String firstName, String lastName) {
        final Person person = newTransientInstance(Person.class);
        person.setInitials(initials);
        person.setLastName(lastName);
        person.setFirstName(firstName);
        persist(person);
        return person;
    }

    // }}

    // {{ NewOwner (action)
    @Override
    public Organisation newOrganisation(String name) {
        final Organisation org = newTransientInstance(Organisation.class);
        org.setName(name);
        persist(org);
        return org;
    }

    // }}

    // {{ AllInstances
    @Override
    public List<Party> allInstances() {
        return allInstances(Party.class);
    }

    // }}

    @Override
    public Organisation findOrganisationByReference(final String reference) {
        return firstMatch(Organisation.class, new Filter<Organisation>() {
            @Override
            public boolean accept(final Organisation organisation) {
                return reference.contains(organisation.getReference());
            }
        });
    }

    @Override
    public Organisation findOrganisationByName(final String reference) {
        return firstMatch(Organisation.class, new Filter<Organisation>() {
            @Override
            public boolean accept(final Organisation organisation) {
                return organisation.getReference().contains(reference);
            }
        });
    }

    @Override
    public Person findPerson(final String firstName, final String lastName) {
        return firstMatch(Person.class, new Filter<Person>() {
            @Override
            public boolean accept(final Person person) {
                return person.getLastName().contains(lastName) || person.getFirstName().contains(firstName);
            }
        });
    }

    @Override
    public Party findPartyByReference(final String reference) {
        return firstMatch(Party.class, new Filter<Party>() {
            @Override
            public boolean accept(final Party party) {
                return reference.contains(party.getReference());
            }
        });
    }

}
