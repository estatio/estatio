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
package org.estatio.module.asset.dom.role;

import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.module.asset.dom.FixedAsset;
import org.estatio.module.asset.dom.Property;
import org.estatio.module.party.dom.Party;

@DomainService(nature = NatureOfService.DOMAIN, repositoryFor = FixedAssetRole.class)
public class FixedAssetRoleRepository extends UdoDomainRepositoryAndFactory<FixedAssetRole> {

    public FixedAssetRoleRepository() {
        super(FixedAssetRoleRepository.class, FixedAssetRole.class);
    }

    public List<FixedAssetRole> findByAsset(
            final FixedAsset asset) {
        return allMatches("findByAsset",
                "asset", asset);
    }

    public List<FixedAssetRole> findByType(
            final FixedAssetRoleTypeEnum type) {
        return allMatches("findByType",
                "type", type);
    }

    public List<FixedAssetRole> findByAssetAndType(
            final FixedAsset asset,
            final FixedAssetRoleTypeEnum type) {
        return allMatches("findByAssetAndType",
                "asset", asset,
                "type", type);
    }

    public FixedAssetRole findRole(
            final FixedAsset asset,
            final FixedAssetRoleTypeEnum type) {
        return firstMatch("findByAssetAndType",
                "asset", asset,
                "type", type);
    }

    public FixedAssetRole findRole(
            final FixedAsset asset,
            final Party party,
            final FixedAssetRoleTypeEnum type,
            final LocalDate startDate,
            final LocalDate endDate) {
        return firstMatch("findByAssetAndPartyAndType",
                "asset", asset,
                "party", party,
                "type", type);
    }

    public List<FixedAssetRole> findAllForProperty(
            final Property property) {
        return allMatches("findAllForProperty",
                "asset", property);
    }

    public List<FixedAssetRole> findAllForPropertyAndPartyAndType(
            final FixedAsset asset,
            final Party party,
            final FixedAssetRoleTypeEnum type) {
        return allMatches("findByAssetAndPartyAndType",
                "asset", asset,
                "party", party,
                "type", type);
    }

    public List<FixedAssetRole> findByPartyAndType(
            final Party party,
            final FixedAssetRoleTypeEnum type) {
        return allMatches("findByPartyAndType",
                "party", party,
                "type", type);
    }

    public List<FixedAssetRole> findByParty(final Party party) {
        return allMatches("findByParty",
                "party", party);
    }
}
