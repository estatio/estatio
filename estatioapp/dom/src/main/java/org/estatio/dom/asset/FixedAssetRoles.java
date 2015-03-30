/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
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
package org.estatio.dom.asset;

import java.util.List;
import org.joda.time.LocalDate;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.NotContributed;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.party.Party;

@DomainService(menuOrder = "10", repositoryFor = FixedAssetRole.class)
@Hidden
public class FixedAssetRoles extends UdoDomainRepositoryAndFactory<FixedAssetRole> {

    public FixedAssetRoles() {
        super(FixedAssetRoles.class, FixedAssetRole.class);
    }

    // //////////////////////////////////////

    @Programmatic
    public List<FixedAssetRole> findByAsset(
            final FixedAsset asset) {
        return allMatches("findByAsset",
                "asset", asset);
    }

    @Programmatic
    public List<FixedAssetRole> findByAssetAndType(
            final FixedAsset asset,
            final FixedAssetRoleType type) {
        return allMatches("findByAssetAndType",
                "asset", asset,
                "type", type);
    }

    @Programmatic
    public FixedAssetRole findRole(
            final FixedAsset asset,
            final FixedAssetRoleType type) {
        return firstMatch("findByAssetAndType",
                "asset", asset,
                "type", type);
    }

    // //////////////////////////////////////

    @NotContributed
    @Action(semantics = SemanticsOf.SAFE)
    public FixedAssetRole findRole(
            final FixedAsset asset,
            final Party party,
            final FixedAssetRoleType type,
            final LocalDate startDate,
            final LocalDate endDate) {
        return firstMatch("findByAssetAndPartyAndType",
                "asset", asset,
                "party", party,
                "type", type);
    }

}
