package org.estatio.dom.lease;

import com.google.common.collect.Ordering;

import org.estatio.dom.utils.StringUtils;

public enum LeaseItemStatus {

    APPROVED,
    NEW;

    public String title() {
        return StringUtils.enumTitle(this.name());
    }

    public static Ordering<LeaseItemStatus> ORDERING_BY_TYPE = 
            Ordering.<LeaseItemStatus> natural().nullsFirst();

 }
