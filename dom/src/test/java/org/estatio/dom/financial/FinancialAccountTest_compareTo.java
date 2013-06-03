package org.estatio.dom.financial;

import java.util.List;

import org.estatio.dom.ComparableContractTest_compareTo;


public class FinancialAccountTest_compareTo extends ComparableContractTest_compareTo<FinancialAccount> {

    @SuppressWarnings("unchecked")
    @Override
    protected List<List<FinancialAccount>> orderedTuples() {
        return listOf(
                listOf(
                        newFinancialAccount(null, null),
                        newFinancialAccount(null, "ABC"),
                        newFinancialAccount(null, "ABC"),
                        newFinancialAccount(null, "DEF")
                        ),
                listOf(
                        newFinancialAccount(FinancialAccountType.BANK_ACCOUNT, null),
                        newFinancialAccount(FinancialAccountType.BANK_ACCOUNT, "ABC"),
                        newFinancialAccount(FinancialAccountType.BANK_ACCOUNT, "ABC"),
                        newFinancialAccount(FinancialAccountType.BANK_ACCOUNT, "DEF")
                        )
                );
    }

    private FinancialAccount newFinancialAccount(
            FinancialAccountType type, String reference) {
        final FinancialAccount fa = new FinancialAccount() {};
        fa.setType(type);
        fa.setReference(reference);
        return fa;
    }

}
