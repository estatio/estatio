package com.eurocommercialproperties.estatio.dom.communicationchannel;

import java.util.List;

import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ObjectType;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Title;

import com.eurocommercialproperties.estatio.dom.geography.Countries;
import com.eurocommercialproperties.estatio.dom.geography.Country;
import com.eurocommercialproperties.estatio.dom.geography.State;
import com.eurocommercialproperties.estatio.dom.geography.States;

@PersistenceCapable
@Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@Discriminator("POST")
// required since subtypes are rolling-up
@ObjectType("POST")
public class PostalAddress extends CommunicationChannel {

    // {{ Address1 (attribute, title)
    private String address1;

    // TODO: Title throws error in ui interface
    @Title(sequence = "1", append = ", ")
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

    @Title(sequence = "2", append = ", ")
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

    @Title(sequence = "3")
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
    // because the additional business logic in the setter causes DN to dirty
    // the object
    // outside of xactn, breaking the fixture installation
    // TODO: Country does not render as dropdown
    // TODO: When no country has been selected, the UI renders
    // "no objects returned" and an ok button. After that it's impossible to to
    // a searh again.
    private Country country;

    @Optional
    @MemberOrder(sequence = "5")
    @javax.jdo.annotations.Column(name = "COUNTRY_ID")
    public Country getCountry() {
        return country;
    }

    public void setCountry(final Country country) {
        this.country = country;
    }

    public void modifyCountry(final Country country) {
        setCountry(country);
        if (getState() != null && getState().getCountry() != country) {
            setState(null);
        }
    }

    public List<Country> choicesCountry() {
        return countries.allCountries();
    }

    public void clearCountry() {
        setCountry(null);
        setState(null);
    }

    // }}

    // {{ State (attribute)
    private State state;

    @javax.jdo.annotations.Column(name = "STATE_ID")
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

    private Countries countries;

    public void setCountries(Countries countries) {
        this.countries = countries;
    }

}
