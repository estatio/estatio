package com.eurocommercialproperties.estatio.dom.asset;

import com.eurocommercialproperties.estatio.dom.utils.StringUtils;

public enum UnitType {

    BOUTIQUE,      
    STORAGE,        
    MEDIUM,         
    HYPERMARKET,    
    EXTERNAL,       
    DEHOR,          
    CINEMA,         
    SERVICES,       
    VIRTUAL;        

    public String title() {
        return StringUtils.enumTitle(name());
    }

}
