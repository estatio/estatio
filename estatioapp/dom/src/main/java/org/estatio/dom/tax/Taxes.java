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
package org.estatio.dom.tax;

import java.util.List;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.RegexValidation;

@DomainService(repositoryFor = Tax.class)
@DomainServiceLayout(
        named = "Other",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "80.7")
public class Taxes extends EstatioDomainService<Tax> {

    public Taxes() {
        super(Taxes.class, Tax.class);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public Tax newTax(
            final @ParameterLayout(named = "Reference") @Parameter(regexPattern = RegexValidation.REFERENCE) String reference,
            final @ParameterLayout(named = "Name") @Parameter(optionality = Optionality.OPTIONAL) String name) {
        final Tax tax = newTransientInstance();
        tax.setReference(reference);
        tax.setName(name);
        persist(tax);
        return tax;
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "2")
    public List<Tax> allTaxes() {
        return allInstances();
    }

    // //////////////////////////////////////

    @Programmatic
    public Tax findTaxByReference(final String reference) {
        return firstMatch("findByReference", "reference", reference);
    }

    @Programmatic
    public Tax findOrCreate(final String reference, String name) {
        Tax tax = firstMatch("findByReference", "reference", reference);
        if (tax == null) {
            tax = newTax(reference, name);
        }
        return tax;
    }

}
