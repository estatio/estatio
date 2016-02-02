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
package org.estatio.dom.index;

import java.math.BigDecimal;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Uniques;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.EstatioDomainObject;
import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.RegexValidation;
import org.estatio.dom.WithNameUnique;
import org.estatio.dom.WithReferenceComparable;
import org.estatio.dom.apptenancy.WithApplicationTenancyCountry;
import org.estatio.dom.apptenancy.WithApplicationTenancyPathPersisted;
import org.estatio.dom.index.api.IndexBaseCreator;
import org.estatio.dom.index.api.IndexValueCreator;
import org.estatio.dom.lease.indexation.Indexable;

/**
 * Represents an externally-defined index (eg the retail price index) which
 * provides values for a sequence of dates (typically monthly). The values are
 * decimals representing an increase in percentage points, eg 1.05 to mean a 5%
 * increase.
 * 
 * <p>
 * Periodically the index will be rebased, to reset the percentage point back to
 * 1.00. Therefore the index does not hold {@link IndexValue index value}s
 * directly, instead it {@link #getIndexBases() holds} a succession of
 * {@link IndexBase}s. It is the {@link IndexBase}s that
 * {@link IndexBase#getValues() hold} the {@link IndexValue}s. The rebasing
 * {@link IndexBase#getFactor() factor} is held in {@link IndexBase}.
 */
@PersistenceCapable(identityType = IdentityType.DATASTORE)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@Uniques({
        @Unique(
                name = "Index_reference_UNQ", members = {"reference"}),
        @Unique(
                name = "Index_name_UNQ", members = {"name"})
})
@Queries({
        @Query(
                name = "findByReference", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.index.Index "
                        + "WHERE reference == :reference")
})
@DomainObject(editing = Editing.DISABLED, bounded = true)
public class Index
        extends EstatioDomainObject<Index>
        implements WithReferenceComparable<Index>, WithNameUnique, WithApplicationTenancyCountry, WithApplicationTenancyPathPersisted, IndexBaseCreator, IndexValueCreator {

    public Index() {
        super("reference");
    }

    public Index(
            final String reference,
            final String name,
            final ApplicationTenancy applicationTenancy){
        super("reference");
        this.reference = reference;
        this.name = name;
        this.applicationTenancyPath = applicationTenancy.getPath();
    }


    // //////////////////////////////////////

    private String applicationTenancyPath;

    @Column(
            length = ApplicationTenancy.MAX_LENGTH_PATH,
            allowsNull = "false",
            name = "atPath"
    )
    @Property(hidden = Where.EVERYWHERE)
    public String getApplicationTenancyPath() {
        return applicationTenancyPath;
    }

    public void setApplicationTenancyPath(final String applicationTenancyPath) {
        this.applicationTenancyPath = applicationTenancyPath;
    }

    @PropertyLayout(
            named = "Application Level",
            describedAs = "Determines those users for whom this object is available to view and/or modify."
    )
    public ApplicationTenancy getApplicationTenancy() {
        return securityApplicationTenancyRepository.findByPathCached(getApplicationTenancyPath());
    }

    // //////////////////////////////////////

    private String reference;

    @Column(allowsNull = "false", length = JdoColumnLength.REFERENCE)
    @Property(regexPattern = RegexValidation.REFERENCE)
    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    // //////////////////////////////////////

    private String name;

    @Column(allowsNull = "false", length = JdoColumnLength.NAME)
    @Title()
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    // //////////////////////////////////////

    @Persistent(mappedBy = "index")
    private SortedSet<IndexBase> indexBases = new TreeSet<IndexBase>();

    @CollectionLayout(render = RenderType.EAGERLY)
    public SortedSet<IndexBase> getIndexBases() {
        return indexBases;
    }

    public void setIndexBases(final SortedSet<IndexBase> indexBases) {
        this.indexBases = indexBases;
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    @Override
    public IndexValue newIndexValue(
            final LocalDate startDate,
            final BigDecimal value) {
        IndexBase indexBase = indexBaseRepository.findByIndexAndActiveOnDate(this, startDate);
        return indexBase.newIndexValue(startDate, value);
    }

    public LocalDate default0NewIndexValue(){
        final IndexValue last = indexValueRepository.findLastByIndex(this);
        if (last == null){
            return null;
        }
        return last.getStartDate().plusMonths(1);
    }


    @Programmatic
    public BigDecimal getIndexValueForDate(final LocalDate date) {
        if (date != null) {
            IndexValue indexValue = indexValueRepository.findByIndexAndStartDate(this, date);
            return indexValue == null ? null : indexValue.getValue();
        }
        return null;
    }

    @Programmatic
    public BigDecimal getRebaseFactorForDates(final LocalDate baseIndexStartDate, final LocalDate nextIndexStartDate) {
        if (baseIndexStartDate == null || nextIndexStartDate == null) {
            return null;
        }
        IndexValue nextIndexValue = indexValueRepository.findByIndexAndStartDate(this, nextIndexStartDate);
        if (nextIndexValue != null) {
            final BigDecimal rebaseFactor = nextIndexValue.getIndexBase().factorForDate(baseIndexStartDate);
            return rebaseFactor;
        }
        return null;
    }

    @Programmatic
    @Override
    public IndexBase findOrCreateBase(final LocalDate indexBaseStartDate, final BigDecimal indexBaseFactor) {
        return indexBaseRepository.findOrCreate(this, indexBaseStartDate, indexBaseFactor);
    }

    @Programmatic
    @Override
    public IndexBase createBase(final LocalDate indexBaseStartDate, final BigDecimal indexBaseFactor) {
        return indexBaseRepository.newIndexBase(this, indexBaseRepository.findByIndexAndDate(this, indexBaseStartDate), indexBaseStartDate, indexBaseFactor);
    }


    @Programmatic
    public void initialize(final Indexable input) {
        input.setBaseIndexValue(getIndexValueForDate(input.getBaseIndexStartDate()));
        input.setNextIndexValue(getIndexValueForDate(input.getNextIndexStartDate()));
        input.setRebaseFactor(getRebaseFactorForDates(input.getBaseIndexStartDate(), input.getNextIndexStartDate()));
    }

    // //////////////////////////////////////

    @Inject
    public IndexValueRepository indexValueRepository;

    @Inject
    private IndexBaseRepository indexBaseRepository;

}
