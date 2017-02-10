package org.estatio.app.mixins.budgetassignment;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.budgetassignment.dom.override.BudgetOverride;
import org.estatio.budgetassignment.dom.override.BudgetOverrideRepository;
import org.estatio.dom.lease.Lease;

@Mixin
public class Lease_BudgetOverrides {

    private final Lease lease;
    public Lease_BudgetOverrides(Lease lease){
        this.lease = lease;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<BudgetOverride> budgetOverrides() {
        return budgetOverrideRepository.findByLease(lease);
    }

    @Inject
    private BudgetOverrideRepository budgetOverrideRepository;

}
