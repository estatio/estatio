/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.dom.communicationchannel;

import java.util.List;

import javax.jdo.annotations.InheritanceStrategy;

import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Mandatory;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Title;

import org.estatio.dom.geography.Country;
import org.estatio.dom.geography.State;
import org.estatio.dom.geography.States;

@javax.jdo.annotations.PersistenceCapable // identityType=IdentityType.DATASTORE inherited from superclass
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
//no @DatastoreIdentity nor @Version, since inherited from supertype
@javax.jdo.annotations.Indices({
    @javax.jdo.annotations.Index(
            name="PostalAddress_main_idx", 
            members={"owner", "address1","postalCode","city","country"})
})
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByAddress", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.communicationchannel.CommunicationChannel "
                        + "WHERE owner == :owner"
                        + "&& address1 == :address1 "
                        + "&& postalCode == :postalCode "
                        + "&& city == :city "
                        + "&& country == :country ")
})
public class PostalAddress extends CommunicationChannel {

    private String address1;

    @javax.jdo.annotations.Column(allowsNull="true")
    @Mandatory
    @Title(sequence = "1", append = ", ")
    @Named("Address Line 1")
    public String getAddress1() {
        return address1;
    }

    public void setAddress1(final String address1) {
        this.address1 = address1;
    }

    // //////////////////////////////////////

    private String address2;

    @javax.jdo.annotations.Column(allowsNull="true")
    @Title(sequence = "2", append = ", ")
    @Optional
    @Named("Address Line 2")
    public String getAddress2() {
        return address2;
    }

    public void setAddress2(final String address2) {
        this.address2 = address2;
    }

    // //////////////////////////////////////

    private String postalCode;

    @javax.jdo.annotations.Column(allowsNull="true")
    @Mandatory
    @Title(sequence = "3", append = ", ")
    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(final String postalCode) {
        this.postalCode = postalCode;
    }

    // //////////////////////////////////////

    private String city;

    @javax.jdo.annotations.Column(allowsNull="true")
    @Mandatory
    @Title(sequence = "4")
    public String getCity() {
        return city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    // //////////////////////////////////////

    private Country country;

    // optional only because of superclass inheritance strategy=SUPERCLASS_TABLE
    @javax.jdo.annotations.Column(name = "countryId", allowsNull="true")
    @Mandatory
    @Disabled(reason="Update using action") 
    public Country getCountry() {
        return country;
    }

    public void setCountry(final Country country) {
        this.country = country;
    }

    // //////////////////////////////////////

    private State state;

    // optional only because of superclass inheritance strategy=SUPERCLASS_TABLE
    @javax.jdo.annotations.Column(name = "stateId", allowsNull="true")
    @Mandatory
    @Disabled(reason="Update using action") 
    public State getState() {
        return state;
    }

    public void setState(final State state) {
        this.state = state;
    }

    public List<State> choicesState() {
        return states.findStatesByCountry(getCountry());
    }

    // //////////////////////////////////////

    @Named("Update")
    @MemberOrder(sequence = "1")
    public PostalAddress updateCountryAndState(
            final Country country, 
            final State state) {
        setCountry(country);
        setState(state);
        return this;
    }
    public String disableUpdateCountryAndState(
            final Country country, 
            final State state) {
        return isLocked() ? "Cannot modify when locked": null;
    }

    public Country default0UpdateCountryAndState() {
        return getCountry();
    }
    public State default1UpdateCountryAndState() {
        return getState();
    }
    public List<State> choices1UpdateCountryAndState(
            final Country country) {
        return states.findStatesByCountry(country);
    }

    // //////////////////////////////////////

    private States states;

    public final void injectStates(final States states) {
        this.states = states;
    }

}
