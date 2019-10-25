package org.estatio.module.bankmandate.contributions;

import org.apache.isis.applib.annotation.*;
import org.estatio.module.agreement.dom.AgreementRoleHolder;
import org.estatio.module.bankmandate.dom.BankMandate;

import javax.inject.Inject;
import java.util.Collection;

@Mixin(method = "coll")
public class AgreementRoleHolder_currentBankMandates {

    private final AgreementRoleHolder agreementRoleHolder;

    public AgreementRoleHolder_currentBankMandates(AgreementRoleHolder agreementRoleHolder) {
        this.agreementRoleHolder = agreementRoleHolder;
    }

    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @Action(semantics = SemanticsOf.SAFE)
    @CollectionLayout(defaultView = "table")
    @MemberOrder(sequence = "80")
    public Collection<BankMandate> coll() {
        return agreementRoleHolderBankMandateService.currentBankMandates(this.agreementRoleHolder);
    }

    @Inject
    AgreementRoleHolderBankMandateService agreementRoleHolderBankMandateService;
}
