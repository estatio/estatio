package org.incode.module.communications.dom.impl.commchannel;

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
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.util.TitleBuffer;

import org.incode.module.base.dom.types.ProperNameType;
import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.dom.impl.State;
import org.incode.module.country.dom.impl.StateRepository;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        schema = "IncodeCommunications" // Isis' ObjectSpecId inferred from @Discriminator
)
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@javax.jdo.annotations.Discriminator("org.estatio.dom.communicationchannel.PostalAddress")
@javax.jdo.annotations.Indices({
        @javax.jdo.annotations.Index(
                name = "PostalAddress_main_idx",
                members = { "address1", "postalCode", "city", "country" })
})
@DomainObject(editing = Editing.DISABLED)
public class PostalAddress extends CommunicationChannel {


    public static final class AddressLineType {
        private AddressLineType() {
        }

        /**
         * @deprecated - use {@link Meta#MAX_LEN} instead
         */
        @Deprecated
        public static final int MAX_LEN = Meta.MAX_LEN;

        public static class Meta {
            private Meta() { }

            public static final int MAX_LEN = 100;
        }
    }

    @Programmatic
    public String asAddressLabel() {
        final StringBuffer buf = new StringBuffer();
        append(buf, getAddress1());
        append(buf, getAddress2());
        append(buf, getAddress3());
        append(buf, getCity());
        append(buf, getPostalCode());
        append(buf, getState() != null ? getState().getName() : null);
        append(buf, getCountry() != null ? getCountry().getName() : null);
        return buf.toString();
    }

    private void append(final StringBuffer buf, final String str) {
        if (str == null) {
            return;
        }
        if(buf.length() > 0) {
            buf.append("\n");
        }
        buf.append(str);
    }

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

    @javax.jdo.annotations.Column(allowsNull = "true", length = PostalAddress.AddressLineType.Meta.MAX_LEN)
    @Property(optionality = Optionality.MANDATORY)
    @PropertyLayout(named = "Address line 1")
    @Getter @Setter
    private String address1;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "true", length = PostalAddress.AddressLineType.Meta.MAX_LEN)
    @PropertyLayout(named = "Address line 2")
    @Getter @Setter
    private String address2;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "true", length = PostalAddress.AddressLineType.Meta.MAX_LEN)
    @Property(optionality = Optionality.OPTIONAL)
    @PropertyLayout(named = "Address line 3")
    @Getter @Setter
    private String address3;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "true", length = PostalAddress.PostalCodeType.Meta.MAX_LEN)
    @Property(optionality = Optionality.MANDATORY)
    @Getter @Setter
    private String postalCode;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "true", length = ProperNameType.Meta.MAX_LEN)
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

    public static final class PostalCodeType {
        private PostalCodeType() {
        }

        /**
         * @deprecated - use {@link Meta#MAX_LEN} instead
         */
        @Deprecated
        public static final int MAX_LEN = Meta.MAX_LEN;

        public static class Meta {
            private Meta() {
            }

            public static final int MAX_LEN = 12;
        }

    }

    @ActionLayout(named = "Update")
    @MemberOrder(sequence = "1")
    public PostalAddress updateCountryAndState(
            final Country country,
            final State state) {
        setCountry(country);
        setState(state);
        return this;
    }

    public String disableUpdateCountryAndState() {
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

        public static Predicate<PostalAddress> equalTo(
                final String address1,
                final String address2,
                final String address3,
                final String postalCode,
                final String city,
                final Country country) {
            return new Predicate<PostalAddress>() {
                @Override
                public boolean apply(final PostalAddress input) {
                    return Objects.equals(address1, input.getAddress1()) &&
                            Objects.equals(address2, input.getAddress2()) &&
                            Objects.equals(address3, input.getAddress3()) &&
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
