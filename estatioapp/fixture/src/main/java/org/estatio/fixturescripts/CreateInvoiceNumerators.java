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
package org.estatio.fixturescripts;

import java.util.Arrays;
import java.util.List;

import com.google.inject.Inject;

import org.apache.isis.applib.fixturescripts.DiscoverableFixtureScript;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.asset.FixedAssetRole;
import org.estatio.dom.asset.FixedAssetRoleRepository;
import org.estatio.dom.asset.FixedAssetRoleType;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.lease.leaseinvoicing.NumeratorForCollectionRepository;
import org.estatio.dom.lease.EstatioApplicationTenancyRepositoryForLease;
import org.estatio.numerator.dom.impl.Numerator;

import static org.incode.module.base.integtests.VT.bi;

public class CreateInvoiceNumerators extends DiscoverableFixtureScript {


    @Override
    protected void execute(ExecutionContext fixtureResults) {
        final List<FixedAssetRoleType> roleTypes = Arrays.asList(FixedAssetRoleType.PROPERTY_OWNER, FixedAssetRoleType.TENANTS_ASSOCIATION);
        for (Property property : propertyRepository.allProperties()) {
            for (FixedAssetRole fixedAssetRole : fixedAssetRoleRepository.findAllForProperty(property)){
                if (roleTypes.contains(fixedAssetRole.getType())) {
                    ApplicationTenancy applicationTenancy = estatioApplicationTenancyRepository.findOrCreateTenancyFor(property, fixedAssetRole.getParty());
                    final Numerator numerator = estatioNumeratorRepository.createInvoiceNumberNumerator(property, property.getReference().concat("-%04d"), bi(0), applicationTenancy);
                    fixtureResults.addResult(this, property.getReference(), numerator);
                }
            }
        }
    }
    // //////////////////////////////////////

    @Inject
    NumeratorForCollectionRepository estatioNumeratorRepository;

    @Inject
    PropertyRepository propertyRepository;

    @Inject
    FixedAssetRoleRepository fixedAssetRoleRepository;

    @Inject
    EstatioApplicationTenancyRepositoryForLease estatioApplicationTenancyRepository;
}
