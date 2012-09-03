package com.eurocommercialproperties.estatio.dom.communicationchannel;

import java.util.List;

import javax.jdo.annotations.PersistenceCapable;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.util.TitleBuffer;

import com.eurocommercialproperties.estatio.dom.geography.Country;
import com.eurocommercialproperties.estatio.dom.geography.State;
import com.eurocommercialproperties.estatio.dom.geography.States;

@PersistenceCapable
public class PostalAddress extends CommunicationChannel {

    public String title(){
        TitleBuffer tb = new TitleBuffer(getAddress1());
        tb.append(", ", getPostalCode());
        tb.append(" ", getCity());
        tb.append(", ", getCountry().getName());
        return tb.toString();
    }
    
    
    // {{ Address1 (attribute, title)
    private String address1;

    @MemberOrder(sequence = "1")
    public String getAddress1() {
        return address1;
    }

    public void setAddress1(final String address1) {
        this.address1 = address1;
    }

    // }}

    // {{ Address2 (attribute)
    private String address2;

    @Optional
    @MemberOrder(sequence = "2")
    public String getAddress2() {
        return address2;
    }

    public void setAddress2(final String address2) {
        this.address2 = address2;
    }

    // }}

    // {{ PostalCode (attribute)
    private String postalCode;

    @MemberOrder(sequence = "3")
    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(final String postalCode) {
        this.postalCode = postalCode;
    }

    // }}

    // {{ City (attribute, title)
    private String city;

    @Title(sequence = "2", prepend = ", ")
    @MemberOrder(sequence = "4")
    public String getCity() {
        return city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    // }}

    // {{ Country (attribute)
    // annotation must be here rather than on the getter, 
    // because the additional business logic in the setter causes DN to dirty the object 
    // outside of xactn, breaking the fixture installation
    @javax.jdo.annotations.Column(name="COUNTRY_ID") 
    private Country country;

    @Optional
    @MemberOrder(sequence = "5")
    public Country getCountry() {
        return country;
    }

    public void setCountry(final Country country) {
        this.country = country;
        if (getState() != null && getState().getCountry() != country) {
            setState(null);
        }
    }
    // }}

    // {{ State (attribute)
    private State state;

    @javax.jdo.annotations.Column(name="STATE_ID")
    @Optional
    @MemberOrder(sequence = "6")
    public State getState() {
        return state;
    }

    public void setState(final State state) {
        this.state = state;
    }

    public List<State> choicesState() {
        return states.findByCountry(country);
    }

    // }}

    // {{ injected dependencies
    private States states;
    
    public void setStates(final States states) {
    	this.states = states;
    }
	// }}

}
