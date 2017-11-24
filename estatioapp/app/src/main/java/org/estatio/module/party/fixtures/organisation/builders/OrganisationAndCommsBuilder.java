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

import org.incode.module.communications.dom.impl.commchannel.EmailAddress;
import org.incode.module.communications.dom.impl.commchannel.PhoneOrFaxNumber;
import org.incode.module.communications.dom.impl.commchannel.PostalAddress;

import org.estatio.module.base.platform.fixturesupport.BuilderScriptAbstract;
import org.estatio.module.party.dom.Organisation;

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
    private Boolean legalAddress;

    @Getter @Setter
    private String phone;

    @Getter @Setter
    private String fax;

    @Getter @Setter
    private String emailAddress;

    @Getter
    private Organisation organisation;

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

        final OrganisationBuilder organisationBuilder = new OrganisationBuilder();
        this.organisation = organisationBuilder
                .setAtPath(atPath)
                .setPartyName(partyName)
                .setPartyReference(partyReference)
                .setUseNumeratorForReference(useNumeratorForReference)
                .build(this, executionContext)
                .getOrganisation();

        final OrganisationCommsBuilder organisationCommsBuilder = new OrganisationCommsBuilder();
        organisationCommsBuilder
                .setOrganisation(organisation)
                .setAddress1(address1)
                .setAddress2(address2)
                .setCity(city)
                .setCountryReference(countryReference)
                .setStateReference(stateReference)
                .setPostalCode(postalCode)
                .setLegalAddress(legalAddress)
                .setPhone(phone)
                .setFax(fax)
                .setEmailAddress(emailAddress)
                .build(this, executionContext);

        this.postalAddress = organisationCommsBuilder.getPostalAddress();
        this.emailAddressObj = organisationCommsBuilder.getEmailAddressObj();
        this.phoneObj = organisationCommsBuilder.getPhoneObj();
        this.faxObj = organisationCommsBuilder.getFaxObj();

        /*
        checkParam("atPath", executionContext, String.class);
        checkParam("partyReference", executionContext, String.class);
        checkParam("partyName", executionContext, String.class);

        defaultParam("useNumeratorForReference", executionContext, false);

        // if postal address
        defaultParam("legalAddress", executionContext, true);


        ApplicationTenancy applicationTenancy = applicationTenancies.findTenancyByPath(atPath);

        this.party = organisationRepository.newOrganisation(partyReference, useNumeratorForReference, partyName, applicationTenancy);

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
            postalAddress.setLegal(legalAddress);
            transactionService.flushTransaction();
            executionContext.addResult(this, party.getReference() + "/postalAddress", postalAddress);
        }
        if (phone != null) {
            final PhoneOrFaxNumber phoneDO = communicationChannelRepository.newPhoneOrFax(
                    party,
                    CommunicationChannelType.PHONE_NUMBER,
                    phone);
            transactionService.flushTransaction();
            executionContext.addResult(this, party.getReference() + "/phone", phoneDO);
        }
        if (fax != null) {
            final PhoneOrFaxNumber faxDO = communicationChannelRepository.newPhoneOrFax(
                    party,
                    CommunicationChannelType.FAX_NUMBER,
                    this.fax);
            transactionService.flushTransaction();
            executionContext.addResult(this, party.getReference() + "/fax", faxDO);
        }
        if (emailAddress != null) {
            final EmailAddress emailAddressDO = communicationChannelRepository.newEmail(
                    party,
                    CommunicationChannelType.EMAIL_ADDRESS,
                    this.emailAddress);
            transactionService.flushTransaction();
            executionContext.addResult(this, party.getReference() + "/email", emailAddressDO);
        }

        executionContext.addResult(this, party.getReference(), party);
         */
    }

//    protected boolean defined(String[] values, int i) {
//        return values.length > i && !values[i].isEmpty();
//    }
//
//    // //////////////////////////////////////
//
//    @Inject
//    protected CountryRepository countryRepository;
//
//    @Inject
//    protected StateRepository stateRepository;
//
//    @Inject
//    protected OrganisationRepository organisationRepository;
//
//    @Inject
//    protected PersonRepository personRepository;
//
//    @Inject
//    protected CommunicationChannelRepository communicationChannelRepository;
//
//    @Inject
//    protected ApplicationTenancies applicationTenancies;

}
