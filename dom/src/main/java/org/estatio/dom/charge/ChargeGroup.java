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

import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.isis.applib.annotation.Bounded;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;
import org.apache.isis.applib.annotation.Title;

import org.estatio.dom.EstatioRefDataObject;
import org.estatio.dom.WithReferenceComparable;
import org.estatio.dom.WithReferenceUnique;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Queries({
    @javax.jdo.annotations.Query(
            name = "findByReference", language = "JDOQL", 
            value = "SELECT " +
                    "FROM org.estatio.dom.charge.ChargeGroup " +
            "WHERE reference.matches(:reference)")
})
@javax.jdo.annotations.Uniques({
    @javax.jdo.annotations.Unique(name = "CHARGEGROUP_REFERENCE_UNIQUE_IDX", members="reference")
})
@Immutable
@Bounded
public class ChargeGroup extends EstatioRefDataObject<ChargeGroup> implements WithReferenceComparable<ChargeGroup>, WithReferenceUnique {

    public ChargeGroup() {
        super("reference");
    }
    
    // //////////////////////////////////////

    private String reference;

    @javax.jdo.annotations.Column(allowsNull="false")
    @Title(sequence = "1")
    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    // //////////////////////////////////////

    private String description;

    @javax.jdo.annotations.Column(allowsNull="false")
    @Title(sequence = "2", prepend = "-")
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent(mappedBy = "group")
    private SortedSet<Charge> charges = new TreeSet<Charge>();

    @Render(Type.EAGERLY)
    public SortedSet<Charge> getCharges() {
        return charges;
    }

    public void setCharges(final SortedSet<Charge> charges) {
        this.charges = charges;
    }

}
