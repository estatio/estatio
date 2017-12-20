package org.incode.module.communications.dom.impl.commchannel;

import java.util.Objects;

import javax.jdo.annotations.InheritanceStrategy;

import com.google.common.base.Predicate;

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
@javax.jdo.annotations.Discriminator("org.estatio.dom.communicationchannel.EmailAddress")
@javax.jdo.annotations.Indices({
        @javax.jdo.annotations.Index(
                name = "EmailAddress_emailAddress_IDX", members = { "emailAddress" })
})
@DomainObject(editing = Editing.DISABLED)
public class EmailAddress extends CommunicationChannel {


    public String title() {
        return getEmailAddress();
    }

    @javax.jdo.annotations.Column(allowsNull = "true", length = EmailType.Meta.MAX_LEN)
    @Property(optionality = Optionality.MANDATORY)
    @Getter @Setter
    private String emailAddress;

    public EmailAddress changeEmailAddress(
            @Parameter(
                    maxLength = EmailType.Meta.MAX_LEN,
                    regexPattern = EmailType.Meta.REGEX,
                    regexPatternReplacement = EmailType.Meta.REGEX_DESC
            )
            final String emailAddress) {
        setEmailAddress(emailAddress);

        return this;
    }

    public String default0ChangeEmailAddress() {
        return getEmailAddress();
    }

    // //////////////////////////////////////

    public static class Predicates {
        private Predicates(){}

        public static Predicate<EmailAddress> equalTo(
                final String emailAddress) {
            return new Predicate<EmailAddress>() {
                @Override
                public boolean apply(final EmailAddress input) {
                    return Objects.equals(emailAddress, input.getEmailAddress());
                }
            };
        }
    }

}