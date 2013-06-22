package org.estatio.dom.agreement;

import java.util.List;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.NotContributed;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.party.Party;
import org.estatio.dom.utils.StringUtils;

@Hidden
public class Agreements extends EstatioDomainService<Agreement> {

    public Agreements() {
        super(Agreements.class, Agreement.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @NotContributed
    public Agreement newAgreement(AgreementType agreementType, final String reference, final String name) {
        return agreementType.create(getContainer());
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    public Agreement findByReference(String reference) {
        return firstMatch("findByReference", "r", StringUtils.wildcardToRegex(reference));
    }

    @ActionSemantics(Of.SAFE)
    @NotContributed
    public List<Agreement> findByAgreementTypeAndRoleTypeAndParty(AgreementType agreementType, AgreementRoleType agreementRoleType, Party party) {
        return allMatches("findByAgreementTypeAndRoleTypeAndParty", "agreementType", agreementType, "roleType", agreementRoleType, "party", party);
    }

}
