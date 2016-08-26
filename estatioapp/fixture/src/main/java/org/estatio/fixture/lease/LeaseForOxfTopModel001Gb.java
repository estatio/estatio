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

import java.util.SortedSet;

import javax.inject.Inject;

import org.estatio.dom.agreement.AgreementRole;
import org.estatio.dom.agreement.AgreementRoleCommunicationChannelType;
import org.estatio.dom.agreement.AgreementRoleCommunicationChannelTypeRepository;
import org.estatio.dom.agreement.AgreementRoleTypeRepository;
import org.estatio.dom.communicationchannel.CommunicationChannel;
import org.estatio.dom.communicationchannel.CommunicationChannelType;
import org.estatio.dom.communicationchannel.CommunicationChannelRepository;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseConstants;
import org.estatio.dom.lease.tags.BrandCoverage;
import org.estatio.dom.party.Party;
import org.estatio.fixture.asset.PropertyForOxfGb;
import org.estatio.fixture.geography.CountriesRefData;
import org.estatio.fixture.party.OrganisationForHelloWorldGb;
import org.estatio.fixture.party.OrganisationForTopModelGb;
import org.estatio.fixture.party.PersonForGinoVannelliGb;

import static org.estatio.integtests.VT.ld;

public class LeaseForOxfTopModel001Gb extends LeaseAbstract {

    public static final String REF = "OXF-TOPMODEL-001";

    public static final String UNIT_REF = PropertyForOxfGb.unitReference("001");
    public static final String PARTY_REF_LANDLORD = OrganisationForHelloWorldGb.REF;
    public static final String PARTY_REF_TENANT = OrganisationForTopModelGb.REF;

    public static final String BRAND = "Topmodel";
    public static final BrandCoverage BRAND_COVERAGE = BrandCoverage.NATIONAL;
    public static final String COUNTRY_OF_ORIGIN_REF = CountriesRefData.GBR;

    @Inject
    private AgreementRoleTypeRepository agreementRoleTypeRepository;

    @Inject
    private AgreementRoleCommunicationChannelTypeRepository agreementRoleCommunicationChannelTypeRepository;

    @Inject
    private CommunicationChannelRepository communicationChannelRepository;

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        if (isExecutePrereqs()) {
            executionContext.executeChild(this, new PersonForGinoVannelliGb());
            executionContext.executeChild(this, new OrganisationForHelloWorldGb());
            executionContext.executeChild(this, new OrganisationForTopModelGb());
            executionContext.executeChild(this, new PersonForGinoVannelliGb());
            executionContext.executeChild(this, new PropertyForOxfGb());
        }

        // exec
        Party manager = partyRepository.findPartyByReference(PersonForGinoVannelliGb.REF);
        Lease lease = createLease(
                REF,
                "Topmodel Lease",
                UNIT_REF,
                BRAND,
                BRAND_COVERAGE,
                COUNTRY_OF_ORIGIN_REF,
                "FASHION",
                "WOMEN",
                PARTY_REF_LANDLORD,
                PARTY_REF_TENANT,
                ld(2010, 7, 15),
                ld(2022, 7, 14),
                true,
                true,
                manager,
                executionContext);
        createAddress(lease, LeaseConstants.ARCCT_ADMINISTRATION_ADDRESS);
        createAddress(lease, LeaseConstants.ARCCT_INVOICE_ADDRESS);
    }

    private void createAddress(Lease lease, String addressType) {
        AgreementRole agreementRole = lease.findRoleWithType(agreementRoleTypeRepository.findByTitle(LeaseConstants.ART_TENANT), ld(2010, 7, 15));
        AgreementRoleCommunicationChannelType agreementRoleCommunicationChannelType = agreementRoleCommunicationChannelTypeRepository
                .findByTitle(addressType);
        final SortedSet<CommunicationChannel> channels = communicationChannelRepository.findByOwnerAndType(lease.getSecondaryParty(), CommunicationChannelType.POSTAL_ADDRESS);
        final CommunicationChannel postalAddress = channels.first();
        agreementRole.addCommunicationChannel(agreementRoleCommunicationChannelType, postalAddress, null);
    }

}
