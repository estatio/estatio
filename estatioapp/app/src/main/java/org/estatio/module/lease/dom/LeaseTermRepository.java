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
package org.estatio.module.lease.dom;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.user.UserService;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.security.UserMemento;
import org.apache.isis.applib.services.factory.FactoryService;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.base.dom.EstatioRole;
import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;

@DomainService(
        repositoryFor = LeaseTerm.class,
        objectType = "org.estatio.dom.lease.LeaseTermRepository"
)
@DomainServiceLayout(
        named = "Leases",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "40.2"
)
public class LeaseTermRepository extends UdoDomainRepositoryAndFactory<LeaseTerm> {

    public LeaseTermRepository() {
        super(LeaseTermRepository.class, LeaseTerm.class);
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT, hidden = Where.EVERYWHERE)
    public LeaseTerm newLeaseTerm(
            final LeaseItem leaseItem,
            final LeaseTerm previous,
            final LocalDate startDate,
            final LocalDate endDate) {
        LeaseTerm leaseTerm = leaseItem.getType().create(factoryService);
        leaseTerm.setLeaseItem(leaseItem);
        leaseTerm.setPrevious(previous);
        if (previous != null) {
            previous.setNext(leaseTerm);
        }
        leaseTerm.modifyStartDate(startDate);

        if (endDate == null && !leaseTerm.allowOpenEndDate()){
            LocalDate nextEndDate = leaseTerm.getFrequency().nextDate(startDate).minusDays(1);
            leaseTerm.modifyEndDate(nextEndDate);
        } else {
            leaseTerm.modifyEndDate(endDate);
        }
        // TOFIX: When changing the user in the integration test from 'tester' to 'estatio-admin' the getPrevious method returns null. Setting both sides of the bi-directional relationship makes them pass.

        leaseTerm.initialize();
        leaseTerm.align();

        if (previous != null) {
            previous.setNext(leaseTerm);
        }
        // TOFIX: without this flush and refresh, the collection of terms on the
        // item is not updated. Removing code below will fail integration tests
        // too.
        repositoryService.persistAndFlush(leaseTerm);
//        getIsisJdoSupport().getJdoPersistenceManager().flush();
        getIsisJdoSupport().refresh(leaseItem);
        return leaseTerm;
    }

    public String validateNewLeaseTerm(
            final LeaseItem leaseItem,
            final LeaseTerm previous,
            final LocalDate startDate,
            final LocalDate endDate) {
        if (previous != null) {
            if (startDate.isBefore(previous.getStartDate())) {
                return String.format("Start date must be on or after %s", previous.getStartDate().toString());
            }
        }
        final LocalDateInterval interval = LocalDateInterval.including(startDate, endDate);
        if (!interval.isValid()) {
            return String.format("From %s to %s is not a valid interval", startDate.toString(), endDate.toString());
        }
        if (endDate == null && !leaseItem.getType().allowOpenEndDate()) {
            return String.format("A term of type %s should have an end date", leaseItem.getType());
        }
        return null;
    }

    @Deprecated
    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "20")
    public List<LeaseTerm> allLeaseTermsToBeApproved(final LocalDate date) {
        return repositoryService.allMatches(new QueryDefault<>(LeaseTerm.class,
                "findByStatusAndActiveDate", "status", LeaseTermStatus.NEW, "date", date));
    }

    public LocalDate default0AllLeaseTermsToBeApproved() {
        return getClockService().now();
    }

    @Programmatic
    public List<LeaseTerm> findByLeaseItem(final LeaseItem leaseItem) {
        return repositoryService.allMatches(new QueryDefault<>(LeaseTerm.class,"findByLeaseItem",
                "leaseItem", leaseItem));
    }

    /**
     * Returns terms by LeaseItem and sequence. Used by the API
     */
    @Action(hidden = Where.EVERYWHERE)
    public LeaseTerm findByLeaseItemAndSequence(final LeaseItem leaseItem, final BigInteger sequence) {
        List<LeaseTerm> list = repositoryService.allMatches(new QueryDefault<>(LeaseTerm.class,
                "findByLeaseItemAndSequence", "leaseItem", leaseItem, "sequence", sequence));
        return list.isEmpty() ? null : list.get(0);
    }

    @Programmatic
    public LeaseTerm findByLeaseItemAndStartDate(final LeaseItem leaseItem, final LocalDate startDate) {
        List<LeaseTerm> list = repositoryService.allMatches(new QueryDefault<>(LeaseTerm.class,
                "findByLeaseItemAndStartDate", "leaseItem", leaseItem, "startDate", startDate));
        return list.isEmpty() ? null : list.get(0);
    }

    @Action(semantics = SemanticsOf.SAFE, restrictTo = RestrictTo.PROTOTYPING)
    @MemberOrder(sequence = "99")
    public List<LeaseTerm> allLeaseTerms() {
        return repositoryService.allInstances(LeaseTerm.class);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE, hidden = Where.EVERYWHERE)
    public List<LeaseTerm> findByPropertyAndTypeAndStartDate(
            final Property property,
            final LeaseItemType leaseItemType,
            final LocalDate startDate) {
        return repositoryService.allMatches(new QueryDefault<>(LeaseTerm.class,"findByPropertyAndTypeAndStartDate",
                "property", property,
                "leaseItemType", leaseItemType,
                "startDate", startDate));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
//    @Action(semantics = SemanticsOf.SAFE, hidden = Where.EVERYWHERE)
    @Programmatic
    public List<LeaseTermForServiceCharge> findServiceChargeByPropertyAndItemTypeAndStartDate(
            final Property property,
            final List<LeaseItemType> leaseItemTypes,
            final LocalDate startDate) {
        List<LeaseTermForServiceCharge> leaseTerms = new ArrayList<>();
        for (LeaseItemType type : leaseItemTypes) {
            leaseTerms.addAll((List) findByPropertyAndTypeAndStartDate(property, type, startDate));
        }
        return leaseTerms;
    }

    @Action(semantics = SemanticsOf.SAFE, hidden = Where.EVERYWHERE)
    public List<LeaseTerm> findByPropertyAndType(
            final Property property,
            final LeaseItemType leaseItemType) {
        return repositoryService.allMatches(new QueryDefault<>(LeaseTerm.class,"findByPropertyAndType",
                "property", property,
                "leaseItemType", leaseItemType));
    }

    // //////////////////////////////////////

    /* TODO: refactor allMatches to use RepositoryService.allMatches() */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Action(semantics = SemanticsOf.SAFE, hidden = Where.EVERYWHERE)
    public List<LocalDate> findStartDatesByPropertyAndType(
            final Property property,
            final LeaseItemType leaseItemType) {
        List startDates = allMatches(
                "findStartDatesByPropertyAndType",
                "property", property,
                "leaseItemType", leaseItemType);
        return startDates;
    }

    /* TODO: refactor allMatches to use RepositoryService.allMatches() */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Action(semantics = SemanticsOf.SAFE, hidden = Where.EVERYWHERE)
    public List<LocalDate> findStartDatesByPropertyAndTypeAndInvoicedBy(
            final Property property,
            final LeaseItemType leaseItemType,
            final List<LeaseAgreementRoleTypeEnum> invoicedByList) {
        List<LocalDate> result = new ArrayList<>();
        for (LeaseAgreementRoleTypeEnum invoicedBy : invoicedByList) {
            result.addAll((List) allMatches(
                    "findStartDatesByPropertyAndTypeAndInvoicedBy",
                    "property", property,
                    "leaseItemType", leaseItemType,
                    "invoicedBy", invoicedBy));
        }
        return result;
    }

    @Programmatic
    public List<LocalDate> findServiceChargeDatesByPropertyAndLeaseItemTypeAndInvoicedBy(final Property property, final List<LeaseItemType> leaseItemTypes, final List<LeaseAgreementRoleTypeEnum> invoicedBy) {
        List<LocalDate> result = new ArrayList<>();
        for (LeaseItemType type : leaseItemTypes){
            for (LocalDate date : findStartDatesByPropertyAndTypeAndInvoicedBy(property, type, invoicedBy)){
                if (!result.contains(date)){
                    result.add(date);
                }
            }
        }
        return result.stream().sorted().collect(Collectors.toList());
    }

    @Programmatic
    public List<LeaseTermForServiceCharge> findServiceChargeByPropertyAndItemTypeWithStartDateInPeriod(final Property property, final List<LeaseItemType> leaseItemTypes, final List<LeaseAgreementRoleTypeEnum> invoicedBy, final LocalDate startDate, final LocalDate endDate) {
        List<LeaseTermForServiceCharge> result = new ArrayList<>();
        for (LeaseItemType type : leaseItemTypes) {
            result.addAll((List) findByPropertyAndType(property, type));
        }
        return result.stream()
                .filter(lt->!lt.getStartDate().isBefore(startDate))
                .filter(lt->!lt.getStartDate().isAfter(endDate))
                .filter(lt->invoicedBy.contains(lt.getLeaseItem().getInvoicedBy()))
                .collect(Collectors.toList());
    }

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "30")
    public List<LeaseTerm> findTermsWithInvalidInterval() {
        List<LeaseTerm> lts = allLeaseTerms();
        List<LeaseTerm> returnList = new ArrayList<>();
        LocalDateInterval ldi;
        for (LeaseTerm lt : lts) {
            try {
                if ((ldi = lt.getEffectiveInterval()) == null) {
                    returnList.add(lt);
                    continue;
                }

                if (!ldi.isValid()) {
                    returnList.add(lt);
                    continue;
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                returnList.add(lt);
            }
        }

        if (returnList.isEmpty()) {
            return null;
        } else {
            return returnList;
        }
    }

    public boolean hideFindTermsWithInvalidInterval() {
        final UserMemento user = getContainer().getUser();
        return !EstatioRole.ADMINISTRATOR.isApplicableFor(userService.getUser());
    }

    @Programmatic
    public LeaseTerm findOrCreateWithStartDate(final LeaseItem leaseItem, final LocalDateInterval localDateInterval) {
        boolean leaseTermFound = false;
        for (LeaseTerm leaseTerm : leaseItem.getTerms()) {
            if (leaseTerm.getStartDate().equals(localDateInterval.startDate())) {
                return leaseTerm;
            }
        }
        if (!leaseTermFound) {
            return leaseItem.newTerm(localDateInterval.startDate(), localDateInterval.endDate());
        }
        return null;
    }

    @Inject
    FactoryService factoryService;

    @Inject
    private UserService userService;
}
