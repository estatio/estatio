package org.estatio.module.bankmandate.contributions;

import org.apache.isis.applib.annotation.*;
import org.estatio.module.agreement.dom.AgreementRoleHolder;
import org.estatio.module.bankmandate.dom.BankMandate;

import javax.inject.Inject;
import java.util.Collection;

@Mixin(method = "coll")
public class AgreementRoleHolder_allBankMandates {

    private final AgreementRoleHolder agreementRoleHolder;

    public AgreementRoleHolder_allBankMandates(AgreementRoleHolder agreementRoleHolder) {
        this.agreementRoleHolder = agreementRoleHolder;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(named = "List All", contributed = Contributed.AS_ACTION)
    public Collection<BankMandate> coll() {
        return agreementRoleHolderBankMandateService.allBankMandates(this.agreementRoleHolder);
    }

    @Inject
    AgreementRoleHolderBankMandateService agreementRoleHolderBankMandateService;
}
