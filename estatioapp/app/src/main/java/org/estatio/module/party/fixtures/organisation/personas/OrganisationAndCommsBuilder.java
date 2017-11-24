/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
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
package org.estatio.module.party.fixtures.organisation.personas;

import javax.inject.Inject;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancies;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelRepository;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelType;
import org.incode.module.communications.dom.impl.commchannel.PostalAddress;
import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.dom.impl.CountryRepository;
import org.incode.module.country.dom.impl.State;
import org.incode.module.country.dom.impl.StateRepository;

import org.estatio.module.base.platform.fixturesupport.BuilderScriptAbstract;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.OrganisationRepository;
import org.estatio.module.party.dom.PersonRepository;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public class OrganisationAndCommsBuilder extends BuilderScriptAbstract<OrganisationAndCommsBuilder> {

    @Getter @Setter
    private String atPath;

    @Getter @Setter
    private String partyReference;

    @Getter @Setter
    private String partyName;

    @Getter @Setter
    private Boolean useNumeratorForReference;

    @Getter @Setter
    private String address1;

    @Getter @Setter
    private String address2;

    @Getter @Setter
    private String postalCode;

    @Getter @Setter
    private String city;

    @Getter @Setter
    private String stateReference;

    @Getter @Setter
    private String countryReference;

    @Getter @Setter
    private String phone;

    @Getter @Setter
    private String fax;

    @Getter @Setter
    private String emailAddress;

    @Getter
    private Organisation party;

    @Override
    protected void execute(ExecutionContext executionContext) {

        checkParam("atPath", executionContext, String.class);
        checkParam("partyReference", executionContext, String.class);
        checkParam("partyName", executionContext, String.class);

        defaultParam("useNumeratorForReference", executionContext, false);

        /*

    @Getter @Setter
    private String address1;

    @Getter @Setter
    private String address2;

    @Getter @Setter
    private String postalCode;

    @Getter @Setter
    private String city;

    @Getter @Setter
    private String stateReference;

    @Getter @Setter
    private String countryReference;

    @Getter @Setter
    private String phone;

    @Getter @Setter
    private String fax;

    @Getter @Setter
    private String emailAddress;

    @Getter
    private Organisation party;

    }

    protected Party createOrganisation(
            String atPath,
            String partyReference,
            String partyName,
            String address1,
            String address2,
            String postalCode,
            String city,
            String stateReference,
            String countryReference,
            String phone,
            String fax,
            String emailAddress,
            ExecutionContext executionContext) {
         */

        ApplicationTenancy applicationTenancy = applicationTenancies.findTenancyByPath(atPath);

        this.party = organisationRepository.newOrganisation(partyReference, false, partyName, applicationTenancy);

        if (address1 != null) {
            final Country country = countryRepository.findCountry(countryReference);
            final State state = stateRepository.findState(stateReference);
            final PostalAddress postalAddress = communicationChannelRepository.newPostal(
                    party,
                    CommunicationChannelType.POSTAL_ADDRESS,
                    address1,
                    address2,
                    null,
                    postalCode,
                    city,
                    state,
                    country);
            // We make this the legal address too...
            postalAddress.setLegal(true);
            getContainer().flush();
        }
        if (phone != null) {
            communicationChannelRepository.newPhoneOrFax(
                    party,
                    CommunicationChannelType.PHONE_NUMBER,
                    phone);
            getContainer().flush();
        }
        if (fax != null) {
            communicationChannelRepository.newPhoneOrFax(
                    party,
                    CommunicationChannelType.FAX_NUMBER,
                    fax);
            getContainer().flush();
        }
        if (emailAddress != null) {
            communicationChannelRepository.newEmail(
                    party,
                    CommunicationChannelType.EMAIL_ADDRESS,
                    emailAddress);
            getContainer().flush();
        }

        executionContext.addResult(this, party.getReference(), party);
    }

    protected boolean defined(String[] values, int i) {
        return values.length > i && !values[i].isEmpty();
    }

    // //////////////////////////////////////

    @Inject
    protected CountryRepository countryRepository;

    @Inject
    protected StateRepository stateRepository;

    @Inject
    protected OrganisationRepository organisationRepository;

    @Inject
    protected PersonRepository personRepository;

    @Inject
    protected CommunicationChannelRepository communicationChannelRepository;

    @Inject
    protected ApplicationTenancies applicationTenancies;

}
