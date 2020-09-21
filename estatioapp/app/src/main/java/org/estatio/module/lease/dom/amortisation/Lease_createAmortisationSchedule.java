package org.estatio.module.lease.dom.amortisation;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.charge.dom.Charge;
import org.estatio.module.lease.dom.Frequency;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseStatus;

@Mixin
public class Lease_createAmortisationSchedule {

    private final Lease lease;

    public Lease_createAmortisationSchedule(Lease lease) {
        this.lease = lease;
    }

    @Action()
    public Lease $$(
            final Charge charge,
            final Frequency frequency,
            final LocalDate startDate,
            final LocalDate endDate
    ) {
        amortisationScheduleService.findOrCreateAmortisationScheduleForLeaseAndCharge(lease, charge, frequency, startDate, endDate);
        return lease;
    }

    public List<Charge> choices0$$(){
        return Lists.newArrayList(lease.getItems()).stream().filter(li->li.getCharge()!=null).map(LeaseItem::getCharge).distinct().collect(Collectors.toList());
    }

    public List<Frequency> choices1$$(){
        // currently the only frequencies supported
        return Arrays.asList(Frequency.MONTHLY, Frequency.QUARTERLY);
    }

    public String validate$$(
            final Charge charge,
            final Frequency frequency,
            final LocalDate startDate,
            final LocalDate endDate){
        if (!LocalDateInterval.including(startDate,endDate).isValid()) return "The start date and the end date should be a valid interval";
        return null;
    }

    public boolean hide$$(){
        return lease.getStatus()== LeaseStatus.PREVIEW;
    }

    @Inject
    AmortisationScheduleService amortisationScheduleService;

}
