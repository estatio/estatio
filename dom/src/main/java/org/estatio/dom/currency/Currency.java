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

import org.apache.isis.applib.annotation.AutoComplete;
import org.apache.isis.applib.annotation.Bounded;
import org.apache.isis.applib.annotation.Immutable;

import org.estatio.dom.EstatioRefDataObject;
import org.estatio.dom.WithDescriptionUnique;
import org.estatio.dom.WithReferenceComparable;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Queries({
    @javax.jdo.annotations.Query(
            name = "findByReference", language = "JDOQL", 
            value = "SELECT " +
            		"FROM org.estatio.dom.currency.Currency " +
            		"WHERE reference.matches(:reference)"),
	@javax.jdo.annotations.Query(
	        name = "findByReferenceOrDescription", language = "JDOQL", 
	        value = "SELECT " +
	                "FROM org.estatio.dom.currency.Currency " +
	        "WHERE reference.matches(:searchArg) " + 
	        "|| description.matches(:searchArg)")
})
@Bounded
@Immutable
@AutoComplete(repository=Currencies.class, action="autoComplete")
public class Currency extends EstatioRefDataObject<Currency> implements WithReferenceComparable<Currency>, WithDescriptionUnique {

    public Currency() {
        super("reference");
    }
    
    // //////////////////////////////////////

    @javax.jdo.annotations.Unique(name = "CURRENCY_REFERENCE_UNIQUE_IDX")
    private String reference;

    @javax.jdo.annotations.Column(allowsNull="false")
    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }
    
    // //////////////////////////////////////

    @javax.jdo.annotations.Unique(name = "CURRENCY_DESCRIPTION_UNIQUE_IDX")
    private String description;

    @javax.jdo.annotations.Column(allowsNull="false")
    public String getDescription() {
        return description;
    }

    public void setDescription(final String despription) {
        this.description = despription;
    }

}
