package org.incode.module.communications.dom.impl.commchannel;

import java.util.Objects;

import javax.jdo.annotations.InheritanceStrategy;

import com.google.common.base.Predicate;

import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Property;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        schema = "IncodeCommunications" // Isis' ObjectSpecId inferred from @Discriminator
)
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@javax.jdo.annotations.Discriminator("org.estatio.dom.communicationchannel.PhoneOrFaxNumber")
@javax.jdo.annotations.Indices({
        @javax.jdo.annotations.Index(
                name = "PhoneNumber_phoneNumber_IDX", members = { "phoneNumber" })
})
@DomainObject(editing = Editing.DISABLED)
public class PhoneOrFaxNumber extends CommunicationChannel {

    public String title() {
        return getPhoneNumber();
    }

    @javax.jdo.annotations.Column(allowsNull = "true", length = PhoneNumberType.Meta.MAX_LEN)
    @Property(optionality = Optionality.MANDATORY)
    @Getter @Setter
    private String phoneNumber;

    @ActionLayout(named = "Change Number")
    public PhoneOrFaxNumber changePhoneOrFaxNumber(
            @Parameter(
                    regexPattern = PhoneNumberType.Meta.REGEX,
                    regexPatternReplacement = PhoneNumberType.Meta.REGEX_DESC
            )
            final String phoneNumber) {
        setPhoneNumber(phoneNumber);

        return this;
    }

    public String default0ChangePhoneOrFaxNumber() {
        return getPhoneNumber();
    }

    // //////////////////////////////////////

    public static class Predicates {
        private Predicates(){}

        public static Predicate<PhoneOrFaxNumber> equalTo(
                final String phoneNumber,
                final CommunicationChannelType communicationChannelType) {
            return new Predicate<PhoneOrFaxNumber>() {
                @Override
                public boolean apply(final PhoneOrFaxNumber input) {
                    return  Objects.equals(phoneNumber, input.getPhoneNumber()) &&
                            Objects.equals(communicationChannelType, input.getType());
                }
            };
        }
    }


}
