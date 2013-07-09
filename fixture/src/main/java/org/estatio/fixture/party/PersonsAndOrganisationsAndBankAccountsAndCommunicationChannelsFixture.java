/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.fixture.party;

import org.estatio.dom.communicationchannel.CommunicationChannels;
import org.estatio.dom.financial.FinancialAccounts;
import org.estatio.dom.geography.Countries;
import org.estatio.dom.geography.States;
import org.estatio.dom.party.Organisations;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.Persons;

import org.apache.isis.applib.fixtures.AbstractFixture;

public class PersonsAndOrganisationsAndBankAccountsAndCommunicationChannelsFixture extends AbstractFixture {

    @Override
    public void install() {
        createOrganisation("ACME;ACME Properties International;NL31ABNA0580744433;Herengracht 100;null;1010 AA;Amsterdam;null;NLD;+31202211333;+312022211399;info@acme.example.com");
        createOrganisation("HELLOWORLD;Hello World Properties;NL31ABNA0580744434;5 Covent Garden;;W1A1AA;London;;GBR;+44202211333;+442022211399;info@hello.example.com");
        createOrganisation("TOPMODEL;Topmodel Fashion;NL31ABNA0580744435;2 Top Road;;W2AXXX;London;;GBR;+31202211333;+312022211399;info@topmodel.example.com");
        createOrganisation("MEDIAX;Mediax Electronics;NL31ABNA0580744436;Herengracht 100;;1010 AA;Amsterdam;;GBR;+31202211333;+312022211399;info@mediax.example.com");
        createOrganisation("POISON;Poison Perfumeries;NL31ABNA0580744437;Herengracht 100;;1010 AA;Amsterdam;;GBR;+31202211333;+312022211399;info@poison.example.com");
        createOrganisation("PRET;Pret-a-Manger;NL31ABNA0580744438;;;;;;;;;");
        createPerson("JDOE", "J", "John", "Doe");
        createPerson("LTORVALDS", "L", "Linus", "Torvalds");
    }

    private Party createPerson(String reference, String initials, String firstName, String lastName) {
        Party p = persons.newPerson(initials, firstName, lastName);
        p.setReference(reference);
        return p;
    }

    private Party createOrganisation(String input) {
        String[] values = input.split(";");
        Party party = organisations.newOrganisation(values[0], values[1]);
        financialAccounts.newBankAccount(party, values[2]);
        if(defined(values, 3)) {
            communicationChannels.newPostalAddress(party, values[3], values[4], values[5], values[6], states.findStateByReference(values[7]), countries.findCountryByReference(values[8]));
        }
        if(defined(values, 9)) {
            communicationChannels.newPhoneNumber(party, values[9]);
        }
        if(defined(values, 10)) {
            communicationChannels.newFaxNumber(party, values[10]);
        }
        if(defined(values, 11)) {
            communicationChannels.newEmailAddress(party ,values[11]);
        }
        return party;
    }

    protected boolean defined(String[] values, int i) {
        return values.length>i && !values[i].isEmpty();
    }

    // //////////////////////////////////////

    private Countries countries;

    public void injectCountries(Countries countries) {
        this.countries = countries;
    }

    private States states;

    public void injectStates(States states) {
        this.states = states;
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
