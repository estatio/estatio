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
package org.estatio.module.charge.fixtures.chargegroups.builders;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;

import org.estatio.module.charge.dom.ChargeGroup;
import org.estatio.module.charge.dom.ChargeGroupRepository;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@EqualsAndHashCode(of={"ref"},callSuper = false)
@Accessors(chain = true)
public class ChargeGroupBuilder extends BuilderScriptAbstract<ChargeGroup, ChargeGroupBuilder> {

    @Getter @Setter
    String ref;
    @Getter @Setter
    String description;

    @Getter
    ChargeGroup object;

    @Override
    protected void execute(final ExecutionContext executionContext) {

        checkParam("ref", executionContext, String.class);

        defaultParam("description", executionContext, getRef());

        this.object = repository.upsert(getRef(), getDescription());
    }

    @Inject
    ChargeGroupRepository repository;

}
