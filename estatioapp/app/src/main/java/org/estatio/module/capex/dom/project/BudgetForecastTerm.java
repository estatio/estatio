/*
 *
 *  Copyright 2012-2015 Eurocommercial Properties NV
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
package org.estatio.module.capex.dom.project;

import java.math.BigDecimal;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.utils.TitleBuilder;
import org.incode.module.base.dom.valuetypes.AbstractInterval;
import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.base.dom.UdoDomainObject2;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE
        , schema = "dbo"
)
@DatastoreIdentity(strategy = IdGeneratorStrategy.NATIVE, column = "id")
@Version(strategy = VersionStrategy.VERSION_NUMBER, column = "version")
@Unique(members = { "forecastItem", "startDate" })
@Queries({
        @Query(name = "findUnique", language = "JDOQL",
                value = "SELECT "
                + "FROM org.estatio.module.capex.dom.project.BudgetForecastTerm "
                + "WHERE forecastItem == :forecastItem && startDate == :startDate ")
})
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "org.estatio.capex.dom.project.BudgetForecastTerm"
)
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_ROOT)
public class BudgetForecastTerm extends UdoDomainObject2<BudgetForecastTerm> {

    // TODO: persist next / previous ? Derive? Of just leave it like this ...

    public BudgetForecastTerm() {
        super("forecastItem, startDate");
    }

    public BudgetForecastTerm(final BudgetForecastItem forecastItem, final LocalDateInterval interval, final BigDecimal amount){
        this();
        this.forecastItem = forecastItem;
        this.startDate = interval.startDate();
        this.endDate = interval.endDate(AbstractInterval.IntervalEnding.INCLUDING_END_DATE);
        this.amount = amount;
    }

    public String title() {
        return TitleBuilder.start().withParent(getForecastItem()).withName(getStartDate()).toString();
    }

    @Column(allowsNull = "false", name = "budgetForecastItemId")
    @PropertyLayout(hidden = Where.REFERENCES_PARENT)
    @Getter @Setter
    private BudgetForecastItem forecastItem;

    @Column(allowsNull = "false")
    @Getter @Setter
    private LocalDate startDate;

    @Column(allowsNull = "false")
    @Getter @Setter
    private LocalDate endDate;

    @Column(allowsNull = "false", scale = 2)
    @Getter @Setter
    private BigDecimal amount;

    @Column(allowsNull = "true", name = "nextTermId")
    @PropertyLayout(hidden = Where.ALL_TABLES)
    @Getter @Setter
    private BudgetForecastTerm next;

    @Column(allowsNull = "true", name = "previousTermId")
    @PropertyLayout(hidden = Where.ALL_TABLES)
    @Persistent(mappedBy = "next")
    @Getter @Setter
    private BudgetForecastTerm previous;

    @Override
    @PropertyLayout(hidden = Where.EVERYWHERE)
    public ApplicationTenancy getApplicationTenancy() {
        return getForecastItem().getApplicationTenancy();
    }

}
