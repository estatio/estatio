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
package org.estatio.module.coda.fixtures.elements.builders;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;

import org.estatio.module.coda.dom.elements.CodaElement;
import org.estatio.module.coda.dom.elements.CodaElementLevel;
import org.estatio.module.coda.dom.elements.CodaElementRepository;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@EqualsAndHashCode(of={"code"}, callSuper = false)
@ToString(of={"code"})
@Accessors(chain = true)
public final class CodaElementBuilder
        extends BuilderScriptAbstract<CodaElement, CodaElementBuilder> {

    @Getter @Setter
    private CodaElementLevel codaElementLevel;

    @Getter @Setter
    private String code;

    @Getter @Setter
    private String name;

    @Getter
    private CodaElement object;

    @Override
    protected void execute(ExecutionContext executionContext) {

        checkParam("codaElementLevel", executionContext, CodaElementLevel.class);
        checkParam("name", executionContext, String.class);
        checkParam("code", executionContext, String.class);
        checkParam("name", executionContext, String.class);

        object = codaElementRepository.findOrCreate(codaElementLevel, code, name );

        executionContext.addResult(this, object.getCode(), object);
    }

    @Inject
    CodaElementRepository codaElementRepository;
}

