package org.estatio.capex.dom.bankaccount.verification.tasks;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.bookmark.BookmarkService;

import org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationStateTransition;
import org.estatio.capex.dom.task.Task;
import org.estatio.dom.financial.bankaccount.BankAccount;

@Mixin
public class BankAccount_tasks {

    private final BankAccount bankAccount;

    public BankAccount_tasks(final BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<Task> tasks() {
        final List<BankAccountVerificationStateTransition> transitions = repository.findByDomainObject(bankAccount);
        return Task.from(transitions);
    }

    @Inject
    BankAccountVerificationStateTransition.Repository repository;

    @Inject
    BookmarkService bookmarkService;


}
