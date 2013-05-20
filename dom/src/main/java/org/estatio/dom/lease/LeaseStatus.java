package org.estatio.dom.lease;

import org.estatio.dom.utils.StringUtils;

public enum LeaseStatus {

    APPROVED, NEW;

    public String title() {
        return StringUtils.enumTitle(this.name());
    }

 }
