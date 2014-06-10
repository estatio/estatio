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
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.estatio.dom.EstatioDomainService;

@DomainService(menuOrder = "80", repositoryFor = Tax.class)
public class Taxes extends EstatioDomainService<Tax> {

    public Taxes() {
        super(Taxes.class, Tax.class);
    }

    // //////////////////////////////////////

    
    @ActionSemantics(Of.SAFE)
    @MemberOrder(name="Other", sequence = "taxStuff.taxes.1")
    public List<Tax> allTaxes() {
        return allInstances();
    }


    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(name="Other", sequence = "taxStuff.taxes.2")
    public Tax newTax(
            final @Named("Reference") String reference,
            final @Named("Name") @Optional String name) {
        final Tax tax = newTransientInstance();
        tax.setReference(reference);
        tax.setName(name);
        persist(tax);
        return tax;
    }

    // //////////////////////////////////////

    @Programmatic
    public Tax findTaxByReference(final String reference) {
        return firstMatch("findByReference", "reference", reference);
    }

    @Programmatic
    public Tax findOrCreate(final String reference, String name) {
        Tax tax =  firstMatch("findByReference", "reference", reference);
        if(tax == null) {
            tax = newTax(reference, name);
        }
        return tax;
    }

    
}
