/*
 *  Copyright 2012-2013 Eurocommercial Properties NV
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
package org.estatio.dom.specs;

import cucumber.api.Transformer;

import org.apache.isis.core.unittestsupport.scenarios.ScenarioExecution;

import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.party.Organisations;

/**
 * A set of Estatio-specific converters for {@link EstatioTransactionalObject transactional object}s.
 * 
 * <p>
 * These converters look up from the {@link ScenarioExecutionIntegrationScopeAbstract scenario}, then fall back to looking up from
 * the associated repository (if there is one for that type).
 */
public class ETO  {
    private ETO() {}

    /**
     * Looks up from session only.
     */
    public class DomainObject extends Transformer<org.estatio.dom.EstatioTransactionalObject<?,?>> {

        @Override
        public org.estatio.dom.EstatioTransactionalObject<?,?> transform(String id) {
            return ScenarioExecution.current().get(null, id, org.estatio.dom.EstatioTransactionalObject.class);
        }
    }

    /**
     * Looks up from session only (abstract class).
     */
    public static class Agreement extends Transformer<org.estatio.dom.agreement.Agreement<?>> {
        @Override
        public org.estatio.dom.agreement.Agreement<?> transform(String id) {
            return ScenarioExecution.current().get("agreement", id, org.estatio.dom.agreement.Agreement.class);
        }
    }

    /**
     * Looks up from session, else repository
     */
    public static class Lease extends Transformer<org.estatio.dom.lease.Lease> {
        @Override
        public org.estatio.dom.lease.Lease transform(String id) {
            final org.estatio.dom.lease.Lease lease = ScenarioExecution.current().get("lease", id, org.estatio.dom.lease.Lease.class);
            return lease != null? lease: ScenarioExecution.current().service(Leases.class).findLeaseByReference(id);
        }
    }
    
    /**
     * Looks up from session, else repository.
     */
    public static class Organisation extends Transformer<org.estatio.dom.party.Organisation> {
        @Override
        public org.estatio.dom.party.Organisation transform(String id) {
            final org.estatio.dom.party.Organisation organisation = ScenarioExecution.current().get("organisation", id, org.estatio.dom.party.Organisation.class);
            return organisation != null? organisation: ScenarioExecution.current().service(Organisations.class).findOrganisation(id);
        }
    }
}