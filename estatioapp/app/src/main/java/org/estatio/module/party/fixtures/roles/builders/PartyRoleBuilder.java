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
package org.estatio.module.party.fixtures.roles.builders;

import java.util.Objects;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;

import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.role.IPartyRoleType;
import org.estatio.module.party.dom.role.PartyRole;
import org.estatio.module.party.dom.role.PartyRoleType;
import org.estatio.module.party.dom.role.PartyRoleTypeRepository;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@EqualsAndHashCode(of={"reference"}, callSuper = false)
@ToString(of={"reference"})
@Accessors(chain = true)
public final class PartyRoleBuilder
        extends BuilderScriptAbstract<PartyRole, PartyRoleBuilder> {

    @Getter @Setter
    private Organisation organisation;

    @Getter @Setter
    private IPartyRoleType partyRoleType;

    @Getter
    private PartyRole object;

    @Override
    protected void execute(ExecutionContext executionContext) {

        checkParam("organisation", executionContext, Organisation.class);
        checkParam("partyRoleType", executionContext, IPartyRoleType.class);

        final PartyRoleType roleType = partyRoleType.findUsing(partyRoleTypeRepository);
        PartyRole partyRole = organisation.getPartyRole(roleType);
        if(partyRole == null) {
            organisation.addRole(roleType);
            transactionService.flushTransaction();
            partyRole = organisation.getPartyRole(roleType);
        }

        object = partyRole;

        executionContext.addResult(this, object.getRoleType().getKey(), object);
    }

    @Inject
    PartyRoleTypeRepository partyRoleTypeRepository;

    @Override public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        final PartyRoleBuilder that = (PartyRoleBuilder) o;
        return Objects.equals(organisation, that.organisation) &&
                Objects.equals(object, that.object);
    }

    @Override public int hashCode() {
        return Objects.hash(organisation, object);
    }
}

