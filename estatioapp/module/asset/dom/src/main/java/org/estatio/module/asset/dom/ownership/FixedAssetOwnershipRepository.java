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

package org.estatio.module.asset.dom.ownership;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.module.asset.dom.FixedAsset;
import org.estatio.module.asset.dom.OwnershipType;
import org.estatio.module.party.dom.Party;

@DomainService(nature = NatureOfService.DOMAIN)
public class FixedAssetOwnershipRepository extends UdoDomainRepositoryAndFactory<FixedAssetOwnership> {

    public FixedAssetOwnershipRepository() {
        super(FixedAssetOwnershipRepository.class, FixedAssetOwnership.class);
    }

    public FixedAssetOwnership newOwnership(
            final Party newOwner,
            final OwnershipType type,
            final FixedAsset fixedAsset) {
        FixedAssetOwnership fixedAssetOwnership = newTransientInstance(FixedAssetOwnership.class);
        fixedAssetOwnership.setOwner(newOwner);
        fixedAssetOwnership.setOwnershipType(type);
        persistIfNotAlready(fixedAssetOwnership);
        return fixedAssetOwnership;
    }
}
