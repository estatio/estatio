package org.estatio.dom.agreement.role;

import org.estatio.dom.agreement.type.IAgreementType;

public interface IAgreementRoleType {
    String getTitle();

    default AgreementRoleType findUsing(final AgreementRoleTypeRepository repo) {
        return repo.find(this);
    }

    default AgreementRoleType findOrCreateUsing(AgreementRoleTypeRepository repository, IAgreementType IAgreementType) {
        return  repository.findOrCreate(this, IAgreementType);
    }


    public static class Meta {
        public final static int MAX_LEN = 30;

        private Meta() {
        }
    }

}