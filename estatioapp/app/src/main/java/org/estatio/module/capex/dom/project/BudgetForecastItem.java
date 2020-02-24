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
import java.util.SortedSet;
import java.util.TreeSet;

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

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.utils.TitleBuilder;

import org.estatio.module.base.dom.UdoDomainObject2;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE
        , schema = "dbo"
)
@DatastoreIdentity(strategy = IdGeneratorStrategy.NATIVE, column = "id")
@Version(strategy = VersionStrategy.VERSION_NUMBER, column = "version")
@Unique(members = { "forecast", "projectItem" })
@Queries({
        @Query(name = "findUnique", language = "JDOQL",
                value = "SELECT "
                + "FROM org.estatio.module.capex.dom.project.BudgetForecastItem "
                + "WHERE forecast == :forecast && projectItem == :projectItem ")
})
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "org.estatio.capex.dom.project.BudgetForecastItem"
)
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_ROOT)
public class BudgetForecastItem extends UdoDomainObject2<BudgetForecastItem> {

    public BudgetForecastItem() {
        super("forecast, projectItem");
    }

    public BudgetForecastItem(final BudgetForecast forecast, final ProjectItem projectItem, final BigDecimal amount, final BigDecimal budgetedAmountUntilForecastDate, final BigDecimal invoicedAmountUntilForecastDate){
        this();
        this.forecast = forecast;
        this.projectItem = projectItem;
        this.amount = amount;
        this.budgetedAmountUntilForecastDate = budgetedAmountUntilForecastDate;
        this.invoicedAmountUntilForecastDate = invoicedAmountUntilForecastDate;
    }

    public String title() {
        return TitleBuilder.start().withParent(getForecast()).withName(getProjectItem()).toString();
    }

    @Column(allowsNull = "false", name = "budgetForecastId")
    @Getter @Setter
    private BudgetForecast forecast;

    @Column(allowsNull = "false", name = "projectItemId")
    @Getter @Setter
    private ProjectItem projectItem;

    @Column(allowsNull = "false", scale = 2)
    @Getter @Setter
    private BigDecimal amount;

    @Column(allowsNull = "false", scale = 2)
    @Getter @Setter
    private BigDecimal budgetedAmountUntilForecastDate;

    @Column(allowsNull = "false", scale = 2)
    @Getter @Setter
    private BigDecimal invoicedAmountUntilForecastDate;

    @Persistent(mappedBy = "forecastItem", dependentElement = "true")
    @Getter @Setter
    private SortedSet<BudgetForecastItemTerm> terms = new TreeSet<BudgetForecastItemTerm>();

    @Override
    public ApplicationTenancy getApplicationTenancy() {
        return getForecast().getApplicationTenancy();
    }

}
