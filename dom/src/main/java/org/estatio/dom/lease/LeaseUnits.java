/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
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
import org.apache.isis.applib.annotation.NotContributed;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.asset.Unit;

@Hidden
public class LeaseUnits extends EstatioDomainService<LeaseUnit> {

    public LeaseUnits() {
        super(LeaseUnits.class, LeaseUnit.class);
    }

    // //////////////////////////////////////

    // @Hidden
    @ActionSemantics(Of.NON_IDEMPOTENT)
    @NotContributed
    public LeaseUnit newLeaseUnit(Lease lease, UnitForLease unit) {
        LeaseUnit lu = newTransientInstance(LeaseUnit.class);
        persist(lu);
        lu.modifyLease(lease);
        lu.modifyUnit(unit);
        return lu;
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @Hidden
    public LeaseUnit findByLeaseAndUnitAndStartDate(final Lease lease, final Unit unit, LocalDate startDate) {
         return firstMatch("findByLeaseAndUnitAndStartDate", "lease", lease, "unit", unit, "startDate", startDate);
    }

}
