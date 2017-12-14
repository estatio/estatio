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
package org.estatio.module.party.fixtures.orgcomms.builders;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelRepository;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelType;
import org.incode.module.communications.dom.impl.commchannel.EmailAddress;
import org.incode.module.communications.dom.impl.commchannel.PhoneOrFaxNumber;
import org.incode.module.communications.dom.impl.commchannel.PostalAddress;
import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.dom.impl.State;
import org.incode.module.country.dom.impl.StateRepository;

import org.estatio.module.country.fixtures.enums.Country_enum;
import org.estatio.module.party.dom.Organisation;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@EqualsAndHashCode(of={"organisation", "address1", "phone", "emailAddress", "fax"}, callSuper = false)
@ToString(of={"organisation", "address1", "phone", "emailAddress", "fax"})
@Accessors(chain = true)
public final class OrganisationCommsBuilder
        extends BuilderScriptAbstract<Organisation, OrganisationCommsBuilder> {

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
    private Country_enum country_d;

    @Getter @Setter
    private Boolean legalAddress;

    @Getter @Setter
    private String phone;

    @Getter @Setter
    private String fax;

    @Getter @Setter
    private String emailAddress;

    @Getter
    private Organisation object;

    @Getter
    private PostalAddress postalAddress;

    @Getter
    private PhoneOrFaxNumber phoneObj;

    @Getter
    private PhoneOrFaxNumber faxObj;

    @Getter
    private EmailAddress emailAddressObj;

    @Override
    protected void execute(ExecutionContext ec) {

        checkParam("organisation", ec, Organisation.class);

        if (address1 != null) {
            createPostalAddress(ec);
        }

        if (phone != null) {
            createPhone(ec);
        }
        if (fax != null) {
            createFax(ec);
        }
        if (emailAddress != null) {
            createEmailAddress(ec);
        }

        object = organisation;
    }

    private void createPostalAddress(final ExecutionContext ec) {
        defaultParam("legalAddress", ec, true);

        final Country country = objectFor(this.country_d, ec);

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
        ec.addResult(this, organisation.getReference() + "/postalAddress", postalAddress);
    }

    private void createPhone(final ExecutionContext ec) {
        this.phoneObj = communicationChannelRepository.newPhoneOrFax(
                organisation,
                CommunicationChannelType.PHONE_NUMBER,
                phone);
        transactionService.flushTransaction();
        ec.addResult(this, organisation.getReference() + "/phone", phoneObj);
    }

    private void createFax(final ExecutionContext ec) {
        this.faxObj = communicationChannelRepository.newPhoneOrFax(
                organisation,
                CommunicationChannelType.FAX_NUMBER,
                this.fax);
        transactionService.flushTransaction();
        ec.addResult(this, organisation.getReference() + "/fax", faxObj);
    }

    private void createEmailAddress(final ExecutionContext ec) {
        this.emailAddressObj = communicationChannelRepository.newEmail(
                organisation,
                CommunicationChannelType.EMAIL_ADDRESS,
                this.emailAddress);
        transactionService.flushTransaction();
        ec.addResult(this, organisation.getReference() + "/email", emailAddressObj);
    }


    @Inject
    StateRepository stateRepository;

    @Inject
    CommunicationChannelRepository communicationChannelRepository;

}
