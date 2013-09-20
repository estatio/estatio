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
package org.estatio.dom.charge;

import java.util.List;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotContributed;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Prototype;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.tax.Tax;
import org.estatio.dom.utils.StringUtils;

public class Charges extends EstatioDomainService<Charge> {

    public Charges() {
        super(Charges.class, Charge.class);
    }
    
    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(name="Other", sequence = "chargeAndChargeGroups.2.1")
    public List<Charge> allCharges() {
        return allInstances();
    }

    @NotContributed
    @ActionSemantics(Of.IDEMPOTENT)
    @MemberOrder(name="Other", sequence = "chargeAndChargeGroups.2.2")
    public List<Charge> newCharge(
            final @Named("Reference") String reference, 
            final @Named("Description") String description, 
            final @Named("Code") String code, 
            final Tax tax, 
            final ChargeGroup chargeGroup) {
        createCharge(reference, description, code, tax, chargeGroup);
        return allCharges();
    }

    // //////////////////////////////////////

    @Programmatic
    public Charge createCharge(
            final String reference, 
            final String description, 
            final String code, 
            final Tax tax, 
            final ChargeGroup chargeGroup) {
        Charge charge = findCharge(reference);
        if (charge == null) {
            charge = newTransientInstance();
            charge.setReference(reference);
            persist(charge);
        }
        charge.setDescription(description);
        charge.setCode(code);
        charge.setTax(tax);
        charge.setGroup(chargeGroup);
        return charge;
    }

    // //////////////////////////////////////
    
    @Programmatic
    public Charge findCharge(final String reference) {
        String regex = StringUtils.wildcardToRegex(reference);
        return firstMatch("findByReference", "reference", regex);
    }

}
