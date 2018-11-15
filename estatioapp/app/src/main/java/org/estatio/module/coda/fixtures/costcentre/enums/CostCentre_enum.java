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
package org.estatio.module.coda.fixtures.costcentre.enums;

import org.apache.isis.applib.fixturescripts.PersonaWithBuilderScript;
import org.apache.isis.applib.fixturescripts.PersonaWithFinder;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.coda.dom.costcentre.CostCentre;
import org.estatio.module.coda.dom.costcentre.CostCentreRepository;
import org.estatio.module.coda.fixtures.costcentre.builders.CostCentreBuilder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(chain = true)
public enum CostCentre_enum
        implements PersonaWithBuilderScript<CostCentre, CostCentreBuilder>,
        PersonaWithFinder<CostCentre> {

    ITRRON1("ITRRON1","RON"),
    ;

    private final String element3;
    private final String extRef3Segment2;

    @Override
    public CostCentreBuilder builder() {
        return new CostCentreBuilder()
                .setElement3(element3)
                .setExtRef3Segment2(extRef3Segment2);
    }

    @Override
    public CostCentre findUsing(final ServiceRegistry2 serviceRegistry) {
        return serviceRegistry.lookupService(CostCentreRepository.class).findByElement3(element3);
    }

}
