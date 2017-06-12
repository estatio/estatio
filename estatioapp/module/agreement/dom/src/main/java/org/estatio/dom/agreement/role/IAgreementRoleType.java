package org.estatio.dom.agreement.role;

import org.estatio.dom.agreement.AgreementTypeData;

public interface IAgreementRoleType {
    String getTitle();

    default AgreementRoleType findUsing(final AgreementRoleTypeRepository repo) {
        return repo.find(this);
    }

    default AgreementRoleType findOrCreateUsing(AgreementRoleTypeRepository repository, AgreementTypeData agreementTypeData) {
        return  repository.findOrCreate(this, agreementTypeData);
    }


}