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

package org.estatio.module.charge.dom;

import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;

@DomainService(nature = NatureOfService.DOMAIN, repositoryFor = ChargeGroup.class)
public class ChargeGroupRepository extends UdoDomainRepositoryAndFactory<ChargeGroup> {

    public ChargeGroupRepository() {
        super(ChargeGroupRepository.class, ChargeGroup.class);
    }

    // //////////////////////////////////////

    @Programmatic
    public ChargeGroup upsert(final String reference, final String description) {
        ChargeGroup chargeGroup = findChargeGroup(reference);
        if(chargeGroup == null) {
            chargeGroup = createChargeGroup(reference, description);
        }
        return chargeGroup;
    }

    @Programmatic
    public ChargeGroup createChargeGroup(final String reference, final String description) {
        final ChargeGroup chargeGroup = newTransientInstance();
        chargeGroup.setReference(reference);
        chargeGroup.setName(description != null ? description : reference);
        persist(chargeGroup);
        return chargeGroup;
    }

    // //////////////////////////////////////

    @Programmatic
    public ChargeGroup findChargeGroup(
            final String reference) {
        return firstMatch("findByReference", "reference", reference);
    }

    // //////////////////////////////////////

    @Programmatic
    public List<ChargeGroup> allChargeGroups() {
        return allInstances();
    }

}
