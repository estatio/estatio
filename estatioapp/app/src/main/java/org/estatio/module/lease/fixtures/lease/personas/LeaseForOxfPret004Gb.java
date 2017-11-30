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

import org.estatio.module.lease.fixtures.LeaseAbstract;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;

public class LeaseForOxfPret004Gb extends LeaseAbstract {

    public static final Lease_enum data = Lease_enum.OxfPret004Gb;


    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
//        executionContext.executeChild(this, data.getLandlord_d().toFixtureScript());
//        executionContext.executeChild(this, data.getTenant_d().toFixtureScript());
//        executionContext.executeChild(this, new PersonAndRolesForGinoVannelliGb());
//        executionContext.executeChild(this, new PropertyAndUnitsAndOwnerAndManagerForOxfGb());

        // exec
        data.toFixtureScript().build(this, executionContext);

    }


}
