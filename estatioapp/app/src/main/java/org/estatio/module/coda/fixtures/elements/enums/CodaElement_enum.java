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
package org.estatio.module.coda.fixtures.elements.enums;

import org.apache.isis.applib.fixturescripts.PersonaWithBuilderScript;
import org.apache.isis.applib.fixturescripts.PersonaWithFinder;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.coda.dom.elements.CodaElement;
import org.estatio.module.coda.dom.elements.CodaElementLevel;
import org.estatio.module.coda.dom.elements.CodaElementRepository;
import org.estatio.module.coda.fixtures.elements.builders.CodaElementBuilder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(chain = true)
public enum CodaElement_enum
        implements PersonaWithBuilderScript<CodaElement, CodaElementBuilder>,
        PersonaWithFinder<CodaElement> {

    FRL5_12345(CodaElementLevel.LEVEL_5,"FR12345", "FR12345"),
    FR23456(CodaElementLevel.LEVEL_5,"FR23456", "FR23456"),
    FRL4_77777(CodaElementLevel.LEVEL_4,"777777", "777777"),
    ;

    private final CodaElementLevel codaElementLevel;
    private final String code;
    private final String name;

    @Override
    public CodaElementBuilder builder() {
        return new CodaElementBuilder()
                .setCodaElementLevel(codaElementLevel)
                .setCode(code)
                .setName(name);
    }

    @Override
    public CodaElement findUsing(final ServiceRegistry2 serviceRegistry) {
        return serviceRegistry.lookupService(CodaElementRepository.class).findByLevelAndCode(codaElementLevel, code);
    }

}
