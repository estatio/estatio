package org.estatio.dom.event;

import org.estatio.dom.TitledEnum;
import org.estatio.dom.utils.StringUtils;

//TODO: is this in scope?
// EST-131: convert to entity, since will vary by location
public enum PropertyEventType implements TitledEnum {

    DISRUPTION, 
    EXTENSION, 
    REFURBISHMENT, 
    EVENT, 
    TASK;

    public String title() {
        return StringUtils.enumTitle(this.toString());
    }

}
