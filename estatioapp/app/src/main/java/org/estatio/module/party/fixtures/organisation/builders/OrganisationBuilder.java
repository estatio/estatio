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
package org.estatio.module.party.fixtures.organisation.builders;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancies;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.OrganisationRepository;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@EqualsAndHashCode(of={"reference"}, callSuper = false)
@ToString(of={"reference"})
@Accessors(chain = true)
public final class OrganisationBuilder
        extends BuilderScriptAbstract<Organisation,OrganisationBuilder> {

    @Getter @Setter
    private String atPath;

    @Getter @Setter
    private String reference;

    @Getter @Setter
    private String name;

    @Getter @Setter
    private Boolean useNumeratorForReference;

    @Getter
    private Organisation object;

    @Override
    protected void execute(ExecutionContext executionContext) {

        checkParam("atPath", executionContext, String.class);
        checkParam("reference", executionContext, String.class);
        checkParam("name", executionContext, String.class);

        defaultParam("useNumeratorForReference", executionContext, false);

        ApplicationTenancy applicationTenancy = applicationTenancies.findTenancyByPath(atPath);

        this.object = organisationRepository.findOrCreateOrganisation(reference, useNumeratorForReference, name, applicationTenancy);

        executionContext.addResult(this, object.getReference(), object);
    }

    protected boolean defined(String[] values, int i) {
        return values.length > i && !values[i].isEmpty();
    }

    // //////////////////////////////////////

    @Inject
    OrganisationRepository organisationRepository;

    @Inject
    ApplicationTenancies applicationTenancies;

}
