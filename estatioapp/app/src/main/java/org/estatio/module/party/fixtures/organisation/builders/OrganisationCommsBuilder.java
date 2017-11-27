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
package org.estatio.module.party.fixtures.organisation.builders;

import javax.inject.Inject;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancies;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelRepository;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelType;
import org.incode.module.communications.dom.impl.commchannel.EmailAddress;
import org.incode.module.communications.dom.impl.commchannel.PhoneOrFaxNumber;
import org.incode.module.communications.dom.impl.commchannel.PostalAddress;
import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.dom.impl.CountryRepository;
import org.incode.module.country.dom.impl.State;
import org.incode.module.country.dom.impl.StateRepository;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.OrganisationRepository;
import org.estatio.module.party.dom.PersonRepository;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@EqualsAndHashCode(of={"organisation", "address1", "phone", "email", "fax"})
@Accessors(chain = true)
public class OrganisationCommsBuilder extends BuilderScriptAbstract<OrganisationCommsBuilder> {

    @Getter @Setter
    private Organisation organisation;

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
    private Boolean legalAddress;

    @Getter @Setter
    private String phone;

    @Getter @Setter
    private String fax;

    @Getter @Setter
    private String emailAddress;

    @Getter
    private PostalAddress postalAddress;

    @Getter
    private PhoneOrFaxNumber phoneObj;

    @Getter
    private PhoneOrFaxNumber faxObj;

    @Getter
    private EmailAddress emailAddressObj;

    @Override
    protected void execute(ExecutionContext executionContext) {

        checkParam("organisation", executionContext, Organisation.class);

        if (address1 != null) {

            defaultParam("legalAddress", executionContext, true);

            final Country country = countryRepository.findCountry(countryReference);
            final State state = stateRepository.findState(stateReference);
            this.postalAddress = communicationChannelRepository.newPostal(
                    organisation,
                    CommunicationChannelType.POSTAL_ADDRESS,
                    address1,
                    address2,
                    null,
                    postalCode,
                    city,
                    state,
                    country);
            postalAddress.setLegal(legalAddress);
            transactionService.flushTransaction();
            executionContext.addResult(this, organisation.getReference() + "/postalAddress", postalAddress);
        }

        if (phone != null) {
            this.phoneObj = communicationChannelRepository.newPhoneOrFax(
                    organisation,
                    CommunicationChannelType.PHONE_NUMBER,
                    phone);
            transactionService.flushTransaction();
            executionContext.addResult(this, organisation.getReference() + "/phone", phoneObj);
        }
        if (fax != null) {
            this.faxObj = communicationChannelRepository.newPhoneOrFax(
                    organisation,
                    CommunicationChannelType.FAX_NUMBER,
                    this.fax);
            transactionService.flushTransaction();
            executionContext.addResult(this, organisation.getReference() + "/fax", faxObj);
        }
        if (emailAddress != null) {
            this.emailAddressObj = communicationChannelRepository.newEmail(
                    organisation,
                    CommunicationChannelType.EMAIL_ADDRESS,
                    this.emailAddress);
            transactionService.flushTransaction();
            executionContext.addResult(this, organisation.getReference() + "/email", emailAddressObj);
        }

        executionContext.addResult(this, organisation.getReference(), organisation);
    }

    protected boolean defined(String[] values, int i) {
        return values.length > i && !values[i].isEmpty();
    }


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
