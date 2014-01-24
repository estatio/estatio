/*
 *  Copyright 2012-2014 Eurocommercial Properties NV
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
package org.estatio.integration.glue.lease;

import cucumber.api.java.en.Given;

import org.apache.isis.core.specsupport.specs.CukeGlueAbstract;

import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.Leases;

public class LeaseGlue extends CukeGlueAbstract {

    @Given(".*there is.* lease \"([^\"]*)\"$")
    public void given_lease(final String leaseReference) throws Throwable {
        final Lease lease = service(Leases.class).findLeaseByReference(leaseReference);
        putVar("lease", leaseReference, lease);
        putVar("agreement", leaseReference, lease);
    }

    
}
