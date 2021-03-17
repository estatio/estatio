package org.estatio.module.agreement.dom.commchantype;

import org.incode.module.base.dom.TitledEnum;
import org.incode.module.base.dom.utils.StringUtils;

import org.estatio.module.agreement.dom.AgreementRoleCommunicationChannelType;
import org.estatio.module.agreement.dom.AgreementRoleCommunicationChannelTypeRepository;
import org.estatio.module.agreement.dom.type.IAgreementType;

public interface IAgreementRoleCommunicationChannelType extends TitledEnum {

    default String getTitle() {
        return title();
    }

    default String title() {
        return StringUtils.enumTitle(this.toString());
    }

    IAgreementType getAppliesTo();

    default AgreementRoleCommunicationChannelType findUsing(final AgreementRoleCommunicationChannelTypeRepository repo) {
        return repo.find(this);
    }

    default AgreementRoleCommunicationChannelType findOrCreateUsing(AgreementRoleCommunicationChannelTypeRepository repository) {
        return  repository.findOrCreate(this, this.getAppliesTo());
    }

}