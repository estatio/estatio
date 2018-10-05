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
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

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
import javax.validation.constraints.Digits;

import org.assertj.core.util.Lists;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.types.MoneyType;
import org.incode.module.base.dom.utils.TitleBuilder;
import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.base.dom.UdoDomainObject;
import org.estatio.module.capex.dom.order.Order;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE
        , schema = "dbo"
)
@DatastoreIdentity(strategy = IdGeneratorStrategy.NATIVE, column = "id")
@Version(strategy = VersionStrategy.VERSION_NUMBER, column = "version")
@Unique(name = "ProjectTerm_project_startDate_UNQ", members = { "project", "startDate" })
@Queries({
        @Query(name = "findByProject", language = "JDOQL", value = "SELECT "
                + "FROM org.estatio.module.capex.dom.project.ProjectTerm "
                + "WHERE project == :project "),
        @Query(name = "findByProjectAndStartDate", language = "JDOQL", value = "SELECT "
                + "FROM org.estatio.module.capex.dom.project.ProjectTerm "
                + "WHERE project == :project && "
                + "startDate == :startDate ")
})
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "org.estatio.capex.dom.project.ProjectTerm"
)
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_ROOT)
public class ProjectTerm extends UdoDomainObject<ProjectTerm> {

    public ProjectTerm() {
        super("project, startDate");
    }

    public String title() {
        return TitleBuilder.start()
                .withParent(getProject())
                .withName(getInterval() + " ")
                .withName(getBudgetedAmount())
                .toString();
    }

    @Getter @Setter
    @Column(allowsNull = "false", name = "projectId")
    private Project project;

    @Getter @Setter
    @Column(allowsNull = "false", scale = MoneyType.Meta.SCALE)
    private BigDecimal budgetedAmount;

    @Property()
    public Integer getPercentageOfTotalBudget(){
        if (getProject().getBudgetedAmount()==null || getProject().getBudgetedAmount().compareTo(BigDecimal.ZERO) == 0) return 0;
        BigDecimal fraction = getBudgetedAmount().divide(getProject().getBudgetedAmount(), MathContext.DECIMAL64);
        BigDecimal percentageAsBd = fraction.multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP);
        return percentageAsBd.intValueExact();
    }

    @Getter @Setter
    @Column(allowsNull = "false")
    private LocalDate startDate;

    @Getter @Setter
    @Column(allowsNull = "false")
    private LocalDate endDate;

    @Property()
    @Digits(integer = 13, fraction = 2)
    public BigDecimal getOrderedAmount(){
        // TODO: implement
        return BigDecimal.ZERO;
    }

    @Property()
    @Digits(integer = 13, fraction = 2)
    public BigDecimal getPaidAmount(){
        // TODO: implement
        return BigDecimal.ZERO;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<Order> getOrders(){
        // TODO: implement
        return Lists.emptyList();
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<Order> getInvoices(){
        // TODO: implement
        return Lists.emptyList();
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    public ProjectTerm amendBudgetedAmount(
            @Parameter(optionality = Optionality.OPTIONAL)
            final BigDecimal add,
            @Parameter(optionality = Optionality.OPTIONAL)
            final BigDecimal subtract){
        BigDecimal newAmount = getBudgetedAmount()!=null ? getBudgetedAmount() : BigDecimal.ZERO;
        if (add!=null){
            newAmount = newAmount.add(add);
        }
        if (subtract!=null){
            newAmount = newAmount.subtract(subtract);
        }
        setBudgetedAmount(newAmount);
        return this;
    }

    @PropertyLayout(hidden = Where.EVERYWHERE)
    @Override
    public ApplicationTenancy getApplicationTenancy() {
        return getProject().getApplicationTenancy();
    }

    @PropertyLayout(hidden = Where.EVERYWHERE)
    @Override
    public String getAtPath() {
        final ApplicationTenancy applicationTenancy = getApplicationTenancy();
        return applicationTenancy != null ? applicationTenancy.getPath() : null;
    }

    @Programmatic
    public LocalDateInterval getInterval() {
        return LocalDateInterval.including(getStartDate(), getEndDate());
    }

}
