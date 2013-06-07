package org.estatio.dom.leaseassignments;

import org.estatio.dom.utils.StringUtils;

public enum LeaseAssignmentType {
    
    NEW_TENANT, 
    RENEWAL, 
    TURNOVER;
    
    public String title() {
        return StringUtils.enumTitle(this.toString());
    }
    

}