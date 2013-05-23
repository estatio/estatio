package org.estatio.dom.communicationchannel;

import java.util.List;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.InheritanceStrategy;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ObjectType;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.util.TitleBuffer;

import org.estatio.dom.geography.Countries;
import org.estatio.dom.geography.Country;
import org.estatio.dom.geography.State;
import org.estatio.dom.geography.States;


@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@javax.jdo.annotations.Discriminator("POST") // required since subtypes are rolling-up
@ObjectType("POST")
public class PostalAddress extends CommunicationChannel {

    @Override
    public String getName() {
        TitleBuffer title = new TitleBuffer(getAddress1());
        title.append(", ", getAddress2());
        title.append(", ", getPostalCode());
        title.append(", ", getCity());
        return title.toString();
    }

    // {{ Address1 (attribute, title)
    private String address1;

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
    @javax.jdo.annotations.Column(name = "COUNTRY_ID")
    private Country country;

    @Optional
    @MemberOrder(sequence = "5")
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
    @javax.jdo.annotations.Column(name = "STATE_ID")
    private State state;

    @Optional
    @MemberOrder(sequence = "6")
    public State getState() {
        return state;
    }

    public void setState(final State state) {
        this.state = state;
    }

    public List<State> choicesState() {
        return states.findByCountry(getCountry());
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
