package org.estatio.dom.agreement.type;

import org.incode.module.base.dom.TitledEnum;
import org.incode.module.base.dom.utils.StringUtils;

public interface IAgreementType extends TitledEnum {

    default String getTitle() {
        return title();
    }

    default String title() {
        return StringUtils.enumTitle(this.toString());
    }

    default AgreementType findUsing(AgreementTypeRepository agreementTypeRepository) {
        return agreementTypeRepository.find(this);
    }

    default AgreementType findOrCreateUsing(AgreementTypeRepository agreementTypeRepository) {
        return agreementTypeRepository.findOrCreate(this);
    }

}