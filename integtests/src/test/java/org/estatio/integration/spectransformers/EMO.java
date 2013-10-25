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
package org.estatio.integration.spectransformers;

import org.apache.isis.core.specsupport.scenarios.ScenarioExecution;

import org.estatio.dom.EstatioMutableObject;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.party.Organisations;
import org.estatio.dom.party.Parties;

/**
 * A set of Estatio-specific converters for {@link EstatioMutableAndLockableObject transactional object}s.
 * 
 * <p>
 * These converters look up from the {@link ScenarioExecutionIntegrationScopeAbstract scenario}, then fall back to looking up from
 * the associated repository (if there is one for that type).
 */
public class EMO  {
    private EMO() {}

    /**
     * Looks up from session only.
     */
    public static class DomainObject extends NullRecognizingTransformer<EstatioMutableObject<?>> {

        @Override
        public org.estatio.dom.EstatioMutableObject<?> transformNonNull(String id) {
            return ScenarioExecution.current().getVar(null, id, org.estatio.dom.EstatioMutableObject.class);
        }
    }

    /**
     * Looks up from session only (abstract class).
     */
    public static class Agreement extends NullRecognizingTransformer<org.estatio.dom.agreement.Agreement> {
        @Override
        public org.estatio.dom.agreement.Agreement transformNonNull(String id) {
            return ScenarioExecution.current().getVar("agreement", id, org.estatio.dom.agreement.Agreement.class);
        }
    }

    /**
     * Looks up from session, else repository
     */
    public static class Lease extends NullRecognizingTransformer<org.estatio.dom.lease.Lease> {
        @Override
        public org.estatio.dom.lease.Lease transformNonNull(String id) {
            try {
                return ScenarioExecution.current().getVar("lease", id, org.estatio.dom.lease.Lease.class);
            } catch(IllegalStateException e) {
                return ScenarioExecution.current().service(Leases.class).findLeaseByReference(id);
            }
        }
    }
    
    /**
     * Looks up from session, else repository.
     */
    public static class Party extends NullRecognizingTransformer<org.estatio.dom.party.Party> {
        @Override
        public org.estatio.dom.party.Party transformNonNull(String id) {
            try {
                return ScenarioExecution.current().getVar("party", id, org.estatio.dom.party.Party.class);
            } catch(IllegalStateException e) {
                return ScenarioExecution.current().service(Parties.class).findPartyByReference(id);
            }
        }
    }

    /**
     * Looks up from session, else repository.
     */
    public static class Organisation extends NullRecognizingTransformer<org.estatio.dom.party.Organisation> {
        @Override
        public org.estatio.dom.party.Organisation transformNonNull(String id) {
            try {
                return ScenarioExecution.current().getVar("organisation", id, org.estatio.dom.party.Organisation.class);
            } catch(IllegalStateException e) {
                return ScenarioExecution.current().service(Organisations.class).findOrganisation(id);
            }
        }
    }

}