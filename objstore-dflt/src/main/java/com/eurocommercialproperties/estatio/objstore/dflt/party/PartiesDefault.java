package com.eurocommercialproperties.estatio.objstore.dflt.party;

import java.util.List;

import com.eurocommercialproperties.estatio.dom.party.Parties;
import com.eurocommercialproperties.estatio.dom.party.Party;
import com.eurocommercialproperties.estatio.dom.party.Person;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.QueryOnly;

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
    @QueryOnly
    @MemberOrder(sequence = "1")
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
    @QueryOnly
    @MemberOrder(sequence = "2")
    public Person newOrganisation(String name) {

        // TODO Auto-generated method stub
        return null;
    }

    // }}

    // {{ AllInstances
    @Override
    public List<Party> allInstances() {
        return allInstances(Party.class);
    }
    // }}

}
