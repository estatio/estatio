package org.estatio.module.lease.dom.amendments;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.lease.dom.LeaseStatus;
import org.estatio.module.lease.dom.LeaseTerm;

@Mixin
public class LeaseTerm_invoiceCalculations {

    private final LeaseTerm leaseTerm;

    public LeaseTerm_invoiceCalculations(LeaseTerm leaseTerm) {
        this.leaseTerm = leaseTerm;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<PersistedCalculationResult> $$() {
        return persistedCalculationResultRepository.findByLeaseTerm(leaseTerm);
    }

    public boolean hide$$(){
        if (leaseTerm.getLeaseItem().getLease().getStatus()== LeaseStatus.PREVIEW) return false;
        return true;
    }

    @Inject
    PersistedCalculationResultRepository persistedCalculationResultRepository;

}
