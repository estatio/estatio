package org.estatio.dom.lease;

import org.estatio.dom.Lockable;
import org.estatio.dom.utils.StringUtils;

public enum LeaseItemStatus implements Lockable {

    APPROVED,
    NEW;

    public String title() {
        return StringUtils.enumTitle(this.name());
    }

    @Override
    public boolean isUnlocked() {
        return this == NEW;
    }

    @Override
    public boolean isLocked() {
        return this == APPROVED;
    }

 }
