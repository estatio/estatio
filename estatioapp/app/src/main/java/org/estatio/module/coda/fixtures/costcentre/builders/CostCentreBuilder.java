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
package org.estatio.module.coda.fixtures.costcentre.builders;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;

import org.estatio.module.coda.dom.costcentre.CostCentre;
import org.estatio.module.coda.dom.costcentre.CostCentreRepository;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@EqualsAndHashCode(of={"element3"}, callSuper = false)
@ToString(of={"element3"})
@Accessors(chain = true)
public final class CostCentreBuilder
        extends BuilderScriptAbstract<CostCentre, CostCentreBuilder> {

    @Getter @Setter
    private String element3;

    @Getter @Setter
    private String extRef3Segment2;

    @Getter
    private CostCentre object;

    @Override
    protected void execute(ExecutionContext executionContext) {

        checkParam("element3", executionContext, String.class);
        checkParam("extRef3Segment2", executionContext, String.class);

        object = costCentreRepository.upsert(element3, extRef3Segment2);

        executionContext.addResult(this, object.getElement3(), object);
    }

    @Inject
    CostCentreRepository costCentreRepository;
}

