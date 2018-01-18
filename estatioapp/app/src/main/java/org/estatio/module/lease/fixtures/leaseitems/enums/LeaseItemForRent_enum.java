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

import org.estatio.module.index.fixtures.enums.Index_enum;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemRepository;
import org.estatio.module.lease.dom.LeaseTermFrequency;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.fixtures.leaseitems.builders.LeaseItemForRentBuilder;
import org.estatio.module.lease.fixtures.leaseitems.builders.LeaseTermForIndexableBuilder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import static org.incode.module.base.integtests.VT.bd;
import static org.incode.module.base.integtests.VT.bi;
import static org.incode.module.base.integtests.VT.ld;

@AllArgsConstructor()
@Getter
@Accessors(chain = true)
public enum LeaseItemForRent_enum implements PersonaWithFinder<LeaseItem>, PersonaWithBuilderScript<LeaseItem, LeaseItemForRentBuilder> {

    KalPoison001Nl(Lease_enum.KalPoison001Nl, bi(1),
        new TermSpec[]{
            new TermSpec(null, null, null, bd(150000),
                         null, null, null, Index_enum.IStatFoi)
        }),
    OxfMediaX002Gb(Lease_enum.OxfMediaX002Gb, bi(1),
        new TermSpec[]{
            new TermSpec(null, null, null, bd(20000),
                         ld(2008, 1, 1), ld(2009, 1, 1), ld(2009, 4, 1), Index_enum.IStatFoi)
        }),
    OxfTopModel001Gb(Lease_enum.OxfTopModel001Gb, bi(1),
        new TermSpec[]{
            new TermSpec(null, null, null, bd(20000),
                         ld(2010, 7, 1), ld(2011, 1, 1), ld(2011, 4, 1), Index_enum.IStatFoi)
        }),
    OxfMiracl005Gb(Lease_enum.OxfMiracl005Gb, bi(1),
        new TermSpec[]{
            new TermSpec(Lease_enum.OxfMiracl005Gb.getStartDate(), null, null, bd(150000),
                         null, null, null, Index_enum.IStatFoi),
            new TermSpec(ld(2015, 1, 1), null, null, null,
                         ld(2013, 11, 1), ld(2014, 12, 1), null, Index_enum.IStatFoi),
        }),
    OxfPoison003Gb(Lease_enum.OxfPoison003Gb, bi(1),
        new TermSpec[]{
            new TermSpec(Lease_enum.OxfPoison003Gb.getStartDate(), null, null, bd(87300),
                        null, null, null, Index_enum.IStatFoi),
            new TermSpec(Lease_enum.OxfPoison003Gb.getStartDate().plusYears(1), null, null, bd(87300),
                        ld(2011, 1, 1), ld(2012, 1, 1), ld(2012, 4, 1), Index_enum.IStatFoi),
        }),
    OxfFix006Gb(Lease_enum.OxfFix006Gb, bi(1),
            new TermSpec[]{
                    new TermSpec(Lease_enum.OxfFix006Gb.getStartDate(), null, null, bd(87300),
                            null, null, null, Index_enum.IStatFoi),
                    new TermSpec(Lease_enum.OxfFix006Gb.getStartDate().plusYears(1), null, null, bd(87300),
                            ld(2011, 1, 1), ld(2012, 1, 1), ld(2012, 4, 1), Index_enum.IStatFoi),
            })
    ;

    private final Lease_enum lease_d;
    private final BigInteger sequence;

    private final TermSpec[] termSpecs;

    @AllArgsConstructor
    @Data
    static class TermSpec {
        LocalDate startDate;
        LocalDate endDate;
        LeaseTermFrequency leaseTermFrequency;
        BigDecimal baseValue;
        LocalDate baseIndexStartDate;
        LocalDate nextIndexStartDate;
        LocalDate effectiveDate;
        Index_enum index_d;
    }

    @Override
    public LeaseItemForRentBuilder builder() {
        return new LeaseItemForRentBuilder()
                .setPrereq((f,ec) -> f.setLease(f.objectFor(lease_d, ec)))
                .setPrereq((f,ec) -> f.setTermSpecs(
                        Arrays.stream(termSpecs)
                                .map(x -> new LeaseTermForIndexableBuilder.TermSpec(
                                        x.startDate, x.endDate, x.leaseTermFrequency, x.baseValue,
                                        x.baseIndexStartDate, x.nextIndexStartDate, x.effectiveDate,
                                        f.objectFor(x.index_d, ec)))
                                .collect(Collectors.toList())))
                ;
    }

    @Override
    public LeaseItem findUsing(final ServiceRegistry2 serviceRegistry) {
        final Lease lease = lease_d.findUsing(serviceRegistry);
        final LocalDate startDate = lease.getStartDate();
        final LeaseItemRepository leaseItemRepository = serviceRegistry.lookupService(LeaseItemRepository.class);
        return leaseItemRepository.findLeaseItem(
                lease, LeaseItemForRentBuilder.LEASE_ITEM_TYPE, startDate, sequence);
    }
}
