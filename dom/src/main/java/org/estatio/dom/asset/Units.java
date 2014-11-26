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
package org.estatio.dom.asset;

import java.util.List;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.DescribedAs;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.annotation.RegEx;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.RegexValidation;
import org.estatio.dom.utils.StringUtils;

@DomainService(repositoryFor = Unit.class)
public class Units extends EstatioDomainService<Unit> {

    public Units() {
        super(Units.class, Unit.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.NON_IDEMPOTENT)
    public Unit newUnit(
            final Property property,
            final @RegEx(validation = RegexValidation.Unit.REFERENCE, caseSensitive = true) String reference,
            final String name,
            final UnitType type) {
        final Unit unit = newTransientInstance();
        unit.setReference(reference);
        unit.setName(name);
        unit.setType(type);
        unit.setProperty(property);
        persist(unit);
        return unit;
    }

    public UnitType default3NewUnit() {
        return UnitType.BOUTIQUE;
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(name = "Fixed Assets", sequence = "2")
    public List<Unit> findUnits(
            final @Named("Reference or Name") @DescribedAs("May include wildcards '*' and '?'") String referenceOrName) {
        return allMatches("findByReferenceOrName",
                "referenceOrName", StringUtils.wildcardToCaseInsensitiveRegex(referenceOrName));
    }

    @ActionSemantics(Of.SAFE)
    @Hidden
    public Unit findUnitByReference(final String reference) {
        return firstMatch("findByReference", "reference", reference);
    }

    // //////////////////////////////////////

    @Hidden
    public List<Unit> autoComplete(final String searchPhrase) {
        return findUnits("*".concat(searchPhrase).concat("*"));
    }

    // //////////////////////////////////////

    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(name = "Fixed Assets", sequence = "99")
    public List<Unit> allUnits() {
        return allInstances();
    }

}
