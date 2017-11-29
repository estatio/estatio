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
package org.estatio.module.lease.fixtures.lease;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelType;

import org.estatio.module.asset.fixtures.person.enums.Person_enum;
import org.estatio.module.asset.fixtures.person.personas.PersonAndRolesForJohnSmithGb;
import org.estatio.module.asset.fixtures.property.personas.PropertyAndUnitsAndOwnerAndManagerForOxfGb;
import org.estatio.module.country.fixtures.enums.Country_enum;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.occupancy.tags.BrandCoverage;
import org.estatio.module.lease.fixtures.LeaseAbstract;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.fixtures.organisation.enums.Organisation_enum;
import org.estatio.module.party.fixtures.organisation.personas.OrganisationForHelloWorldGb;
import org.estatio.module.party.fixtures.organisation.personas.OrganisationForPoisonGb;

import static org.incode.module.base.integtests.VT.ld;

public class LeaseForOxfPoison003Gb extends LeaseAbstract {

    public static final String REF = "OXF-POISON-003";
    public static final String UNIT_REFERENCE = PropertyAndUnitsAndOwnerAndManagerForOxfGb.unitReference("003");
    public static final String PARTY_REF_LANDLORD = Organisation_enum.HelloWorldGb.getRef();

    public static final String BRAND = "Poison";
    public static final BrandCoverage BRAND_COVERAGE = BrandCoverage.INTERNATIONAL;
    public static final String COUNTRY_OF_ORIGIN_REF = Country_enum.NLD.getRef3();

    public static final String PARTY_REF_TENANT = Organisation_enum.PoisonGb.getRef();
    public static final String PARTY_REF_MANAGER = Person_enum.JohnSmithGb.getRef();

    @Override
    protected void execute(final ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, new OrganisationForHelloWorldGb());
        executionContext.executeChild(this, new OrganisationForPoisonGb());
        executionContext.executeChild(this, new PersonAndRolesForJohnSmithGb());
        executionContext.executeChild(this, new PropertyAndUnitsAndOwnerAndManagerForOxfGb());

        // exec
        final Party manager = partyRepository.findPartyByReference(PARTY_REF_MANAGER);
        final Lease lease = createLease(
                REF,
                "Poison Lease",
                UNIT_REFERENCE,
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
