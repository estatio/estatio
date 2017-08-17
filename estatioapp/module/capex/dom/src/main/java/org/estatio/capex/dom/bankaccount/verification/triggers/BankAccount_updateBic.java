package org.estatio.capex.dom.bankaccount.verification.triggers;

import java.net.MalformedURLException;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.financial.bankaccount.BankAccount;

/**
 * TODO: inline this mixin
 */
@Mixin(method="act")
public class BankAccount_updateBic {

    private final BankAccount bankAccount;

    public BankAccount_updateBic(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    @Action(
            semantics = SemanticsOf.IDEMPOTENT
    )
    public BankAccount act(@Nullable String bic) throws MalformedURLException {
        bankAccount.setBic(BankAccount.trimBic(bic));
        return bankAccount;
    }

    public String default0Act() { return bankAccount.getBic(); }

}
