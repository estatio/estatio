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

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;

import org.estatio.dom.EstatioRefDataObject;
import org.estatio.dom.WithCodeUnique;
import org.estatio.dom.WithDescriptionGetter;
import org.estatio.dom.WithReferenceUnique;
import org.estatio.dom.tax.Tax;

import org.apache.isis.applib.annotation.Bounded;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.Title;

@javax.jdo.annotations.PersistenceCapable(identityType=IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Uniques({
    @javax.jdo.annotations.Unique(
            name = "Charge_code_UNQ", members={"code"}),
    @javax.jdo.annotations.Unique(
            name = "Charge_reference_UNQ", members={"reference"})
})
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByReference", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.charge.Charge "
                        + "WHERE reference.matches(:reference)")
})
@Bounded
@Immutable
public class Charge 
        extends EstatioRefDataObject<Charge> 
        implements WithReferenceUnique, WithCodeUnique, WithDescriptionGetter {

    public Charge() {
        super("code");
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

    private String code;

    @javax.jdo.annotations.Column(allowsNull="false")
    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    // //////////////////////////////////////

    private Tax tax;

    @javax.jdo.annotations.Column(name = "taxId", allowsNull="false")
    public Tax getTax() {
        return tax;
    }

    public void setTax(final Tax tax) {
        this.tax = tax;
    }

    // //////////////////////////////////////

    private String description;

    @javax.jdo.annotations.Column(allowsNull="false")
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    // //////////////////////////////////////

    private ChargeGroup group;

    @javax.jdo.annotations.Column(name = "groupId", allowsNull="false")
    public ChargeGroup getGroup() {
        return group;
    }

    public void setGroup(final ChargeGroup group) {
        this.group = group;
    }

}
