package org.estatio.fixture.party;

import org.apache.isis.applib.fixtures.AbstractFixture;
import org.estatio.dom.communicationchannel.CommunicationChannel;
import org.estatio.dom.communicationchannel.CommunicationChannels;
import org.estatio.dom.financial.FinancialAccount;
import org.estatio.dom.financial.FinancialAccounts;
import org.estatio.dom.financial.contributed.FinancialAccountContributedActions;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;

public class PartiesFixture extends AbstractFixture {

    @Override
    public void install() {
        createOrganisation(
                "ACME", 
                "ACME Properties International",
                financialAccounts.newBankAccount("NL31ABNA0580744434"),
                communicationChannels.newPostalAddress("Herengracht 100", null, "1010 AA", "Amsterdam", null, null),
                communicationChannels.newPhoneNumber("+31202211333"),
                communicationChannels.newFaxNumber("+312022211399"),
                communicationChannels.newEmailAddress("info@topmodel.example.com")
                );
        createOrganisation(
                "HELLOWORLD", 
                "Hello World Properties", 
                financialAccounts.newBankAccount("NL31ABNA0580744434"),
                communicationChannels.newPostalAddress("Herengracht 100", null, "1010 AA", "Amsterdam", null, null),
                communicationChannels.newPhoneNumber("+31202211333"),
                communicationChannels.newFaxNumber("+312022211399"),
                communicationChannels.newEmailAddress("info@example.com")
                );
        createOrganisation(
                "TOPMODEL", 
                "Topmodel Fashion",
                financialAccounts.newBankAccount("NL31ABNA0580744434"),
                communicationChannels.newPostalAddress("Herengracht 100", null, "1010 AA", "Amsterdam", null, null),
                communicationChannels.newPhoneNumber("+31202211333"),
                communicationChannels.newFaxNumber("+312022211399"),
                communicationChannels.newEmailAddress("info@topmodel.example.com")
                );
        createOrganisation(
                "MEDIAX", 
                "Mediax Electronics",
                financialAccounts.newBankAccount("NL31ABNA0580744434"),
                communicationChannels.newPostalAddress("Herengracht 100", null, "1010 AA", "Amsterdam", null, null),
                communicationChannels.newPhoneNumber("+31202211333"),
                communicationChannels.newFaxNumber("+312022211399"),
                communicationChannels.newEmailAddress("info@topmodel.example.com")
                );
        createOrganisation(
                "POISON", 
                "Poison Perfumeries",
                financialAccounts.newBankAccount("NL31ABNA0580744434"),
                communicationChannels.newPostalAddress("Herengracht 100", null, "1010 AA", "Amsterdam", null, null),
                communicationChannels.newPhoneNumber("+31202211333"),
                communicationChannels.newFaxNumber("+312022211399"),
                communicationChannels.newEmailAddress("info@topmodel.example.com")
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
        Party p = parties.newPerson(initials, firstName, lastName);
        p.setReference(reference);
        return p;
    }

    private Party createOrganisation(String reference, String name, FinancialAccount account, CommunicationChannel ... communicationChannels ) {
        Party p = parties.newOrganisation(reference, name);
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
    
    private CommunicationChannels communicationChannels;
    
    public void injectCommunicationChannels(CommunicationChannels communicationChannels) {
        this.communicationChannels = communicationChannels;
    }
    
    private FinancialAccounts financialAccounts;
    
    public void injectFinancialAccounts(FinancialAccounts financialAccounts) {
        this.financialAccounts = financialAccounts;
    }
    
}
