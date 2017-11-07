package org.estatio.dom.viewmodels;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.excel.dom.ExcelFixture;
import org.isisaddons.module.excel.dom.ExcelFixtureRowHandler;

import org.estatio.dom.Importable;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.assetfinancial.FixedAssetFinancialAccountRepository;
import org.estatio.module.financial.dom.FinancialAccountRepository;
import org.estatio.module.bankaccount.dom.BankAccount;
import org.estatio.module.bankaccount.dom.BankAccountRepository;
import org.estatio.module.bankaccount.dom.utils.IBANValidator;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.dom.Party;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.dom.viewmodels.BankAccountImport"
)
public class BankAccountImport implements ExcelFixtureRowHandler, Importable {

    @Getter @Setter
    private String ownerReference;

    @Getter @Setter
    private String iban;

    @Getter @Setter
    private String bic;

    @Getter @Setter
    private String bankAccountType;

    @Getter @Setter
    private String propertyReference;

    @Getter @Setter
    private String externalReference;

//    @Override
//    public List<Class> importAfter() {
//        return Lists.newArrayList(PropertyImport.class, OrganisationImport.class);
//    }

    @Programmatic
    @Override
    public List<Object> handleRow(FixtureScript.ExecutionContext executionContext, ExcelFixture excelFixture, Object previousRow) {
        return importData(previousRow);
    }

    // REVIEW: other import view models have @Action annotation here...  but in any case, is this view model actually ever surfaced in the UI?
    public List<Object> importData() {
        return importData(null);
    }

    @Programmatic
    @Override
    public List<Object> importData(final Object previousRow) {

        if (IBANValidator.valid(iban)) {
            final Party owner = partyRepository.findPartyByReference(ownerReference);
            BankAccount bankAccount = (BankAccount) financialAccountRepository.findByOwnerAndReference(owner, iban);
            if (owner == null)
                return Lists.newArrayList();
            if (bankAccount == null) {
                bankAccount = bankAccountRepository.newBankAccount(owner, iban, bic);
            } else {
                bankAccount.setIban(iban);
                bankAccount.verifyIban();
                bankAccount.setBic(BankAccount.trimBic(bic));
            }
            if (propertyReference != null) {
                final Property property = propertyRepository.findPropertyByReference(propertyReference);
                if (property == null) {
                    throw new IllegalArgumentException(String.format("Property with reference [%s] not found", propertyReference));
                }
                fixedAssetFinancialAccountRepository.findOrCreate(property, bankAccount);
            }
        }

        return Lists.newArrayList();
    }

    //region > injected services
    @Inject
    private FinancialAccountRepository financialAccountRepository;

    @Inject
    private PartyRepository partyRepository;

    @Inject
    private BankAccountRepository bankAccountRepository;

    @Inject
    private PropertyRepository propertyRepository;

    @Inject
    private FixedAssetFinancialAccountRepository fixedAssetFinancialAccountRepository;
    //endregion

}
