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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;

import org.apache.isis.applib.fixtures.AbstractFixture;
import org.apache.isis.core.commons.ensure.Ensure;

import org.estatio.dom.communicationchannel.CommunicationChannelContributions;
import org.estatio.dom.communicationchannel.CommunicationChannelType;
import org.estatio.dom.geography.Countries;
import org.estatio.dom.geography.Country;
import org.estatio.dom.geography.State;
import org.estatio.dom.geography.States;
import org.estatio.dom.party.Organisations;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.Persons;

public class PersonsAndOrganisationsAndCommunicationChannelsFixture extends AbstractFixture {

    @Override
    public void install() {
        createOrganisation("ACME;ACME Properties International;Herengracht 100;null;1010 AA;Amsterdam;null;NLD;+31202211333;+312022211399;info@acme.example.com");
        createOrganisation("HELLOWORLD;Hello World Properties;5 Covent Garden;;W1A1AA;London;;GBR;+44202211333;+442022211399;info@hello.example.com");
        createOrganisation("TOPMODEL;Topmodel Fashion;2 Top Road;;W2AXXX;London;;GBR;+31202211333;+312022211399;info@topmodel.example.com");
        createOrganisation("MEDIAX;Mediax Electronics;Herengracht 100;;1010 AA;Amsterdam;;GBR;+31202211333;+312022211399;info@mediax.example.com");
        createOrganisation("POISON;Poison Perfumeries;Herengracht 100;;1010 AA;Amsterdam;;GBR;+31202211333;+312022211399;info@poison.example.com");
        createOrganisation("PRET;Pret-a-Manger;;;;;;;;;");
        createOrganisation("MIRACLE;Miracle Shoes;;;;;;;;;");
        createPerson("JDOE", "J", "John", "Doe");
        createPerson("LTORVALDS", "L", "Linus", "Torvalds");
    }

    private Party createPerson(String reference, String initials, String firstName, String lastName) {
        Party p = persons.newPerson(reference, initials, firstName, lastName);
        return p;
    }

    private Party createOrganisation(String input) {
        String[] values = input.split(";");
        Party party = organisations.newOrganisation(values[0], values[1]);
        Ensure.ensureThatArg(party, is(not(nullValue())), "could not find party '" + values[0] + "', '" + values[1] + "'");
        getContainer().flush();
        if(defined(values, 2)) {
            final Country country = countries.findCountry(values[7]);
            final State state = states.findState(values[6]);
            if(country != null && state != null) {
                communicationChannelContributedActions.newPostal(
                        party, 
                        CommunicationChannelType.POSTAL_ADDRESS, 
                        country, 
                        state, 
                        values[2], 
                        values[3], 
                        values[4], 
                        values[5]);
            }
            getContainer().flush();
        }
        if(defined(values, 8)) {
            communicationChannelContributedActions.newPhoneOrFax(
                    party, 
                    CommunicationChannelType.PHONE_NUMBER, 
                    values[8]);
            getContainer().flush();
        }
        if(defined(values, 9)) {
            communicationChannelContributedActions.newPhoneOrFax(
                    party, 
                    CommunicationChannelType.FAX_NUMBER, 
                    values[9]);
            getContainer().flush();
        }
        if(defined(values, 10)) {
            communicationChannelContributedActions.newEmail(
                    party, 
                    CommunicationChannelType.EMAIL_ADDRESS, 
                    values[10]);
            getContainer().flush();
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

    private CommunicationChannelContributions communicationChannelContributedActions;

    public void injectCommunicationChannels(CommunicationChannelContributions communicationChannelContributedActions) {
        this.communicationChannelContributedActions = communicationChannelContributedActions;
    }

}
