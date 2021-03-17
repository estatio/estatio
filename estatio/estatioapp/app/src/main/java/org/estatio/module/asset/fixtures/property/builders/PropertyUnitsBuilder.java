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
package org.estatio.module.asset.fixtures.property.builders;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;

import org.isisaddons.module.fakedata.dom.FakeDataService;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.Unit;
import org.estatio.module.asset.dom.UnitType;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@EqualsAndHashCode(of={"property"}, callSuper = false)
@ToString(of={"property"})
@Accessors(chain = true)
public final class PropertyUnitsBuilder
        extends BuilderScriptAbstract<List<Unit>, PropertyUnitsBuilder> {

    @Getter @Setter
    private Property property;

    @Getter @Setter
    private Integer numberOfUnits;

    @Getter
    private List<Unit> object = Lists.newArrayList();

    @Override
    protected void execute(final ExecutionContext executionContext) {

        checkParam("property", executionContext, Property.class);

        defaultParam("numberOfUnits", executionContext, fakeDataService.ints().between(10,20));

        for (int i = 0; i < getNumberOfUnits(); i++) {
            final int unitNum = i + 1;
            final String unitRef = buildUnitReference(property.getReference(), unitNum);
            final UnitType unitType = fakeDataService.enums().anyOf(UnitType.class);
            final String unitName = fakeDataService.name().firstName();
            final Unit unit = wrap(property).newUnit(unitRef, unitName, unitType);

            unit.setArea(new BigDecimal(unitNum * 100));

            object.add(unit);
            executionContext.addResult(this, unitRef, unit);
        }
    }

    String buildUnitReference(final String propertyReference, final Integer unitNum) {
        return String.format("%1$s-%2$03d", propertyReference, unitNum);
    }

    @Inject
    FakeDataService fakeDataService;


}
