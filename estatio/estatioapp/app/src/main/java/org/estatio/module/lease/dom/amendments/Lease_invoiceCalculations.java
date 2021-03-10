package org.estatio.module.lease.dom.amendments;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseStatus;

@Mixin
public class Lease_invoiceCalculations {

    private final Lease lease;

    public Lease_invoiceCalculations(Lease lease) {
        this.lease = lease;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<PersistedCalculationResult> $$() {
        List<PersistedCalculationResult> result = new ArrayList<>();
        Lists.newArrayList(lease.getItems()).forEach(li->{
            Lists.newArrayList(li.getTerms()).forEach(lt->{
                result.addAll(persistedCalculationResultRepository.findByLeaseTerm(lt));
            });
        });
        return result;
    }

    public boolean hide$$(){
        if (lease.getStatus()== LeaseStatus.PREVIEW) return false;
        return true;
    }

    @Inject
    PersistedCalculationResultRepository persistedCalculationResultRepository;

}
