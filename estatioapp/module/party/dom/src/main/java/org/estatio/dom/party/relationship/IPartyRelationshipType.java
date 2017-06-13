package org.estatio.dom.party.relationship;

import org.incode.module.base.dom.TitledEnum;
import org.incode.module.base.dom.utils.StringUtils;

public interface IPartyRelationshipType  extends TitledEnum {

    // String getKey();

    default String getTitle() {
        return title();
    }

    default String title() {
        return StringUtils.enumTitle(this.toString());
    }


}

