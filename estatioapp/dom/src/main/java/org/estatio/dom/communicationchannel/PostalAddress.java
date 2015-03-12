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
import java.util.Objects;

import javax.jdo.annotations.InheritanceStrategy;

import com.google.common.base.Predicate;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
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
                members = { "address1", "postalCode", "city", "country" })
})
@DomainObject(editing = Editing.DISABLED)
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
    @Property(optionality = Optionality.MANDATORY)
    @PropertyLayout(named = "Address line 1")
    public String getAddress1() {
        return address1;
    }

    public void setAddress1(final String address1) {
        this.address1 = address1;
    }

    // //////////////////////////////////////

    private String address2;

    @javax.jdo.annotations.Column(allowsNull = "true", length = JdoColumnLength.PostalAddress.ADDRESS_LINE)
    @PropertyLayout(named = "Address line 2")
    public String getAddress2() {
        return address2;
    }

    public void setAddress2(final String address2) {
        this.address2 = address2;
    }

    // //////////////////////////////////////

    private String address3;

    @javax.jdo.annotations.Column(allowsNull = "true", length = JdoColumnLength.PostalAddress.ADDRESS_LINE)
    @Property(optionality = Optionality.OPTIONAL)
    @PropertyLayout(named = "Address line 3")
    public String getAddress3() {
        return address3;
    }

    public void setAddress3(final String address3) {
        this.address3 = address3;
    }

    // //////////////////////////////////////

    private String postalCode;

    @javax.jdo.annotations.Column(allowsNull = "true", length = JdoColumnLength.PostalAddress.POSTAL_CODE)
    @Property(optionality = Optionality.MANDATORY)
    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(final String postalCode) {
        this.postalCode = postalCode;
    }

    // //////////////////////////////////////

    private String city;

    @javax.jdo.annotations.Column(allowsNull = "true", length = JdoColumnLength.PROPER_NAME)
    @Property(optionality = Optionality.MANDATORY)
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
    @Property(optionality = Optionality.MANDATORY, editing = Editing.DISABLED, editingDisabledReason = "Update using action")
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
    @Property(editing = Editing.DISABLED, editingDisabledReason = "Update using Action")
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

    @ActionLayout(named = "Update")
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
            final @ParameterLayout(named = "Address Line 1") String address1,
            final @ParameterLayout(named = "Address Line 2") @Parameter(optionality = Optionality.OPTIONAL) String address2,
            final @ParameterLayout(named = "Address Line 3") @Parameter(optionality = Optionality.OPTIONAL) String address3,
            final @ParameterLayout(named = "City") String city,
            final @ParameterLayout(named = "Postal Code") String postalCode) {
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

    // //////////////////////////////////////

    public static class Predicates {
        private Predicates(){}

        public static Predicate<PostalAddress> equalTo(
                final String address1,
                final String postalCode,
                final String city,
                final Country country) {
            return new Predicate<PostalAddress>() {
                @Override
                public boolean apply(final PostalAddress input) {
                    return Objects.equals(address1, input.getAddress1()) &&
                            Objects.equals(postalCode, input.getPostalCode()) &&
                            Objects.equals(city, input.getCity()) &&
                            Objects.equals(country, input.getCountry());
                }
            };
        }
    }

}
