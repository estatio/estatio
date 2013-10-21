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
package org.estatio.dom.currency;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;

import org.apache.isis.applib.annotation.AutoComplete;
import org.apache.isis.applib.annotation.Bounded;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.RegEx;

import org.estatio.dom.EstatioRefDataObject;
import org.estatio.dom.WithNameUnique;
import org.estatio.dom.WithReferenceComparable;
import org.estatio.dom.WithReferenceUnique;

/**
 * Represents the {@link Currency} of some amount of money.
 * 
 * <p>
 * Used for taxes, indices, invoices, charges.
 */
@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Uniques({
        @javax.jdo.annotations.Unique(
                name = "Currency_reference_UNQ", members = "reference"),
        @javax.jdo.annotations.Unique(
                name = "Currency_name_UNQ", members = "name")
})
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByReference", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.currency.Currency "
                        + "WHERE reference.matches(:reference)"),
        @javax.jdo.annotations.Query(
                name = "findByReferenceOrDescription", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.currency.Currency "
                        + "WHERE reference.matches(:searchArg) "
                        + "|| description.matches(:searchArg)")
})
@Bounded
@Immutable
@AutoComplete(repository = Currencies.class, action = "autoComplete")
public class Currency
        extends EstatioRefDataObject<Currency>
        implements WithReferenceComparable<Currency>, WithReferenceUnique, WithNameUnique {

    public Currency() {
        super("reference");
    }

    // //////////////////////////////////////

    private String reference;

    @javax.jdo.annotations.Column(allowsNull = "false")
    @RegEx(validation = "[A-Z]+", caseSensitive = true)
    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    // //////////////////////////////////////

    private String name;

    @javax.jdo.annotations.Column(allowsNull = "false")
    @Override
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

}
