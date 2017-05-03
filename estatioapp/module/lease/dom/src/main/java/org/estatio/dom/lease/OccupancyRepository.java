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
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.scratchpad.Scratchpad;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.asset.UnitRepository;
import org.estatio.dom.lease.tags.Brand;

@DomainService(menuOrder = "40", repositoryFor = Occupancy.class, nature = NatureOfService.DOMAIN)
public class OccupancyRepository extends UdoDomainRepositoryAndFactory<Occupancy> {

    public OccupancyRepository() {
        super(OccupancyRepository.class, Occupancy.class);
    }

    // //////////////////////////////////////

    @Programmatic
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

    @Programmatic
    public List<Occupancy> findByUnit(Unit unit) {
        return allMatches("findByUnit", "unit", unit);
    }

    @Programmatic
    public List<Occupancy> findByLease(Lease lease) {
        return allMatches("findByLease", "lease", lease);
    }

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

    @Programmatic
    public List<Occupancy> findByBrand(
            final Brand brand,
            final boolean includeTerminated) {
        return allMatches(
                "findByBrand",
                "brand", brand,
                "includeTerminated", includeTerminated,
                "date", clockService.now());
    }

    @Programmatic
    public List<Occupancy> occupanciesByUnitAndInterval(final Unit unit, final LocalDateInterval localDateInterval) {
        return findByUnit(unit).stream()
                .filter(occupancy -> occupancy.getInterval().overlaps(localDateInterval))
                .collect(Collectors.toList());
    }

    @Programmatic
    public List<Occupancy> occupanciesByPropertyAndInterval(final Property property, final LocalDateInterval localDateInterval) {
        return findByProperty(property).stream()
                .filter(occupancy -> occupancy.getInterval().overlaps(localDateInterval))
                .collect(Collectors.toList());
    }

    @Programmatic
    public List<Occupancy> findByProperty(final Property property) {
        return allMatches("findByProperty", "property", property);
    }

    // //////////////////////////////////////

    private void verifyFor(Lease lease) {
        findByLease(lease).stream()
                .forEach(occupancy -> occupancy.verify());
    }

    private void terminateFor(Lease lease, LocalDate terminationDate) {
        findByLease(lease).stream()
                .forEach(occupancy -> occupancy.terminate(terminationDate));
    }

    // //////////////////////////////////////

    @com.google.common.eventbus.Subscribe
    @Programmatic
    public void on(final Lease.ChangeDatesEvent ev) {
        switch (ev.getEventPhase()) {
        case EXECUTED:
            verifyFor(ev.getSource());
            break;
        default:
            break;
        }
    }

    @com.google.common.eventbus.Subscribe
    @Programmatic
    public void on(final Lease.TerminateEvent ev) {
        switch (ev.getEventPhase()) {
        case EXECUTED:
            terminateFor(ev.getSource(), ev.getTerminationDate());
            break;
        default:
            break;
        }
    }

    // //////////////////////////////////////

    @com.google.common.eventbus.Subscribe
    @Programmatic
    public void on(final Brand.RemoveEvent ev) {
        Brand sourceBrand = (Brand) ev.getSource();
        Brand replacementBrand = ev.getReplacement();

        List<Occupancy> occupancies;
        switch (ev.getEventPhase()) {
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

    @Inject
    private LeaseRepository leaseRepository;

    @Inject
    private UnitRepository unitRepository;

}
