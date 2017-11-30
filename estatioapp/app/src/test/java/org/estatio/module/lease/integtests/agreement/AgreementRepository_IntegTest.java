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
package org.estatio.module.lease.integtests.agreement;

import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.agreement.dom.Agreement;
import org.estatio.module.agreement.dom.AgreementRepository;
import org.estatio.module.agreement.dom.role.AgreementRoleTypeRepository;
import org.estatio.module.agreement.dom.type.AgreementType;
import org.estatio.module.agreement.dom.type.AgreementTypeRepository;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.fixtures.lease.personas.LeaseForKalPoison001Nl;
import org.estatio.module.lease.fixtures.lease.personas.LeaseForOxfMediaX002Gb;
import org.estatio.module.lease.fixtures.lease.personas.LeaseForOxfMiracl005Gb;
import org.estatio.module.lease.fixtures.lease.personas.LeaseForOxfPoison003Gb;
import org.estatio.module.lease.fixtures.lease.personas.LeaseForOxfPret004Gb;
import org.estatio.module.lease.fixtures.lease.personas.LeaseForOxfTopModel001Gb;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class AgreementRepository_IntegTest extends LeaseModuleIntegTestAbstract {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {

                // 5 oxford leases, 1 kal
                executionContext.executeChild(this, new LeaseForOxfTopModel001Gb());
                executionContext.executeChild(this, new LeaseForOxfMediaX002Gb());
                executionContext.executeChild(this, new LeaseForOxfPoison003Gb());
                executionContext.executeChild(this, new LeaseForOxfPret004Gb());
                executionContext.executeChild(this, new LeaseForOxfMiracl005Gb());
                executionContext.executeChild(this, new LeaseForKalPoison001Nl());
            }
        });

        lease = leaseRepository.findLeaseByReference(LeaseForOxfTopModel001Gb.REF);
    }

    @Inject
    AgreementRepository agreementRepository;

    @Inject
    AgreementTypeRepository agreementTypeRepository;

    @Inject
    AgreementRoleTypeRepository agreementRoleTypeRepository;

    @Inject
    LeaseRepository leaseRepository;

    Lease lease;

    public static class FindByTypeAndReferenceOrName extends AgreementRepository_IntegTest {

        @Test
        public void whenPresent() throws Exception {
            final AgreementType type = agreementTypeRepository.find("Lease");
            assertNotNull(type);
            final List<Agreement> results = agreementRepository.findByTypeAndReferenceOrName(type, ".*OXF.*");
            assertThat(results.size(), is(5));
        }
    }

    public static class FindAgreementByReference extends AgreementRepository_IntegTest {

        @Test
        public void happyCase() throws Exception {
            Lease agreement = leaseRepository.findLeaseByReference(lease.getReference());
            assertThat(agreement.getName(), is(lease.getName()));
        }
    }

}
