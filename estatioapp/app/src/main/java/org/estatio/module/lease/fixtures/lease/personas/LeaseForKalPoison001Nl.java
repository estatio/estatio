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

import org.estatio.module.asset.fixtures.person.personas.PersonAndRolesForJohnDoeNl;
import org.estatio.module.asset.fixtures.property.personas.PropertyAndUnitsAndOwnerAndManagerForKalNl;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.fixtures.LeaseAbstract;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;

public class LeaseForKalPoison001Nl extends LeaseAbstract {

    public static final Lease_enum data = Lease_enum.KalPoison001Nl;


    @Override
    protected void execute(final ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, data.getLandlord_d().toFixtureScript());
        executionContext.executeChild(this, data.getTenant_d().toFixtureScript());
        executionContext.executeChild(this, new PersonAndRolesForJohnDoeNl());
        executionContext.executeChild(this, new PropertyAndUnitsAndOwnerAndManagerForKalNl());

        // exec
        final Lease lease = data.toFixtureScript().build(this, executionContext).getObject();


//        final String partyRefTenant = tenant_d.getRef();
//        final CommunicationChannelType channelType = CommunicationChannelType.EMAIL_ADDRESS;
//
//        addInvoiceAddressForTenant(lease, partyRefTenant, channelType);

    }


}
