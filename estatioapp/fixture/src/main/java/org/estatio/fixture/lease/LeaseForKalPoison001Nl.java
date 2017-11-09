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

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelType;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.occupancy.tags.BrandCoverage;
import org.estatio.module.party.dom.Party;
import org.estatio.module.application.fixtures.property.personas.PropertyAndOwnerAndManagerForKalNl;
import org.incode.module.country.fixture.CountriesRefData;
import org.estatio.module.party.fixtures.organisation.personas.OrganisationForAcmeNl;
import org.estatio.module.party.fixtures.organisation.personas.OrganisationForPoisonNl;
import org.estatio.module.application.fixtures.person.personas.PersonAndRolesForJohnDoeNl;

import static org.incode.module.base.integtests.VT.ld;

public class LeaseForKalPoison001Nl extends LeaseAbstract {

    public static final String REF = "KAL-POISON-001";

    public static final String UNIT_REF = PropertyAndOwnerAndManagerForKalNl.unitReference("001");
    public static final String PARTY_REF_LANDLORD = OrganisationForAcmeNl.REF;
    public static final String PARTY_REF_TENANT = OrganisationForPoisonNl.REF;
    public static final String PARTY_REF_MANAGER = PersonAndRolesForJohnDoeNl.REF;

    public static final String BRAND = "Poison";
    public static final BrandCoverage BRAND_COVERAGE = BrandCoverage.INTERNATIONAL;
    public static final String COUNTRY_OF_ORIGIN_REF = CountriesRefData.NLD;

    @Override
    protected void execute(final ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, new PersonAndRolesForJohnDoeNl());
        executionContext.executeChild(this, new OrganisationForAcmeNl());
        executionContext.executeChild(this, new OrganisationForPoisonNl());
        executionContext.executeChild(this, new PropertyAndOwnerAndManagerForKalNl());

        // exec
        final Party manager = partyRepository.findPartyByReference(PARTY_REF_MANAGER);

        final Lease lease = createLease(
                REF,
                "Poison Amsterdam",
                UNIT_REF,
                BRAND,
                BRAND_COVERAGE,
                COUNTRY_OF_ORIGIN_REF,
                "HEALT&BEAUTY",
                "PERFUMERIE",
                PARTY_REF_LANDLORD,
                PARTY_REF_TENANT,
                ld(2011, 1, 1),
                ld(2020, 12, 31),
                true,
                true,
                manager,
                executionContext);


        final String partyRefTenant = PARTY_REF_TENANT;
        final CommunicationChannelType channelType = CommunicationChannelType.EMAIL_ADDRESS;

        addInvoiceAddressForTenant(lease, partyRefTenant, channelType);

    }


}
