package org.estatio.module.budgetassignment.dom.contributed;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.budgeting.dom.budget.Budget;

/**
 * This currently could be inlined into Budget, however it is incomplete and my suspicion is that eventually it
 * may (like the other mixins that do calculations) will depend upon services that are not within Budget.
 */
@Mixin
public class Budget_Reconcile {

    private final Budget budget;
    public Budget_Reconcile(Budget budget){
        this.budget = budget;
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public Budget reconcile(
            @ParameterLayout(describedAs = "Final calculation will make the calculations permanent and impact the leases")
            final boolean finalCalculation) {
        // TODO: implement
        return budget;
    }

}
