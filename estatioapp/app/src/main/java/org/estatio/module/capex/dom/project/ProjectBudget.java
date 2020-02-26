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
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

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
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.services.wrapper.WrapperFactory;
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntityAdapter;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.incode.module.base.dom.types.NameType;
import org.incode.module.base.dom.types.ReferenceType;
import org.incode.module.base.dom.utils.TitleBuilder;
import org.incode.module.base.dom.with.WithReferenceUnique;
import org.incode.module.docfragment.dom.types.AtPathType;

import org.estatio.module.base.dom.UdoDomainObject;
import org.estatio.module.base.dom.UdoDomainObject2;
import org.estatio.module.base.dom.apptenancy.WithApplicationTenancyGlobalAndCountry;
import org.estatio.module.capex.dom.invoice.contributions.Project_InvoiceItemsNotOnProjectItem;
import org.estatio.module.capex.dom.order.OrderItem;
import org.estatio.module.capex.dom.order.contributions.Project_OrderItemsNotOnProjectItem;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.party.dom.Party;
import org.estatio.module.tax.dom.Tax;

import lombok.Getter;
import lombok.Setter;
import static org.apache.cxf.version.Version.getName;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE
        , schema = "dbo"
)
@DatastoreIdentity(strategy = IdGeneratorStrategy.NATIVE, column = "id")
@Version(strategy = VersionStrategy.VERSION_NUMBER, column = "version")
@Unique(name = "ProjectBudget_project_budgetVersion_UNQ", members = { "project", "budgetVersion" })
@Queries({
        @Query(name = "findByProject", language = "JDOQL", value = "SELECT "
                + "FROM org.estatio.module.capex.dom.project.ProjectBudget "
                + "WHERE project == :project "),
        @Query(name = "findCommittedByProject", language = "JDOQL", value = "SELECT "
                + "FROM org.estatio.module.capex.dom.project.ProjectBudget "
                + "WHERE project == :project && "
                + "committedOn != null"),
        @Query(name = "findUnique", language = "JDOQL",
                value = "SELECT "
                + "FROM org.estatio.module.capex.dom.project.ProjectBudget "
                + "WHERE project == :project && budgetVersion == :budgetVersion ")
})
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "org.estatio.capex.dom.project.ProjectBudget"
)
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_ROOT)
public class ProjectBudget extends UdoDomainObject2<ProjectBudget> {

    public ProjectBudget() {
        super("project, budgetVersion");
    }

    public ProjectBudget(final Project project, final int version) {
        this();
        this.project = project;
        this.budgetVersion = version;
    }

    public String title() {
        return TitleBuilder.start().withParent(getProject()).withName("budget version").withName(getBudgetVersion()).toString();
    }

    @Column(allowsNull = "false", name = "projectId")
    @Getter @Setter
    private Project project;

    @Column(allowsNull = "false")
    @Getter @Setter
    private int budgetVersion;

    @Column(allowsNull = "true")
    @Getter @Setter
    private LocalDate approvedOn;

    @Column(allowsNull = "true")
    @Getter @Setter
    private String approvedBy;

    @Column(allowsNull = "true")
    @Getter @Setter
    private LocalDate committedOn;

    @Column(allowsNull = "true")
    @Getter @Setter
    private String committedBy;

    @Persistent(mappedBy = "projectBudget", dependentElement = "true")
    @Getter @Setter
    private SortedSet<ProjectBudgetItem> items = new TreeSet<>();

    @Action(semantics = SemanticsOf.SAFE)
    public ProjectBudget getPrevious(){
        if (getBudgetVersion()<=1) return null;
        return projectBudgetRepository.findUnique(getProject(), getBudgetVersion()-1);
    }

    @Action(semantics = SemanticsOf.SAFE)
    public ProjectBudget getNext(){
        return projectBudgetRepository.findUnique(getProject(), getBudgetVersion()+1);
    }

    @Programmatic
    public ProjectBudget createItem(final ProjectItem projectItem, final BigDecimal amount){
        projectBudgetItemRepository.findOrCreate(this, projectItem, amount);
        return this;
    }

    @Programmatic
    public ProjectBudget findOrCreateBudgetItem(final ProjectItem item){
        projectBudgetItemRepository.findOrCreate(this, item, null);
        return this;
    }

    @Programmatic
    public BigDecimal getAmountFor(final ProjectBudget budget, final ProjectItem projectItem) {
        final ProjectBudgetItem budgetItem = projectBudgetItemRepository.findUnique(budget, projectItem);
        return budgetItem != null ? budgetItem.getAmount() : null;
    }


    @Override
    public ApplicationTenancy getApplicationTenancy() {
        return getProject().getApplicationTenancy();
    }

    @Inject
    ProjectBudgetRepository projectBudgetRepository;

    @Inject
    ProjectBudgetItemRepository projectBudgetItemRepository;
}
