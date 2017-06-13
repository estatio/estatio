package org.estatio.dom.party.role;

import java.util.Objects;

import org.apache.isis.applib.annotation.Programmatic;

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

    @Programmatic
    default PartyRoleType findUsing(final PartyRoleTypeRepository repo) {
        return repo.findByKey(getKey());
    }

    @Programmatic
    default PartyRoleType findOrCreateUsing(PartyRoleTypeRepository repo) {
        return  repo.findOrCreate(this);
    }

    @Programmatic
    default boolean equalsKey(IPartyRoleType partyRoleType) {
        return Objects.equals(getKey(), partyRoleType.getKey());
    }

    class Meta {
        private Meta(){}
        public final static int MAX_LEN = 30;
    }


}
