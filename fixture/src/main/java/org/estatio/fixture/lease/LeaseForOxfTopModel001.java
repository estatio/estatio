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
package org.estatio.fixture.lease;

import static org.estatio.integtests.VT.ld;

import javax.inject.Inject;

import org.estatio.dom.agreement.AgreementRole;
import org.estatio.dom.agreement.AgreementRoleCommunicationChannelType;
import org.estatio.dom.agreement.AgreementRoleCommunicationChannelTypes;
import org.estatio.dom.agreement.AgreementRoleTypes;
import org.estatio.dom.communicationchannel.CommunicationChannel;
import org.estatio.dom.communicationchannel.CommunicationChannelType;
import org.estatio.dom.communicationchannel.CommunicationChannels;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseConstants;
import org.estatio.dom.party.Party;
import org.estatio.fixture.asset.PropertyForOxf;
import org.estatio.fixture.party.OrganisationForHelloWorld;
import org.estatio.fixture.party.OrganisationForTopModel;
import org.estatio.fixture.party.PersonForJohnDoe;

public class LeaseForOxfTopModel001 extends LeaseAbstract {

    public static final String LEASE_REFERENCE = "OXF-TOPMODEL-001";
    public static final String UNIT_REFERENCE = PropertyForOxf.unitReference("001");
    public static final String LANDLORD_REFERENCE = OrganisationForHelloWorld.PARTY_REFERENCE;
    public static final String TENANT_REFERENCE = OrganisationForTopModel.PARTY_REFERENCE;

    @Inject
    private AgreementRoleTypes agreementRoleTypes;

    @Inject
    private AgreementRoleCommunicationChannelTypes agreementRoleCommunicationChannelTypes;

    @Inject
    private CommunicationChannels communicationChannels;

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        if (isExecutePrereqs()) {
            execute(new PersonForJohnDoe(), executionContext);
            execute(new OrganisationForHelloWorld(), executionContext);
            execute(new OrganisationForTopModel(), executionContext);
            execute(new PropertyForOxf(), executionContext);
        }

        // exec
        Party manager = parties.findPartyByReference(PersonForJohnDoe.PARTY_REFERENCE);
        Lease lease = createLease(
                LEASE_REFERENCE, "Topmodel Lease",
                UNIT_REFERENCE,
                "Topmodel", "FASHION", "WOMEN",
                LANDLORD_REFERENCE,
                TENANT_REFERENCE,
                ld(2010, 7, 15), ld(2022, 7, 14), true, true, manager,
                executionContext);

        AgreementRole agreementRole = lease.findRoleWithType(agreementRoleTypes.findByTitle(LeaseConstants.ART_TENANT), ld(2010, 7, 15));
        AgreementRoleCommunicationChannelType agreementRoleCommunicationChannelType = agreementRoleCommunicationChannelTypes.findByTitle(LeaseConstants.ARCCT_ADMINISTRATION_ADDRESS);
        final CommunicationChannel postalAddress = communicationChannels.findByOwnerAndType(lease.getSecondaryParty(), CommunicationChannelType.POSTAL_ADDRESS).first();
        agreementRole.addCommunicationChannel(agreementRoleCommunicationChannelType, postalAddress);

    }

}
