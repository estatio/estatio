package com.eurocommercialproperties.estatio.dom.party;

import javax.jdo.annotations.InheritanceStrategy;

import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ObjectType;
import org.apache.isis.applib.annotation.Title;

@javax.jdo.annotations.PersistenceCapable(schema="party")
@javax.jdo.annotations.Inheritance(strategy=InheritanceStrategy.NEW_TABLE)
@ObjectType("OWNR")
public class Owner extends Party {

	
    // {{ Reference (attribute, overridden for annotations)
    @Override
    @Disabled
    @Title(sequence="1")  // REVIEW: this is an experiment
    @MemberOrder(sequence = "1")
    public String getReference() {
    	return super.getReference();
    }
    // }}
    
    // {{ Name (attribute)
    private String name;

    @Title(prepend="-", sequence="2")
    @MemberOrder(sequence = "2")
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
    // }}

}
