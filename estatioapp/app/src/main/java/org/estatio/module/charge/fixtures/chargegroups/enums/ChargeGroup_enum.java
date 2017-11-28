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
package org.estatio.module.charge.fixtures.chargegroups.enums;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.isisaddons.module.base.platform.fixturesupport.EnumWithUpsert;

import org.estatio.module.charge.dom.ChargeGroup;
import org.estatio.module.charge.dom.ChargeGroupRepository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(chain = true)
public enum ChargeGroup_enum implements EnumWithUpsert<ChargeGroup> {

    Rent                    ("RENT", "Rent"),
    ServiceCharge           ("SERVICE_CHARGE", "Service Charge"),
    TurnoverRent            ("TURNOVER_RENT", "Turnover Rent"),
    Percentage              ("RENTAL_FEE", "Rental Fee"),
    Deposit                 ("DEPOSIT", "Deposit"),
    Discount                ("DISCOUNT", "Discount"),
    EntryFee                ("ENTRY_FEE", "Entry Fee"),
    Tax                     ("TAX", "Tax"),
    ServiceChargeIndexable  ("SERVICE_CHARGE_INDEXABLE", "Service Charge Indexable"),
    Marketing               ("MARKETING", "Marketing"),
    ;

    private final String ref;
    private final String description;

    @Override
    public ChargeGroup findUsing(final ServiceRegistry2 serviceRegistry) {
        final ChargeGroupRepository repository =
                serviceRegistry.lookupService(ChargeGroupRepository.class);
        return repository.findChargeGroup(ref);
    }

    @Override
    public ChargeGroup upsertUsing(final ServiceRegistry2 serviceRegistry) {
        final ChargeGroupRepository repository =
                serviceRegistry.lookupService(ChargeGroupRepository.class);
        return repository.upsert(getRef(), getDescription());
    }

    public FixtureScript toFixtureScript() {
        return new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                final ChargeGroup chargeGroup = upsertUsing(serviceRegistry);
                executionContext.addResult(this, chargeGroup.getReference(), chargeGroup);
            }
        };
    }

}
