package org.estatio.dom.agreement;

import java.util.List;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.NotContributed;

import org.estatio.dom.EstatioDomainService;

@Hidden
public class AgreementRoleTypes extends EstatioDomainService<AgreementRoleType> {

    public AgreementRoleTypes() {
        super(AgreementRoleTypes.class, AgreementRoleType.class);
    }
    
    // //////////////////////////////////////


    @NotContributed
    public AgreementRoleType findByTitle(final String title) {
        return firstMatch("findByTitle", "title", title);
    }

    @NotContributed
    public List<AgreementRoleType> findApplicableTo(AgreementType agreementType) {
        return allMatches("findByAgreementType", "appliesTo", agreementType);
    }


}
