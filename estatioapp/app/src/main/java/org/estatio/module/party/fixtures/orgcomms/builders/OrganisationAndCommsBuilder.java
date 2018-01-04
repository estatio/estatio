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

import java.util.List;

import com.google.common.collect.Lists;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;

import org.incode.module.communications.dom.impl.commchannel.EmailAddress;
import org.incode.module.communications.dom.impl.commchannel.PhoneOrFaxNumber;
import org.incode.module.communications.dom.impl.commchannel.PostalAddress;

import org.incode.module.country.fixtures.enums.Country_enum;
import org.estatio.module.party.dom.Organisation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@EqualsAndHashCode(of={"partyReference", "comms"}, callSuper = false)
@ToString(of={"partyReference", "comms"})
@Accessors(chain = true)
public final class OrganisationAndCommsBuilder
        extends BuilderScriptAbstract<Organisation, OrganisationAndCommsBuilder> {

    @Getter @Setter
    private Organisation organisation;

    @Getter @Setter
    private List<CommsSpec> comms = Lists.newArrayList();

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
    protected void execute(ExecutionContext executionContext) {

        this.object = organisation;

        for (final OrganisationAndCommsBuilder.CommsSpec comms : this.comms) {
            final OrganisationCommsBuilder organisationCommsBuilder = new OrganisationCommsBuilder();
            organisationCommsBuilder
                    .setOrganisation(this.object)
                    .setAddress1(comms.getAddress1())
                    .setAddress2(comms.getAddress2())
                    .setCity(comms.getCity())
                    .setPostalCode(comms.getPostalCode())
                    .setStateReference(comms.getStateReference())
                    .setCountry_d(comms.getCountry())
                    .setLegalAddress(comms.getLegalAddress())
                    .setPhone(comms.getPhone())
                    .setFax(comms.getFax())
                    .setEmailAddress(comms.getEmailAddress())
                    .build(this, executionContext);

            if(this.postalAddress == null) {
                this.postalAddress = organisationCommsBuilder.getPostalAddress();
            }
            if(this.emailAddressObj == null) {
                this.emailAddressObj = organisationCommsBuilder.getEmailAddressObj();
            }
            if(this.phoneObj == null) {
                this.phoneObj = organisationCommsBuilder.getPhoneObj();
            }
            if(this.faxObj == null) {
                this.faxObj = organisationCommsBuilder.getFaxObj();
            }
        }
    }

    @AllArgsConstructor
    @Data
    public static class CommsSpec {
        private final String address1;
        private final String address2;
        private final String postalCode;
        private final String city;
        private final String stateReference;
        private final Country_enum country;
        private final Boolean legalAddress;

        private final String phone;
        private final String fax;
        private final String emailAddress;
    }
}
