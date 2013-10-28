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
import org.apache.isis.applib.annotation.DescribedAs;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Prototype;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.utils.StringUtils;

public abstract class Units<T extends Unit> extends EstatioDomainService<T> {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Units(final Class<T> unitClass) {
        super((Class) Units.class, unitClass);
    }

    // //////////////////////////////////////

    @Programmatic
    public Unit newUnit(
            final String reference, 
            final String name,
            final UnitType type) {
        final Unit unit = newTransientInstance();
        unit.setReference(reference);
        unit.setName(name);
        unit.setType(type);
        persist(unit);
        return unit;
    }

    // //////////////////////////////////////
    
    @ActionSemantics(Of.SAFE)
    @MemberOrder(name="Fixed Assets", sequence = "2")
    public List<T> findUnits(
            final @Named("Reference or Name") @DescribedAs("May include wildcards '*' and '?'") 
            String referenceOrName) {
        // this currently only looks for UnitsForLease, and no other subtypes (none existent at time of writing)
        return allMatches("findByReferenceOrName", 
                "referenceOrName", StringUtils.wildcardToRegex(referenceOrName));
    }

    @ActionSemantics(Of.SAFE)
    @Hidden
    public T findUnitByReference(final String reference) {
        return firstMatch("findByReference", "reference", StringUtils.wildcardToRegex(reference));
    }

    // //////////////////////////////////////

    @Hidden
    public List<T> autoComplete(final String searchPhrase) {
        return findUnits("*".concat(searchPhrase).concat("*"));
    }
    
    // //////////////////////////////////////

    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(name="Fixed Assets", sequence = "99")
    public List<T> allUnits() {
        return allInstances();
    }

}
