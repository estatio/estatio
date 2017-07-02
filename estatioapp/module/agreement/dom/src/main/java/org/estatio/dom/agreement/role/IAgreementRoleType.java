package org.estatio.dom.agreement.role;

import org.incode.module.base.dom.utils.StringUtils;

import org.estatio.dom.agreement.type.IAgreementType;
import org.estatio.dom.party.role.IPartyRoleType;

public interface IAgreementRoleType extends IPartyRoleType {

    default String getTitle() {
        return title();
    }

    default String title() {
        return StringUtils.enumTitle(this.toString());
    }

    IAgreementType getAppliesTo();

    default AgreementRoleType findUsing(final AgreementRoleTypeRepository repo) {
        return repo.find(this);
    }

    default AgreementRoleType findOrCreateUsing(AgreementRoleTypeRepository repository) {
        return  repository.findOrCreate(this, getAppliesTo());
    }


    class Meta {
        public final static int MAX_LEN = 30;

        private Meta() {
        }
    }

}