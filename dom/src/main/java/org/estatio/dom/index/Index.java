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
package org.estatio.dom.index;

import java.math.BigDecimal;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Title;

import org.estatio.dom.EstatioRefDataObject;
import org.estatio.dom.WithNameUnique;
import org.estatio.dom.WithReferenceComparable;

/**
 * Represents an externally-defined index (eg the retail price index) which provides values for a sequence of dates
 * (typically monthly).  The values are decimals representing an increase in percentage points, eg 1.05 to mean a
 * 5% increase.
 * 
 * <p>
 * Periodically the index will be rebased, to reset the percentage point back to 1.00.  Therefore the index does not
 * hold {@link IndexValue index value}s directly, instead it {@link #getIndexBases() holds} a succession of
 * {@link IndexBase}s.  It is the {@link IndexBase}s that {@link IndexBase#getValues() hold} the {@link IndexValue}s.
 * The rebasing {@link IndexBase#getFactor() factor} is held in {@link IndexBase}.
 */
@javax.jdo.annotations.PersistenceCapable(identityType=IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy=IdGeneratorStrategy.NATIVE, 
        column="id")
@javax.jdo.annotations.Discriminator(
        strategy = DiscriminatorStrategy.CLASS_NAME, 
        column="discriminator")
@javax.jdo.annotations.Uniques({
        @javax.jdo.annotations.Unique(
                name = "Index_reference_UNQ", members = "reference"),
        @javax.jdo.annotations.Unique(
                name = "Index_name_UNQ", members = "name")
})
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByReference", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.index.Index "
                        + "WHERE reference == :reference")
})
@Immutable
public class Index 
        extends EstatioRefDataObject<Index> 
        implements WithReferenceComparable<Index>, WithNameUnique {

    public Index() {
        super("reference");
    }
    
    // //////////////////////////////////////

    private String reference;

    @javax.jdo.annotations.Column(allowsNull="false")
    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    // //////////////////////////////////////

    private String name;

    @javax.jdo.annotations.Column(allowsNull="false")
    @Title
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent(mappedBy = "index")
    private SortedSet<IndexBase> indexBases = new TreeSet<IndexBase>();

    public SortedSet<IndexBase> getIndexBases() {
        return indexBases;
    }

    public void setIndexBases(final SortedSet<IndexBase> indexBases) {
        this.indexBases = indexBases;
    }

    // //////////////////////////////////////

    @Programmatic
    public BigDecimal getIndexValueForDate(final LocalDate date) {
        if (date != null) {
            IndexValue indexValue = indexValues.findIndexValueByIndexAndStartDate(this, date);
            return indexValue == null ? null : indexValue.getValue();
        }
        return null;
    }

    @Programmatic
    public BigDecimal getRebaseFactorForDates(final LocalDate baseIndexStartDate, final LocalDate nextIndexStartDate) {
        if (baseIndexStartDate == null || nextIndexStartDate == null) {
            return null;
        }
        IndexValue nextIndexValue = indexValues.findIndexValueByIndexAndStartDate(this, nextIndexStartDate);
        // TODO: check efficiency.. seems to retrieve every single index value
        // for the last 15 years...
        if (nextIndexValue != null) {
            final BigDecimal rebaseFactor = nextIndexValue.getIndexBase().factorForDate(baseIndexStartDate);
            return rebaseFactor;
        }
        return null;
    }

    @Programmatic
    public void initialize(
            final IndexationCalculator indexationCalculator, 
            final LocalDate baseIndexStartDate, 
            final LocalDate nextIndexStartDate) {
        indexationCalculator.setBaseIndexValue(getIndexValueForDate(baseIndexStartDate));
        indexationCalculator.setNextIndexValue(getIndexValueForDate(nextIndexStartDate));
        indexationCalculator.setRebaseFactor(getRebaseFactorForDates(baseIndexStartDate, nextIndexStartDate));
    }

    // //////////////////////////////////////


    private IndexValues indexValues;

    public final void injectIndexValues(final IndexValues indexValues) {
        this.indexValues = indexValues;
    }

}
