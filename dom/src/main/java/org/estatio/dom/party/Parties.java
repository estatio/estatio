package org.estatio.dom.party;

import java.util.List;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.filter.Filter;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.utils.StringUtils;

public class Parties extends EstatioDomainService<Party> {

    public Parties() {
        super(Parties.class, Party.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public Person newPerson(final @Named("initials") @Optional String initials, final @Named("firstName") @Optional String firstName, final @Named("lastName") String lastName) {
        final Person person = newTransientInstance(Person.class);
        person.setInitials(initials);
        person.setLastName(lastName);
        person.setFirstName(firstName);
        person.updating();
        persist(person);
        return person;
    }

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(sequence = "2")
    public Organisation newOrganisation(final @Named("Reference") String reference, final @Named("Name") String name) {
        final Organisation organisation = newTransientInstance(Organisation.class);
        organisation.setReference(reference);
        organisation.setName(name);
        persist(organisation);
        return organisation;
    }

    // //////////////////////////////////////

    // TODO: naive implementation
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "4")
    public Organisation findOrganisationByReference(@Named("Reference") final String reference) {
        return firstMatch(Organisation.class, new Filter<Organisation>() {
            @Override
            public boolean accept(final Organisation organisation) {
                return organisation.getReference().contains(reference);
            }
        });
    }
    
    // //////////////////////////////////////

    // TODO: naive implementation
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "5")
    public Organisation findOrganisationByName(@Named("Name") final String name) {
        return firstMatch(Organisation.class, new Filter<Organisation>() {
            @Override
            public boolean accept(final Organisation organisation) {
                return organisation.getName().contains(name);
            }
        });
    }
    
    // //////////////////////////////////////

    @Hidden
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "3")
    public List<Party> findPartiesByReference(@Named("searchPattern") final String searchPattern) {
        return allMatches("parties_findPartyByReference", "searchPattern", StringUtils.wildcardToRegex(searchPattern));
    }

    @Hidden
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "4")
    public Party findPartyByReference(@Named("searchPattern") final String searchPattern) {
        return firstMatch("parties_findPartyByReference", "searchPattern", StringUtils.wildcardToRegex(searchPattern));
    }


    // //////////////////////////////////////
    
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "5")
    public List<Party> findParties(@Named("searchPattern") final String searchPattern) {
        return allMatches("parties_findParties", "searchPattern", StringUtils.wildcardToCaseInsensitiveRegex(searchPattern));
    }

    // //////////////////////////////////////

    @Hidden
    public List<Party> autoComplete(String searchPhrase) {
        return searchPhrase.length()>2 
                ? findParties("*"+searchPhrase+"*") 
                : null;
    }

    // //////////////////////////////////////

    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "99")
    public List<Party> allParties() {
        return allInstances();
    }

}
