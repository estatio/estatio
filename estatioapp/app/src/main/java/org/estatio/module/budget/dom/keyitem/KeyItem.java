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
package org.estatio.module.budget.dom.keyitem;

import java.math.BigDecimal;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;

import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.apache.isis.applib.services.user.UserService;
import org.estatio.module.budget.dom.ponderingareacalculation.PonderingAreaCalculationService;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.utils.TitleBuilder;

import org.estatio.module.base.dom.distribution.Distributable;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "dbo" // Isis' ObjectSpecId inferred from @DomainObject#objectType
)
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@javax.jdo.annotations.Discriminator("org.estatio.dom.budgeting.keyitem.KeyItem")
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "org.estatio.dom.budgeting.keyitem.KeyItem"
)
public class KeyItem extends PartitioningTableItem
        implements Distributable {

    public String title() {
        return TitleBuilder
                .start()
                .withParent(getPartitioningTable())
                .withName(getUnit())
                .toString();
    }

    public KeyItem(){
        super("partitioningTable, unit, sourceValue, value");
    }

    @Column(allowsNull = "false", scale = 6)
    @Getter @Setter
    private BigDecimal sourceValue;

    @Column(allowsNull = "false", scale = 6)
    @Getter @Setter
    private BigDecimal value;

    @Override
    @PropertyLayout(hidden = Where.EVERYWHERE)
    public ApplicationTenancy getApplicationTenancy() {
        return getPartitioningTable().getApplicationTenancy();
    }

    @Programmatic
    public void delete() {
        repositoryService.removeAndFlush(this);
    }

//    @Action
//    public KeyItem calculateSourceValueWithDifferentCoefficients(PonderingAreaCalculationService.PonderingAreaCoefficientRules rules) {
//        KeyTable keyTable = (KeyTable) getPartitioningTable();
//        setSourceValue(ponderingAreaCalculationService.calculateTotalPonderingAreaForUnitWithSpecifiedRules(getUnit(), rules));
//        keyTable.distributeSourceValues();
//
//        return this;
//    }
//
//    public boolean hideCalculateSourceValueWithDifferentCoefficients() {
//        KeyTable keyTable = (KeyTable) getPartitioningTable();
//        return !(keyTable.getFoundationValueType().equals(FoundationValueType.AREA) && keyTable.areaIsDividedForUnit(getUnit()));
//    }

    @Inject RepositoryService repositoryService;

    @Inject
    UserService userService;

    @Inject
    PonderingAreaCalculationService ponderingAreaCalculationService;

}
