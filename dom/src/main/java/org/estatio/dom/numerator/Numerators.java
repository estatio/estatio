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
package org.estatio.dom.numerator;

import java.math.BigInteger;
import java.util.List;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.asset.Property;

public class Numerators extends EstatioDomainService<Numerator> {

    public Numerators() {
        super(Numerators.class, Numerator.class);
    }

    // //////////////////////////////////////

    
    // is contributed (for administrators)
    @ActionSemantics(Of.IDEMPOTENT)
    @MemberOrder(name="Other", sequence = "numerators.1")
    public Numerator createNumerator(
            final NumeratorType type, 
            final Property property,
            final @Named("Format") String format,
            final @Named("Last value") BigInteger lastIncrement) {
        
        final Numerator existing = findNumerator(type, property);
        if(existing != null) {
            getContainer().warnUser("Numerator already exists for type/property");
            return existing;
        }

        final Numerator numerator = newTransientInstance();
        numerator.setType(type);
        numerator.setProperty(property);
        numerator.setFormat(format);
        numerator.setLastIncrement(lastIncrement);
        persist(numerator);
        return numerator;
    }
    
    public String default2CreateNumerator() {
        return "XXX-%05d";
    }
    public BigInteger default3CreateNumerator() {
        return BigInteger.ZERO;
    }
    
    public String validateCreateNumerator(
            final NumeratorType type, 
            final Property property,
            final String format,
            final BigInteger lastIncrement) {
        
        try {
            String.format(format, lastIncrement);
        } catch(Exception ex) {
            return "Invalid format string";
        }
        return null;
    }
    
    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(name="Other", sequence = "numerators.2")
    public Numerator findNumerator(
            final NumeratorType type, 
            final Property property) {
        return firstMatch("findByTypeAndProperty", "type", type, "property", property);
    }

    // //////////////////////////////////////

    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(name="Other", sequence = "numerators.3")
    public List<Numerator> allNumerators() {
        return allInstances();
    }

}
