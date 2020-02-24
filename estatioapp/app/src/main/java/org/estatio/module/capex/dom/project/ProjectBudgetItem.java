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
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntityAdapter;

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
@Queries({
        @Query(name = "findUnique", language = "JDOQL",
                value = "SELECT "
                + "FROM org.estatio.module.capex.dom.project.ProjectBudgetItem "
                + "WHERE projectBudget == :projectBudget && projectItem == :projectItem ")
})
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "org.estatio.capex.dom.project.ProjectBudgetItem"
)
public class ProjectBudgetItem extends UdoDomainObject2<ProjectBudgetItem> {

    public ProjectBudgetItem() {
        super("projectBudget, projectItem");
    }

    public ProjectBudgetItem(final ProjectBudget budget, final ProjectItem projectItem, final BigDecimal amount) {
        this();
        this.projectBudget = budget;
        this.projectItem = projectItem;
        this.amount = amount;
    }

    public String title() {
        return TitleBuilder.start().withParent(getProjectBudget()).withName(getProjectItem()).toString();
    }

    @Column(allowsNull = "false", name = "projectBudgetId")
    @Getter @Setter
    private ProjectBudget projectBudget;

    @Column(allowsNull = "false", name = "projectItemId")
    @Getter @Setter
    private ProjectItem projectItem;

    @Column(allowsNull = "true")
    @Getter @Setter
    private BigDecimal amount;

    @Override
    public ApplicationTenancy getApplicationTenancy() {
        return getProjectBudget().getApplicationTenancy();
    }
}
