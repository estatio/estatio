/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
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
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.Mandatory;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.util.TitleBuffer;

import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.geography.Country;
import org.estatio.dom.geography.State;
import org.estatio.dom.geography.States;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@javax.jdo.annotations.Indices({
        @javax.jdo.annotations.Index(
                name = "PostalAddress_main_idx",
                members = { "owner", "address1", "postalCode", "city", "country" })
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
@Immutable
public class PostalAddress extends CommunicationChannel {

    public String title() {
        return new TitleBuffer()
                .append(getAddress1())
                .append(", ", getCity())
                .append(" ", getPostalCode())
                .append(" ", isLegal() ? "[Legal]" : "")
                .append(getPurpose() == null ? "" : "[" + getPurpose().title() + "]")
                .toString();
    }

    private String address1;

    // //////////////////////////////////////
    
    @javax.jdo.annotations.Column(allowsNull = "true", length = JdoColumnLength.PostalAddress.ADDRESS_LINE)
    @Mandatory
    @Named("Address line 1")
    public String getAddress1() {
        return address1;
    }

    public void setAddress1(final String address1) {
        this.address1 = address1;
    }

    // //////////////////////////////////////

    private String address2;

    @javax.jdo.annotations.Column(allowsNull = "true", length = JdoColumnLength.PostalAddress.ADDRESS_LINE)
    @Named("Address line 2")
    public String getAddress2() {
        return address2;
    }

    public void setAddress2(final String address2) {
        this.address2 = address2;
    }

    // //////////////////////////////////////

    private String address3;

    @javax.jdo.annotations.Column(allowsNull = "true", length = JdoColumnLength.PostalAddress.ADDRESS_LINE)
    @Optional
    @Named("Address line 3")
    public String getAddress3() {
        return address3;
    }

    public void setAddress3(final String address3) {
        this.address3 = address3;
    }

    // //////////////////////////////////////

    private String postalCode;

    @javax.jdo.annotations.Column(allowsNull = "true", length = JdoColumnLength.PostalAddress.POSTAL_CODE)
    @Mandatory
    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(final String postalCode) {
        this.postalCode = postalCode;
    }

    // //////////////////////////////////////

    private String city;

    @javax.jdo.annotations.Column(allowsNull = "true", length = JdoColumnLength.PROPER_NAME)
    @Mandatory
    public String getCity() {
        return city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    // //////////////////////////////////////

    private Country country;

    // optional only because of superclass inheritance strategy=SUPERCLASS_TABLE
    @javax.jdo.annotations.Column(name = "countryId", allowsNull = "true")
    @Mandatory
    @Disabled(reason = "Update using action")
    public Country getCountry() {
        return country;
    }

    public void setCountry(final Country country) {
        this.country = country;
    }

    // //////////////////////////////////////

    private State state;

    // optional only because of superclass inheritance strategy=SUPERCLASS_TABLE
    @javax.jdo.annotations.Column(name = "stateId", allowsNull = "true")
    @Disabled(reason = "Update using action")
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
        return null;
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

    public PostalAddress changePostalAddress(
            final @Named("Address Line 1") String address1,
            final @Named("Address Line 2") @Optional String address2,
            final @Named("Address Line 3") @Optional String address3,
            final @Named("City") String city,
            final @Named("Postal Code") String postalCode) {
        setAddress1(address1);
        setAddress2(address2);
        setAddress3(address3);
        setCity(city);
        setPostalCode(postalCode);

        return this;
    }

    public String default0ChangePostalAddress() {
        return getAddress1();
    }

    public String default1ChangePostalAddress() {
        return getAddress2();
    }

    public String default2ChangePostalAddress() {
        return getAddress3();
    }

    public String default3ChangePostalAddress() {
        return getCity();
    }

    public String default4ChangePostalAddress() {
        return getPostalCode();
    }
}
