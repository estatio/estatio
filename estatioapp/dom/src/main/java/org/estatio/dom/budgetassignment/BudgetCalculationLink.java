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
package org.estatio.dom.budgetassignment;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.UdoDomainObject2;
import org.estatio.dom.apptenancy.WithApplicationTenancyProperty;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculation;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "EstatioBudgetassignment" // Isis' ObjectSpecId inferred from @DomainObject#objectType
)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Queries({
        @Query(
                name = "findByBudgetCalculation", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.budgetassignment.BudgetCalculationLink " +
                        "WHERE budgetCalculation == :budgetCalculation"),
        @Query(
                name = "findByServiceChargeItem", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.budgetassignment.BudgetCalculationLink " +
                        "WHERE serviceChargeItem == :serviceChargeItem"),
        @Query(
                name = "findByBudgetCalculationAndServiceChargeItem", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.budgetassignment.BudgetCalculationLink " +
                        "WHERE serviceChargeItem == :serviceChargeItem " +
                        "&& budgetCalculation == :budgetCalculation")
})
@DomainObject(
        objectType = "org.estatio.dom.budgetassignment.BudgetCalculationLink"   // TODO: externalize mapping
)
public class BudgetCalculationLink extends UdoDomainObject2<BudgetCalculationLink> implements WithApplicationTenancyProperty {

    public BudgetCalculationLink() {
        super("budgetCalculation, serviceChargeItem");
    }

    public String title(){
        return "";
    }

    @Getter @Setter
    @Column(allowsNull = "false", name="budgetCalculationId")
    @PropertyLayout(hidden = Where.REFERENCES_PARENT)
    private BudgetCalculation budgetCalculation;

    @Getter @Setter
    @Column(allowsNull = "false", name="serviceChargeTermId")
    @PropertyLayout(hidden = Where.REFERENCES_PARENT)
    private ServiceChargeItem serviceChargeItem;

    @Override
    @PropertyLayout(hidden = Where.EVERYWHERE)
    public ApplicationTenancy getApplicationTenancy() {
        return getBudgetCalculation().getApplicationTenancy();
    }

    @Programmatic
    public void remove(){
        getContainer().remove(this);
    }

}

