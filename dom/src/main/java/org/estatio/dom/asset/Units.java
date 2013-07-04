/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
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
package org.estatio.dom.asset;

import java.util.List;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.query.QueryDefault;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.utils.StringUtils;

public abstract class Units<T extends Unit> extends EstatioDomainService<T> {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Units(final Class<T> unitClass) {
        super((Class) Units.class, unitClass);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(name="Assets", sequence = "1")
    public Unit newUnit(final @Named("Reference") String reference, final @Named("Name") String name) {
        return newUnit(reference, name, UnitType.BOUTIQUE);
    }

    @Hidden
    public Unit newUnit(final String reference, final String name, final UnitType type) {
        final Unit unit = newTransientInstance();
        unit.setReference(reference);
        unit.setName(name);
        unit.setUnitType(type);
        persist(unit);
        return unit;
    }

    // //////////////////////////////////////
    
    @ActionSemantics(Of.SAFE)
    @MemberOrder(name="Assets", sequence = "2")
    public List<T> findUnitsByReference(final @Named("Reference") String reference) {
        // this currently only looks for UnitsForLease, and no other subtypes (none existent at time of writing)
        return allMatches("findByReference", "reference", StringUtils.wildcardToRegex(reference));
    }

    @ActionSemantics(Of.SAFE)
    @Hidden
    public T findUnitByReference(final String reference) {
        return firstMatch("findByReference", "reference", StringUtils.wildcardToRegex(reference));
    }

    // //////////////////////////////////////

    @Hidden
    public List<T> autoComplete(String searchPhrase) {
        return findUnitsByReference("*".concat(searchPhrase).concat("*"));
    }
    
    // //////////////////////////////////////

    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(name="Assets", sequence = "99")
    public List<T> allUnits() {
        return allInstances();
    }

}
