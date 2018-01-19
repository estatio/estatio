package org.estatio.module.capex.app;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.assetfinancial.dom.FixedAssetFinancialAccount;
import org.estatio.module.assetfinancial.dom.FixedAssetFinancialAccountRepository;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.financial.dom.BankAccountRepository;
import org.estatio.module.financial.dom.FinancialAccount;
import org.estatio.module.party.dom.Party;

@DomainService(
        nature = NatureOfService.DOMAIN,
        objectType = "org.estatio.module.capex.app.DebtorBankAccountService")
public class DebtorBankAccountService {

    @Programmatic
    public BankAccount uniqueDebtorAccountToPay(final IncomingInvoice invoice) {
        final Party buyer = invoice.getBuyer();
        List<BankAccount> bankAccountsForBuyer = bankAccountRepository.findBankAccountsByOwner(buyer);

        final Property propertyIfAny = invoice.getProperty();
        if (propertyIfAny != null) {
            List<FixedAssetFinancialAccount> fafrList = fixedAssetFinancialAccountRepository.findByFixedAsset(propertyIfAny);
            List<FinancialAccount> bankAccountsForProperty = fafrList.stream()
                    .map(FixedAssetFinancialAccount::getFinancialAccount)
                    .filter(BankAccount.class::isInstance)
                    .map(BankAccount.class::cast)
                    .collect(Collectors.toList());

            bankAccountsForBuyer.retainAll(bankAccountsForProperty);
        }

        // original implementation ... see if we already have a unique bank account
        int numBankAccounts = bankAccountsForBuyer.size();
        switch (numBankAccounts) {
        case 0:
            return null;
        case 1:
            return bankAccountsForBuyer.get(0);
        default:
            // otherwise, non-unique, so fall through
        }

        // see if removing non-preferred helps
        bankAccountsForBuyer.removeIf(x -> (x.getPreferred() == null || !x.getPreferred()));

        numBankAccounts = bankAccountsForBuyer.size();
        switch (numBankAccounts) {
        case 0:
            return null;
        case 1:
            return bankAccountsForBuyer.get(0);
        default:
            // give up, still non-duplicate
            return null;
        }
    }

    @Inject
    BankAccountRepository bankAccountRepository;

    @Inject
    FixedAssetFinancialAccountRepository fixedAssetFinancialAccountRepository;

}
