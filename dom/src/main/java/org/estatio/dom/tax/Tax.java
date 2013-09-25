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
package org.estatio.dom.tax;

import java.math.BigDecimal;
import java.util.SortedSet;
import java.util.TreeSet;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Bounded;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;
import org.apache.isis.applib.annotation.Title;

import org.estatio.dom.EstatioRefDataObject;
import org.estatio.dom.WithNameGetter;
import org.estatio.dom.WithReferenceComparable;
import org.estatio.dom.WithReferenceUnique;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Query(
        name = "findByReference", language = "JDOQL", 
        value = "SELECT "
                + "FROM org.estatio.dom.tax.Tax "
                + "WHERE reference.matches(:reference)")
@javax.jdo.annotations.Unique(name = "TAX_REFERENCE_UNIQUE_IDX", members="reference")
@Bounded
@Immutable
public class Tax
        extends EstatioRefDataObject<Tax>
        implements WithReferenceComparable<Tax>, WithNameGetter, WithReferenceUnique {

    public Tax() {
        super("reference");
    }
    
    // //////////////////////////////////////

    private String reference;

    @javax.jdo.annotations.Column(allowsNull="false")
    @Title
    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    // //////////////////////////////////////

    private String name;

    @javax.jdo.annotations.Column(allowsNull="true")
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent(mappedBy = "tax")
    private SortedSet<TaxRate> rates = new TreeSet<TaxRate>();

    @Render(Type.EAGERLY)
    public SortedSet<TaxRate> getRates() {
        return rates;
    }

    public void setRates(final SortedSet<TaxRate> rates) {
        this.rates = rates;
    }

    // //////////////////////////////////////

    public TaxRate newRate(
            final @Named("Start Date") LocalDate startDate, 
            final @Named("Percentage") BigDecimal percentage) {
        return taxRates.newRate(this, startDate, percentage);
    }

    // //////////////////////////////////////

    @Programmatic
    public TaxRate taxRateFor(final LocalDate date) {
        TaxRate rate = taxRates.findTaxRateByTaxAndDate(this, date);
        return rate;
    }

    // //////////////////////////////////////

    @Programmatic
    public BigDecimal percentageFor(final LocalDate date) {
        TaxRate rate = taxRateFor(date);
        if (rate == null) {
            return null;
        }
        return rate.getPercentage();
    }

    // //////////////////////////////////////


    private TaxRates taxRates;

    public final void injectTaxRates(final TaxRates taxRates) {
        this.taxRates = taxRates;
    }

}
