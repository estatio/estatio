package org.estatio.dom.leaseassignments;

import com.google.common.collect.Ordering;

import org.estatio.dom.utils.StringUtils;

public enum LeaseAssignmentType {
    
    NEW_TENANT, 
    RENEWAL, 
    TURNOVER;
    
    public String title() {
        return StringUtils.enumTitle(this.toString());
    }
    
    public static Ordering<LeaseAssignmentType> ORDERING_BY_TYPE = Ordering.<LeaseAssignmentType> natural().nullsFirst();

}