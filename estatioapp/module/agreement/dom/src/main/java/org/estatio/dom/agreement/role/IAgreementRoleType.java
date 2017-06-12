package org.estatio.dom.agreement.role;

import org.estatio.dom.agreement.IAgreementType;

public interface IAgreementRoleType {
    String getTitle();

    default AgreementRoleType findUsing(final AgreementRoleTypeRepository repo) {
        return repo.find(this);
    }

    default AgreementRoleType findOrCreateUsing(AgreementRoleTypeRepository repository, IAgreementType IAgreementType) {
        return  repository.findOrCreate(this, IAgreementType);
    }


}