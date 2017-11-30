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
package org.estatio.module.lease.fixtures.lease.personas;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelType;

import org.estatio.module.asset.fixtures.person.personas.PersonAndRolesForJohnSmithGb;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.asset.fixtures.property.personas.PropertyAndUnitsAndOwnerAndManagerForOxfGb;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.fixtures.LeaseAbstract;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.party.fixtures.organisation.enums.OrganisationAndComms_enum;

public class LeaseForOxfPoison003Gb extends LeaseAbstract {

    public static final Lease_enum data = Lease_enum.OxfPoison003Gb;
    public static final Property_enum property_d = Property_enum.OxfGb;
    public static final OrganisationAndComms_enum tenant_d = OrganisationAndComms_enum.PoisonGb;

    public static final String REF = data.getRef();

    public static final String PARTY_REF_TENANT = tenant_d.getRef();

    @Override
    protected void execute(final ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, data.getLandlord_d().toFixtureScript());
        executionContext.executeChild(this, data.getTenant_d().toFixtureScript());
        executionContext.executeChild(this, new PersonAndRolesForJohnSmithGb());
        executionContext.executeChild(this, new PropertyAndUnitsAndOwnerAndManagerForOxfGb());

        // exec
        final Lease lease = data.toFixtureScript().build(this, executionContext).getObject();

        addInvoiceAddressForTenant(lease,
                tenant_d.getRef(),
                CommunicationChannelType.EMAIL_ADDRESS);
    }

}
