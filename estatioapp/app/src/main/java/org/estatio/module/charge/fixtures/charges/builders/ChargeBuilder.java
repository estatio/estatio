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
package org.estatio.module.charge.fixtures.charges.builders;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.module.charge.dom.Applicability;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeGroup;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.tax.dom.Tax;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@EqualsAndHashCode(of={"ref"}, callSuper = false)
@Accessors(chain = true)
public class ChargeBuilder extends BuilderScriptAbstract<Charge, ChargeBuilder> {

    @Getter @Setter
    String ref;
    @Getter @Setter
    String name;
    @Getter @Setter
    String description;
    @Getter @Setter
    Applicability applicability;

    @Getter @Setter
    ChargeGroup chargeGroup;
    @Getter @Setter
    ApplicationTenancy applicationTenancy;
    @Getter @Setter
    Tax tax;

    @Getter
    Charge object;

    @Override
    protected void execute(final ExecutionContext executionContext) {

        checkParam("ref", executionContext, String.class);
        checkParam("name", executionContext, String.class);
        checkParam("applicability", executionContext, Applicability.class);

        checkParam("chargeGroup", executionContext, ChargeGroup.class);
        checkParam("applicationTenancy", executionContext, ApplicationTenancy.class);
        checkParam("tax", executionContext, Tax.class);

        defaultParam("description", executionContext, getRef());

        object = repository.upsert(
                ref, name, description,
                applicationTenancy, applicability, tax, chargeGroup);
    }

    @Inject
    ChargeRepository repository;

}
