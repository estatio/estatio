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
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.SemanticsOf;

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
@Unique(name = "BudgetForecast_project_date_UNQ", members = { "project", "date" })
@Queries({
        @Query(name = "findByProject", language = "JDOQL", value = "SELECT "
                + "FROM org.estatio.module.capex.dom.project.BudgetForecast "
                + "WHERE project == :project "),
        @Query(name = "findUnique", language = "JDOQL",
                value = "SELECT "
                + "FROM org.estatio.module.capex.dom.project.BudgetForecast "
                + "WHERE project == :project && date == :date ")
})
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "org.estatio.capex.dom.project.BudgetForecast"
)
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_ROOT)
public class BudgetForecast extends UdoDomainObject2<BudgetForecast> {

    public BudgetForecast() {
        super("project, date");
        this.frequency = ForecastFrequency.QUARTERLY; // Currently the only implementation
    }

    public BudgetForecast(final Project project, final LocalDate date) {
        this();
        this.project = project;
        this.date = date;
        this.frequency = ForecastFrequency.QUARTERLY; // Currently the only implementation
    }

    public String title() {
        return TitleBuilder.start().withParent(getProject()).withName(getDate()).toString();
    }

    @Column(allowsNull = "false", name = "projectId")
    @Getter @Setter
    private Project project;

    @Column(allowsNull = "false")
    @Getter @Setter
    private LocalDate date;

    @Column(allowsNull = "false", length = ForecastFrequency.Meta.MAX_LEN)
    @Getter @Setter
    private ForecastFrequency frequency;

    @Persistent(mappedBy = "forecast", dependentElement = "true")
    @Getter @Setter
    private SortedSet<BudgetForecastItem> items = new TreeSet<>();

    @Column(allowsNull = "true", name = "nextId")
    @Getter @Setter
    private BudgetForecast next;

    @Column(allowsNull = "true", name = "previousId")
    @Getter @Setter
    private BudgetForecast previous;

    @Column(allowsNull = "true")
    @Getter @Setter
    private LocalDate createdOn;

    @Column(allowsNull = "true")
    @Getter @Setter
    private String createdBy;

    @Column(allowsNull = "true")
    @Getter @Setter
    private LocalDate approvedOn;

    @Column(allowsNull = "true")
    @Getter @Setter
    private String approvedBy;

    @Override
    public ApplicationTenancy getApplicationTenancy() {
        return getProject().getApplicationTenancy();
    }

}
