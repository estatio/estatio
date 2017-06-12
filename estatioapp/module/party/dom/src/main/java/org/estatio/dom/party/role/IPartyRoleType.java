package org.estatio.dom.party.role;

import org.incode.module.base.dom.TitledEnum;
import org.incode.module.base.dom.utils.StringUtils;

public interface IPartyRoleType extends TitledEnum {

    String getKey();

    default String getTitle() {
        return title();
    }

    default String title() {
        return StringUtils.enumTitle(this.toString());
    }

    default PartyRoleType findUsing(final PartyRoleTypeRepository repo) {
        return repo.findByKey(getKey());
    }

    default PartyRoleType findOrCreateUsing(PartyRoleTypeRepository repository) {
        return  repository.findOrCreate(this);
    }


    class Meta {
        private Meta(){}
        public final static int MAX_LEN = 30;
    }


}
