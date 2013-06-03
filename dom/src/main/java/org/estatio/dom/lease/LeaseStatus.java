package org.estatio.dom.lease;

import com.google.common.collect.Ordering;

import org.estatio.dom.utils.StringUtils;

public enum LeaseStatus {

    APPROVED, NEW;

    public String title() {
        return StringUtils.enumTitle(this.name());
    }

    public static Ordering<LeaseStatus> ORDERING_BY_TYPE = 
            Ordering.<LeaseStatus> natural().nullsFirst();

 }
