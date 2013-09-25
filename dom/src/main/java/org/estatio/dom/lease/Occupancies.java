/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
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
package org.estatio.dom.lease;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.NotContributed;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.asset.Unit;

@Hidden
public class Occupancies extends EstatioDomainService<Occupancy> {

    public Occupancies() {
        super(Occupancies.class, Occupancy.class);
    }

    // //////////////////////////////////////

    // @Hidden
    @ActionSemantics(Of.NON_IDEMPOTENT)
    @NotContributed
    public Occupancy newOccupancy(
            final Lease lease, 
            final UnitForLease unit) {
        Occupancy lu = newTransientInstance(Occupancy.class);
        lu.setLease(lease);
        lu.setUnit(unit);
        persistIfNotAlready(lu);
        return lu;
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @Hidden
    public Occupancy findByLeaseAndUnitAndStartDate(
            final Lease lease, 
            final Unit unit, 
            final LocalDate startDate) {
         return firstMatch("findByLeaseAndUnitAndStartDate", "lease", lease, "unit", unit, "startDate", startDate);
    }

}
