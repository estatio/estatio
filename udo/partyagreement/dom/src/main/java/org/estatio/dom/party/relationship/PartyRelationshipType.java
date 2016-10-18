package org.estatio.dom.party.relationship;

import java.util.Set;

import com.google.common.collect.Sets;

import org.estatio.dom.party.Organisation;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.Person;

public enum PartyRelationshipType {

    // Org - Org
    OWNERSHIP("Owns","Owned by", Organisation.class, Organisation.class),
    // Org - Person
    EMPLOYMENT("Employer", "Employee", Organisation.class, Person.class),
    MARKETING("Marketing", "Marketing", Organisation.class, Person.class),
    LEASING("Leasing", "Leasing", Organisation.class, Person.class),
    ACCOUNTING("Accounting", "Accounting", Organisation.class, Person.class),
    STORE_MANAGER("Store Manager", "Store Manager", Organisation.class, Person.class),
    TURNOVER_REFERENT("Turnover Referent", "Turnover Referent", Organisation.class, Person.class),
    // Person - Person
    MARRIAGE("Husband", "Wife", Person.class, Person.class),
    CONTACT("Contact", "Contact", Party.class, Party.class);

    private String fromTitle;
    private String toTitle;
    Class<? extends Party> fromClass;
    Class<? extends Party> toClass;

    PartyRelationshipType(
            final String fromTitle,
            final String toTitle,
            final Class<? extends Party> fromClass,
            final Class<? extends Party> toClass) {
        this.fromTitle = fromTitle;
        this.toTitle = toTitle;
        this.fromClass = fromClass;
        this.toClass = toClass;
    }

    public static PartyRelationship createWithToTitle(
            final Party fromParty,
            final Party toParty,
            final String toTitle) {
        for (PartyRelationshipType relationshipType : PartyRelationshipType.values()) {
            if (relationshipType.toTitle.equals(toTitle)) {
                return new PartyRelationship(fromParty, toParty, relationshipType);
            }
            if (relationshipType.fromTitle.equals(toTitle)) {
                return new PartyRelationship(toParty, fromParty, relationshipType);
            }
        }
        return null;
    }

    public static Set<String> toTitlesFor(
            final Class<?> fromClass,
            final Class<?> toClass) {
        Set<String> choices = Sets.newTreeSet();
        if (fromClass != null && toClass != null)
            for (PartyRelationshipType type : PartyRelationshipType.values()) {
                if (type.fromClass.isAssignableFrom(fromClass) && type.toClass.isAssignableFrom(toClass)) {
                    choices.add(type.toTitle);
                }
                if (type.toClass.isAssignableFrom(fromClass) && type.fromClass.isAssignableFrom(toClass)) {
                    choices.add(type.fromTitle);
                }
            }
        return choices;
    }

    public String toTitle() {
        return toTitle;
    }

    public String fromTitle() {
        return fromTitle;
    }

}
