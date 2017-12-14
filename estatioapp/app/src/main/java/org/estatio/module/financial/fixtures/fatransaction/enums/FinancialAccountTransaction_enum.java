package org.estatio.module.financial.fixtures.fatransaction.enums;

import java.math.BigDecimal;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.PersonaWithBuilderScript;

import org.estatio.module.assetfinancial.fixtures.bankaccountfafa.enums.BankAccount_enum;
import org.estatio.module.financial.dom.FinancialAccountTransaction;
import org.estatio.module.financial.fixtures.fatransaction.builders.FinancialAccountTransactionBuilder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import static org.incode.module.base.integtests.VT.bd;
import static org.incode.module.base.integtests.VT.ld;

@AllArgsConstructor()
@Getter
@Accessors(chain = true)
public enum FinancialAccountTransaction_enum
        implements PersonaWithBuilderScript<FinancialAccountTransaction, FinancialAccountTransactionBuilder> {

    TopModelGb_xactn1(
            BankAccount_enum.TopModelGb, ld(2014, 7, 1), bd(1000)
    ),
    TopModelGb_xactn2(
            BankAccount_enum.TopModelGb, ld(2014, 7, 2), bd(2000)
    ),
    ;

    private final BankAccount_enum bankAccount_d;
    private final LocalDate date;
    private final BigDecimal amount;

    @Override
    public FinancialAccountTransactionBuilder builder() {
        return new FinancialAccountTransactionBuilder()
                .setPrereq((f,ec) -> f.setFinancialAccount(f.objectFor(bankAccount_d, ec)))
                .setDate(date)
                .setAmount(amount);
    }


}
