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
package org.estatio.dom.lease;

import java.util.List;
import java.util.UUID;
import javax.inject.Inject;
import com.google.common.eventbus.Subscribe;
import org.joda.time.LocalDate;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NotContributed;
import org.apache.isis.applib.annotation.NotInServiceMenu;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.scratchpad.Scratchpad;
import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.lease.tags.Brand;
import org.estatio.dom.valuetypes.LocalDateInterval;

@DomainService(menuOrder = "40", repositoryFor = Occupancy.class)
@Hidden
public class Occupancies extends UdoDomainRepositoryAndFactory<Occupancy> {

    public Occupancies() {
        super(Occupancies.class, Occupancy.class);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @NotContributed
    @MemberOrder(name = "Occupancies", sequence = "10")
    public Occupancy newOccupancy(
            final Lease lease,
            final Unit unit,
            final LocalDate startDate) {
        Occupancy occupancy = newTransientInstance(Occupancy.class);
        occupancy.setLease(lease);
        occupancy.setUnit(unit);
        occupancy.setStartDate(startDate);
        persistIfNotAlready(occupancy);
        return occupancy;
    }

    // //////////////////////////////////////

    @NotInServiceMenu
    @CollectionLayout(render = RenderType.EAGERLY)
    @MemberOrder(name = "Occupancies", sequence = "10")
    public List<Occupancy> occupancies(Unit unit) {
        return allMatches("findByUnit", "unit", unit);
    }

    // //////////////////////////////////////

    @NotInServiceMenu
    @CollectionLayout(render = RenderType.EAGERLY)
    @NotContributed
    @MemberOrder(name = "Occupancies", sequence = "10")
    public List<Occupancy> occupancies(Lease lease) {
        return allMatches("findByLease", "lease", lease);
    }

    // //////////////////////////////////////

    @Programmatic
    public Occupancy findByLeaseAndUnitAndStartDate(
            final Lease lease,
            final Unit unit,
            final LocalDate startDate) {
        return firstMatch(
                "findByLeaseAndUnitAndStartDate",
                "lease", lease,
                "unit", unit,
                "startDate", startDate);
    }

    @Programmatic
    public List<Occupancy> findByLeaseAndDate(
            final Lease lease,
            final LocalDate date) {
        return allMatches(
                "findByLeaseAndDate",
                "lease", lease,
                "date", date,
                "dateAsEndDate", LocalDateInterval.endDateFromStartDate(date));
    }

    @Action(semantics = SemanticsOf.SAFE)
    public List<Occupancy> findByBrand(
            final Brand brand,
            final @ParameterLayout(named = "Include terminated") boolean includeTerminated) {
        return allMatches(
                "findByBrand",
                "brand", brand,
                "includeTerminated", includeTerminated,
                "date", clockService.now());
    }

    // //////////////////////////////////////

    @Subscribe
    @Programmatic
    public void on(final Brand.RemoveEvent ev) {
        Brand sourceBrand = (Brand) ev.getSource();
        Brand replacementBrand = ev.getReplacement();

        List<Occupancy> occupancies;
        switch (ev.getPhase()) {
        case VALIDATE:
            occupancies = findByBrand(sourceBrand, true);

            if (replacementBrand == null && occupancies.size() > 0) {
                ev.invalidate("Brand is being used in an occupancy. Remove occupancy or provide a replacement");
            } else {
                scratchpad.put(onBrandRemoveScratchpadKey = UUID.randomUUID(), occupancies);
            }

            break;
        case EXECUTING:
            occupancies = (List<Occupancy>) scratchpad.get(onBrandRemoveScratchpadKey);
            for (Occupancy occupancy : occupancies) {
                occupancy.setBrand(replacementBrand);
            }
            break;
        default:
            break;
        }
    }

    // //////////////////////////////////////

    private transient UUID onBrandRemoveScratchpadKey;

    @Inject
    private Scratchpad scratchpad;

    @Inject
    private ClockService clockService;
}
