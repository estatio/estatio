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
package org.estatio.dom.budgeting.partioning;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.utils.TitleBuilder;

import org.estatio.dom.UdoDomainObject2;
import org.estatio.dom.apptenancy.WithApplicationTenancyProperty;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationType;
import org.estatio.dom.charge.Charge;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE
       ,schema = "dbo" // Isis' ObjectSpecId inferred from @DomainObject#objectType
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Queries({
        @Query(
                name = "findUnique", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.budgeting.partioning.Partitioning " +
                        "WHERE budget == :budget && type == :type && startDate == :startDate "),
        @Query(
                name = "findByBudgetAndType", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.budgeting.partioning.Partitioning " +
                        "WHERE budget == :budget && type == :type ")
})
@Unique(name = "Partitioning_budget_type_startDate_UNQ", members = {"budget", "type", "startDate"})
@DomainObject(
        objectType = "org.estatio.dom.budgeting.partioning.Partitioning"
)
public class Partitioning extends UdoDomainObject2<Partitioning> implements WithApplicationTenancyProperty {

    public Partitioning() {
        super("budget, type, startDate");
    }

    public String title() {

        return TitleBuilder.start()
                .withParent(getBudget())
                .withName(getType())
                .withName(" ")
                .withName(getStartDate())
                .toString();
    }

    @Column(allowsNull = "false", name = "budgetId")
    @PropertyLayout(hidden = Where.REFERENCES_PARENT)
    @Getter @Setter
    private Budget budget;

    @Column(allowsNull = "false")
    @Getter @Setter
    private LocalDate startDate;

    @Column(allowsNull = "false")
    @Getter @Setter
    private LocalDate endDate;

    @Column(allowsNull = "false")
    @Getter @Setter
    private BudgetCalculationType type;

    @Persistent(mappedBy = "partitioning")
    @Getter @Setter
    private SortedSet<PartitionItem> items = new TreeSet<>();

    @Override
    @PropertyLayout(hidden = Where.EVERYWHERE)
    public ApplicationTenancy getApplicationTenancy() {
        return getBudget().getApplicationTenancy();
    }

    @Programmatic
    public List<Charge> getDistinctInvoiceCharges() {
        List<Charge> results = new ArrayList<>();
        for (PartitionItem item : getItems()){
            if (!results.contains(item.getCharge())){
                results.add(item.getCharge());
            }
        }
        return results;
    }
}