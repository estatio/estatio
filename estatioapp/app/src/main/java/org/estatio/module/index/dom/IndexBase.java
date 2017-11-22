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
package org.estatio.module.index.dom;

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
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.Chained;
import org.incode.module.base.dom.with.WithStartDate;
import org.incode.module.base.dom.utils.MathUtils;
import org.incode.module.base.dom.utils.TitleBuilder;

import org.estatio.module.base.dom.UdoDomainObject2;
import org.estatio.module.base.dom.apptenancy.WithApplicationTenancyCountry;
import org.estatio.module.index.dom.api.IndexValueCreator;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents the periodic rebasing of an {@link Index}, and
 * {@link #getValues() holds} the {@link IndexValue value}s until the
 * {@link #getNext() next} rebasing.
 *
 * @see Index
 */
@PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "dbo"    // Isis' ObjectSpecId inferred from @DomainObject#objectType
)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@Queries({
        @Query(
                name = "findByIndexAndDate", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.index.dom.IndexBase " +
                        "WHERE index == :index " +
                        "&& startDate == :date " +
                        "ORDER BY startDate DESC "),
        @Query(
                name = "findByIndexAndActiveOnDate", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.index.dom.IndexBase " +
                        "WHERE index == :index " +
                        "&& startDate <= :date " +
                        "ORDER BY startDate DESC ")
})
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "org.estatio.dom.index.IndexBase"
)
public class IndexBase
        extends UdoDomainObject2<IndexBase>
        implements WithStartDate, Chained<IndexBase>, WithApplicationTenancyCountry, IndexValueCreator {

    public static final int FACTOR_SCALE = 4;

    public IndexBase() {
        super("index, startDate desc");
    }

    public String title() {
        return TitleBuilder.start()
                .withParent(getIndex())
                .withName(getStartDate())
                .toString();
    }

    @PropertyLayout(
            named = "Application Level",
            describedAs = "Determines those users for whom this object is available to view and/or modify."
    )
    public ApplicationTenancy getApplicationTenancy() {
        return getIndex().getApplicationTenancy();
    }

    @Column(name = "indexId", allowsNull = "false")
    @Getter @Setter
    private Index index;


    @Persistent
    @Column(allowsNull = "false")
    @Getter @Setter
    private LocalDate startDate;

    @Persistent
    @Column(scale = FACTOR_SCALE)
    @Property(optionality = Optionality.OPTIONAL)
    @Getter @Setter
    private BigDecimal factor;

    public String validateFactor(final BigDecimal factor) {
        if (getPrevious() == null) {
            return null;
        }
        return MathUtils.isZeroOrNull(factor)
                ? "Factor is mandatory when there is a previous base"
                : null;
    }

    @Persistent(mappedBy = "next")
    @Property(optionality = Optionality.OPTIONAL)
    @Getter @Setter
    private IndexBase previous;

    @Column(name = "nextIndexBaseId")
    @Property(optionality = Optionality.OPTIONAL)
    @Getter @Setter
    private IndexBase next;

    @Persistent(mappedBy = "indexBase")
    @CollectionLayout(render = RenderType.EAGERLY)
    @Getter @Setter
    private SortedSet<IndexValue> values = new TreeSet<>();

    @Programmatic
    public BigDecimal factorForDate(final LocalDate date) {
        return date.isBefore(getStartDate())
                ? getFactor().multiply(getPrevious() == null ? BigDecimal.ONE : getPrevious().factorForDate(date))
                : BigDecimal.ONE;
    }

    @Override
    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public IndexValue newIndexValue(
            final LocalDate startDate,
            final BigDecimal value) {
        return indexValueRepository.findOrCreate(this, startDate, value);
    }

    public LocalDate default0NewIndexValue() {
        IndexValue last = indexValueRepository.findLastByIndex(getIndex());
        return last == null ? null : last.getStartDate().plusMonths(1);
    }

    @Inject
    IndexValueRepository indexValueRepository;

}
