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
package org.estatio.dom.asset;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.NotContributed;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.party.Party;

@Hidden
public class FixedAssetRoles extends EstatioDomainService<FixedAssetRole> {

    public FixedAssetRoles() {
        super(FixedAssetRoles.class, FixedAssetRole.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.NON_IDEMPOTENT)
    public FixedAssetRole newRole(final FixedAsset asset, final Party party, final FixedAssetRoleType type, LocalDate startDate, LocalDate endDate) {
        final FixedAssetRole role = newTransientInstance();
        role.setParty(party);
        role.setAsset(asset);
        role.setStartDate(startDate);
        role.setEndDate(endDate);
        role.setType(type);
        persist(role);
        return role;
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    public FixedAssetRole findRole(final FixedAsset asset, final Party party, final FixedAssetRoleType type) {
        return firstMatch(newQueryDefault("findByAssetAndPartyAndType", "asset", asset, "party", party, "type", type));
    }

    @ActionSemantics(Of.SAFE)
    public FixedAssetRole findRole(final FixedAsset asset, final Party party, final FixedAssetRoleType type, final LocalDate startDate, final LocalDate endDate) {
        return firstMatch("findByAssetAndPartyAndType", "asset", asset, "party", party, "type", type);
    }
    
    @ActionSemantics(Of.SAFE)
    @NotContributed
    public FixedAssetRole findByAssetAndPartyAndTypeAndStartDate(final FixedAsset asset, final Party party, final FixedAssetRoleType type, final LocalDate startDate) {
        return firstMatch("findByAssetAndPartyAndTypeAndStartDate", "asset", asset, "party", party, "type", type, "startDate", startDate);
    }
    
    @ActionSemantics(Of.SAFE)
    @NotContributed
    public FixedAssetRole findByAssetAndPartyAndTypeAndEndDate(final FixedAsset asset, final Party party, final FixedAssetRoleType type, final LocalDate endDate) {
        return firstMatch("findByAssetAndPartyAndTypeAndEndDate", "asset", asset, "party", party, "type", type, "endDate", endDate);
    }

}
