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
package org.estatio.dom.budgeting.budgetcalculation;

import lombok.Getter;
import lombok.Setter;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.services.timestamp.Timestampable;
import org.estatio.dom.EstatioDomainObject;
import org.estatio.dom.apptenancy.WithApplicationTenancyProperty;
import org.estatio.dom.budgeting.Distributable;
import org.estatio.dom.budgeting.allocation.BudgetItemAllocation;
import org.estatio.dom.budgeting.keyitem.KeyItem;
import org.estatio.dom.lease.LeaseTermForServiceCharge;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import javax.jdo.annotations.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE
//      ,schema = "budget"
)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Queries({
        @Query(
                name = "findByBudgetItemAllocationAndKeyItemAndCalculationType", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.budgeting.budgetcalculation.BudgetCalculation " +
                        "WHERE budgetItemAllocation == :budgetItemAllocation " +
                        "&& keyItem == :keyItem " +
                        "&& calculationType == :calculationType"),
        @Query(
                name = "findByBudgetItemAllocationAndCalculationType", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.budgeting.budgetcalculation.BudgetCalculation " +
                        "WHERE budgetItemAllocation == :budgetItemAllocation " +
                        "&& calculationType == :calculationType"),
        @Query(
                name = "findByBudgetItemAllocation", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.budgeting.budgetcalculation.BudgetCalculation " +
                        "WHERE budgetItemAllocation == :budgetItemAllocation")
})
@Unique(name = "BudgetCalculation_budgetItemAllocation_keyItem_calculationType_UNQ", members = {"budgetItemAllocation", "keyItem", "calculationType"})
@DomainObject(autoCompleteRepository = BudgetCalculationRepository.class)
public class BudgetCalculation extends EstatioDomainObject<BudgetCalculation> implements Distributable, WithApplicationTenancyProperty, Timestampable {

    public BudgetCalculation() {
        super("budgetItemAllocation, keyItem");
    }

    @Getter @Setter
    @Column(allowsNull = "false", scale = 2)
    private BigDecimal value;

    @Getter @Setter
    @Column(allowsNull = "false", name="budgetItemAllocationId")
    @PropertyLayout(hidden = Where.REFERENCES_PARENT)
    private BudgetItemAllocation budgetItemAllocation;

    @Getter @Setter
    @Column(allowsNull = "false", name="keyItemId")
    @PropertyLayout(hidden = Where.REFERENCES_PARENT)
    private KeyItem keyItem;

    @Getter @Setter
    @Column(allowsNull = "false", scale = 6)
    @PropertyLayout(hidden = Where.EVERYWHERE)
    private BigDecimal sourceValue;

    @Getter @Setter
    @Column(allowsNull = "false")
    private CalculationType calculationType;

    @Getter @Setter
    @CollectionLayout(hidden = Where.EVERYWHERE)
    @Persistent(mappedBy = "budgetCalculation", dependentElement = "true")
    private SortedSet<BudgetCalculationLink> budgetCalculationLinks = new TreeSet<BudgetCalculationLink>();

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @CollectionLayout(render = RenderType.EAGERLY)
    public List<LeaseTermForServiceCharge> getLeaseTerms(){
        List<LeaseTermForServiceCharge> leaseTerms = new ArrayList<>();
        for (BudgetCalculationLink link : budgetCalculationLinks){
            leaseTerms.add(link.getLeaseTerm());
        }
        return leaseTerms;
    }

    @Override
    @PropertyLayout(hidden = Where.EVERYWHERE)
    public ApplicationTenancy getApplicationTenancy() {
        return getBudgetItemAllocation().getApplicationTenancy();
    }

    @Getter @Setter
    @Column(allowsNull = "true")
    @PropertyLayout(hidden = Where.ALL_TABLES)
    private Timestamp updatedAt;

    @Getter @Setter
    @PropertyLayout(hidden = Where.ALL_TABLES)
    @Column(allowsNull = "true")
    private String updatedBy;

    @Programmatic
    public void remove(){
        getContainer().remove(this);
    }

    public String title(){
        return "Budget calculation";
    }

}
