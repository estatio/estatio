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

package org.estatio.module.lease.contributions;

import java.math.BigDecimal;
import java.util.Objects;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.lease.dom.occupancy.OccupancyRepository;

@Mixin()
public class Property_salesAreaNonFoodTotal {

    final private Property property;

    public Property_salesAreaNonFoodTotal(Property property) {
        this.property = property;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public BigDecimal salesAreaNonFoodTotal() {
        return occupancyRepository.findByProperty(property).stream()
                .filter(o->o.getCurrentSalesAreaLicense()!=null)
                .map(o->o.getCurrentSalesAreaLicense())
                .filter(Objects::nonNull)
                .map(sal->sal.getSalesAreaNonFood())
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Inject OccupancyRepository occupancyRepository;

}
