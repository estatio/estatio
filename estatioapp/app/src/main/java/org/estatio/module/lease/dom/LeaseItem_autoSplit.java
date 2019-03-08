package org.estatio.module.lease.dom;

import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.util.Lists;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Where;

@Mixin
public class LeaseItem_autoSplit {

    private final LeaseItem leaseItem;

    public LeaseItem_autoSplit(LeaseItem leaseItem) {
        this.leaseItem = leaseItem;
    }

    @Action()
    @ActionLayout(hidden = Where.EVERYWHERE)
    public LeaseItem autoSplit() {

        final List<LeaseTerm> firstTerms = Lists.newArrayList(leaseItem.getTerms()).stream()
                .filter(leaseTerm -> leaseTerm.getPrevious() == null)
                .collect(Collectors.toList());

        for (LeaseTerm firstTerm : firstTerms){
            LeaseTerm term = firstTerm;

            do {
                final LocalDate calculatedNextStartDate = term.getFrequency().nextDate(term.getStartDate());
                if (term.getInterval().endDateExcluding().isAfter(calculatedNextStartDate)){
                    // term is longer than the caclulated next start date (based on the interval defined on the lease item)
                    term = term.split(calculatedNextStartDate);
                } else {
                    term = term.getNext();
                }
            } while(term != null);
        }

        return leaseItem;
    }


}
