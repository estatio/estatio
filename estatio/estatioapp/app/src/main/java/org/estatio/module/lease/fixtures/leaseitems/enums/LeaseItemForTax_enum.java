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
package org.estatio.module.lease.fixtures.leaseitems.enums;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.PersonaWithBuilderScript;
import org.apache.isis.applib.fixturescripts.PersonaWithFinder;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemRepository;
import org.estatio.module.lease.dom.LeaseTermFrequency;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.fixtures.leaseitems.builders.LeaseItemForTaxBuilder;
import org.estatio.module.lease.fixtures.leaseitems.builders.LeaseTermForTaxBuilder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import static org.incode.module.base.integtests.VT.bd;
import static org.incode.module.base.integtests.VT.bi;

@AllArgsConstructor()
@Getter
@Accessors(chain = true)
public enum LeaseItemForTax_enum implements PersonaWithFinder<LeaseItem>, PersonaWithBuilderScript<LeaseItem, LeaseItemForTaxBuilder> {

    OxfTopModel001Gb(Lease_enum.OxfTopModel001Gb, bi(1), LeaseItemForRent_enum.OxfTopModel001Gb,
        new TermSpec[]{
            new TermSpec(Lease_enum.OxfTopModel001Gb.getStartDate(), null, null, bd(1), bd(50), true)
        }),
    RonTopModel001It(Lease_enum.RonTopModel001It, bi(1), LeaseItemForRent_enum.RonTopModel001It,
            new TermSpec[]{
                    new TermSpec(Lease_enum.RonTopModel001It.getStartDate(), null, null, bd(1), bd(50), true)
            }),
    ;

    private final Lease_enum lease_d;
    private final BigInteger sequence;
    private final LeaseItemForRent_enum sourceItem_d;

    private final TermSpec[] termSpecs;

    @AllArgsConstructor
    @Data
    static class TermSpec {
        LocalDate startDate;
        LocalDate endDate;
        LeaseTermFrequency leaseTermFrequency;
        BigDecimal taxPercentage;
        BigDecimal recoverablePercentage;
        Boolean taxable;
    }

    @Override
    public LeaseItemForTaxBuilder builder() {
        return new LeaseItemForTaxBuilder()
                .setPrereq((f,ec) -> f.setLease(f.objectFor(lease_d, ec)))
                .setSequence(sequence)
                .setPrereq((f,ec) -> f.setSourceItem(f.objectFor(sourceItem_d, ec)))
                .setPrereq((f,ec) -> f.setTermSpecs(
                        Arrays.stream(termSpecs)
                                .map(x -> new LeaseTermForTaxBuilder.TermSpec(
                                        x.startDate, x.endDate, x.leaseTermFrequency,
                                        x.taxPercentage, x.recoverablePercentage, x.taxable))
                                .collect(Collectors.toList())))
                ;
    }

    @Override
    public LeaseItem findUsing(final ServiceRegistry2 serviceRegistry) {
        final Lease lease = lease_d.findUsing(serviceRegistry);
        final LocalDate startDate = lease.getStartDate();
        final LeaseItemRepository leaseItemRepository = serviceRegistry.lookupService(LeaseItemRepository.class);
        return leaseItemRepository.findLeaseItem(
                lease, LeaseItemForTaxBuilder.LEASE_ITEM_TYPE, startDate, sequence);
    }
}
