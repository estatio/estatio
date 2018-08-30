package org.estatio.module.capex.dom.bankaccount.verification.tasks;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.capex.dom.bankaccount.verification.BankAccountVerificationState;
import org.estatio.module.capex.dom.bankaccount.verification.BankAccountVerificationStateTransition;
import org.estatio.module.capex.dom.bankaccount.verification.BankAccountVerificationStateTransitionType;
import org.estatio.module.capex.dom.dobj.DomainObject_checkStateAbstract;
import org.estatio.module.capex.dom.state.StateTransitionRepositoryGeneric;
import org.estatio.module.capex.dom.state.StateTransitionService;
import org.estatio.module.financial.dom.BankAccount;

/**
 * This cannot be inlined (needs to be a mixin) because BankAccount does not know abouts its verification state machine
 */
@Mixin(method="act")
public class BankAccount_checkVerificationState
        extends DomainObject_checkStateAbstract<
                            BankAccount,
                            BankAccountVerificationStateTransition,
                            BankAccountVerificationStateTransitionType,
                            BankAccountVerificationState> {

    public BankAccount_checkVerificationState(final BankAccount bankAccount) {
        super(bankAccount, BankAccountVerificationStateTransition.class);
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(
            contributed= Contributed.AS_ACTION,
            cssClassFa = "fa-question-circle" // override isis-non-changing.properties
    )
    @Override
    public BankAccount act() {

        super.act();
        if (stateTransitionRepositoryGeneric.findByDomainObject(domainObject, BankAccountVerificationStateTransition.class).isEmpty()){
            stateTransitionService.trigger(domainObject,BankAccountVerificationStateTransitionType.INSTANTIATE, null, null);
        }

        return domainObject;
    }

    @Inject
    StateTransitionRepositoryGeneric stateTransitionRepositoryGeneric;

    @Inject
    StateTransitionService stateTransitionService;

}
