package org.estatio.dom.agreement;

import java.util.List;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.NotContributed;

import org.estatio.dom.EstatioDomainService;

@Hidden
public class AgreementRoleCommunicationChannelTypes extends EstatioDomainService<AgreementRoleCommunicationChannelType> {

    public AgreementRoleCommunicationChannelTypes() {
        super(AgreementRoleCommunicationChannelTypes.class, AgreementRoleCommunicationChannelType.class);
    }
    
    // //////////////////////////////////////


    @NotContributed
    public AgreementRoleCommunicationChannelType findByTitle(final String title) {
        return firstMatch("findByTitle", "title", title);
    }

    @NotContributed
    public List<AgreementRoleCommunicationChannelType> findApplicableTo(AgreementType agreementType) {
        return allMatches("findByAgreementType", "agreementType", agreementType);
    }


}
