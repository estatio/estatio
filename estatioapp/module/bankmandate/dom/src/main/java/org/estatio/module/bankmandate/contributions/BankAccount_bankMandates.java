package org.estatio.module.bankmandate.contributions;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.bankmandate.dom.BankMandate;
import org.estatio.module.bankmandate.dom.BankMandateRepository;
import org.estatio.module.bankaccount.dom.BankAccount;

@Mixin
public class BankAccount_bankMandates {

    private final BankAccount bankAccount;

    public BankAccount_bankMandates(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @CollectionLayout(defaultView = "table")
    public List<BankMandate> $$() {
        return bankMandateRepository.findBankMandatesFor(bankAccount);
    }

    @Inject BankMandateRepository bankMandateRepository;
}
