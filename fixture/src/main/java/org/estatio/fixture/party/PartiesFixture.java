package org.estatio.fixture.party;

import org.apache.isis.applib.fixtures.AbstractFixture;
import org.estatio.dom.communicationchannel.CommunicationChannel;
import org.estatio.dom.communicationchannel.CommunicationChannels;
import org.estatio.dom.financial.FinancialAccount;
import org.estatio.dom.financial.FinancialAccounts;
import org.estatio.dom.financial.contributed.FinancialAccountContributedActions;
import org.estatio.dom.party.Organisations;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.Persons;

public class PartiesFixture extends AbstractFixture {

    @Override
    public void install() {
        createOrganisation(
                "ACME", 
                "ACME Properties International",
                financialAccounts.newBankAccount("NL31ABNA0580722234"),
                communicationChannels.newPostalAddress("Herengracht 100", null, "1010 AA", "Amsterdam", null, null),
                communicationChannels.newPhoneNumber("+31202211011"),
                communicationChannels.newFaxNumber("+312022211311"),
                communicationChannels.newEmailAddress("info@acme.com")
                );
        createOrganisation(
                "HELLOWORLD", 
                "Hello World Properties", 
                financialAccounts.newBankAccount("NL31ABNA0580733334"),
                communicationChannels.newPostalAddress("Herengracht 101", null, "1010 AA", "Amsterdam", null, null),
                communicationChannels.newPhoneNumber("+31202211022"),
                communicationChannels.newFaxNumber("+312022211322"),
                communicationChannels.newEmailAddress("info@helloworldproperties.com")
                );
        createOrganisation(
                "TOPMODEL", 
                "Topmodel Fashion",
                financialAccounts.newBankAccount("NL31ABNA0580744434"),
                communicationChannels.newPostalAddress("Herengracht 102", null, "1010 AA", "Amsterdam", null, null),
                communicationChannels.newPhoneNumber("+31202211033"),
                communicationChannels.newFaxNumber("+312022211333"),
                communicationChannels.newEmailAddress("info@topmodel.com")
                );
        createOrganisation(
                "MEDIAX", 
                "Mediax Electronics",
                financialAccounts.newBankAccount("NL31ABNA0580755534"),
                communicationChannels.newPostalAddress("Herengracht 103", null, "1010 AA", "Amsterdam", null, null),
                communicationChannels.newPhoneNumber("+31202211044"),
                communicationChannels.newFaxNumber("+312022211344"),
                communicationChannels.newEmailAddress("info@mediax.com")
                );
        createOrganisation(
                "POISON", 
                "Poison Perfumeries",
                financialAccounts.newBankAccount("NL31ABNA0580766634"),
                communicationChannels.newPostalAddress("Herengracht 104", null, "1010 AA", "Amsterdam", null, null),
                communicationChannels.newPhoneNumber("+31202211055"),
                communicationChannels.newFaxNumber("+312022211355"),
                communicationChannels.newEmailAddress("info@posion-perfumeries.com")
                );
        createPerson(
                "JDOE",
                "J",
                "John", 
                "Doe"
                );
        createPerson(
                "LTORVALDS", 
                "L", 
                "Linus", 
                "Torvalds"
                );
    }

    private Party createPerson(String reference, String initials, String firstName, String lastName) {
        Party p = persons.newPerson(initials, firstName, lastName);
        p.setReference(reference);
        return p;
    }

    private Party createOrganisation(String reference, String name, FinancialAccount account, CommunicationChannel ... communicationChannels ) {
        Party p = organisations.newOrganisation(reference, name);
        p.setReference(reference);
        account.setOwner(p);
        for (CommunicationChannel channel : communicationChannels) {
            channel.setReference(reference);
            p.addToCommunicationChannels(channel);
        }
        return p;
    }
    
//    private Party createOrganisation(String reference, String name) {
//        Party p = parties.newOrganisation(name);
//        p.setReference(reference);
//        return p;
//    }
    
    private Parties parties;

    public void injectParties(final Parties parties) {
        this.parties = parties;
    }
    
    private Organisations organisations;
    
    public void setOrganisations(final Organisations organisations) {
        this.organisations = organisations;
    }
    
    private Persons persons;
    
    public void setOrganisations(final Persons persons) {
        this.persons = persons;
    }
    

    private CommunicationChannels communicationChannels;
    
    public void injectCommunicationChannels(CommunicationChannels communicationChannels) {
        this.communicationChannels = communicationChannels;
    }
    
    private FinancialAccounts financialAccounts;
    
    public void injectFinancialAccounts(FinancialAccounts financialAccounts) {
        this.financialAccounts = financialAccounts;
    }
    
}
