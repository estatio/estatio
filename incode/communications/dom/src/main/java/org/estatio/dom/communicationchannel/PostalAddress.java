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

import javax.inject.Inject;
import javax.jdo.annotations.InheritanceStrategy;

import com.google.common.base.Predicate;

import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.util.TitleBuffer;

import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.geography.Country;
import org.estatio.dom.geography.State;
import org.estatio.dom.geography.StateRepository;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@javax.jdo.annotations.Discriminator("org.estatio.dom.communicationchannel.PostalAddress")
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

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "true", length = JdoColumnLength.PostalAddress.ADDRESS_LINE)
    @Property(optionality = Optionality.MANDATORY)
    @PropertyLayout(named = "Address line 1")
    @Getter @Setter
    private String address1;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "true", length = JdoColumnLength.PostalAddress.ADDRESS_LINE)
    @PropertyLayout(named = "Address line 2")
    @Getter @Setter
    private String address2;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "true", length = JdoColumnLength.PostalAddress.ADDRESS_LINE)
    @Property(optionality = Optionality.OPTIONAL)
    @PropertyLayout(named = "Address line 3")
    @Getter @Setter
    private String address3;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "true", length = JdoColumnLength.PostalAddress.POSTAL_CODE)
    @Property(optionality = Optionality.MANDATORY)
    @Getter @Setter
    private String postalCode;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "true", length = JdoColumnLength.PROPER_NAME)
    @Property(optionality = Optionality.MANDATORY)
    @Getter @Setter
    private String city;

    // //////////////////////////////////////

    // optional only because of superclass inheritance strategy=SUPERCLASS_TABLE
    @javax.jdo.annotations.Column(name = "countryId", allowsNull = "true")
    @Property(optionality = Optionality.MANDATORY, editing = Editing.DISABLED, editingDisabledReason = "Update using action")
    @Getter @Setter
    private Country country;

    // //////////////////////////////////////

    // optional only because of superclass inheritance strategy=SUPERCLASS_TABLE
    @javax.jdo.annotations.Column(name = "stateId", allowsNull = "true")
    @Property(editing = Editing.DISABLED, editingDisabledReason = "Update using Action")
    @Getter @Setter
    private State state;

    public List<State> choicesState() {
        return stateRepository.findStatesByCountry(getCountry());
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
        return stateRepository.findStatesByCountry(country);
    }

    // //////////////////////////////////////

    public PostalAddress changePostalAddress(
            final String addressLine1,
            final @Parameter(optionality = Optionality.OPTIONAL) String addressLine2,
            final @Parameter(optionality = Optionality.OPTIONAL) String addressLine3,
            final String city,
            final String postalCode) {
        setAddress1(addressLine1);
        setAddress2(addressLine2);
        setAddress3(addressLine3);
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

        private Predicates() {
        }

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

    // //////////////////////////////////////

    @Inject
    private StateRepository stateRepository;

}
