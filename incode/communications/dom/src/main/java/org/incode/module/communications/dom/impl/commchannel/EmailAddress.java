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

@javax.jdo.annotations.PersistenceCapable
// identityType=IdentityType.DATASTORE inherited from superclass
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@javax.jdo.annotations.Discriminator("org.estatio.dom.communicationchannel.EmailAddress")
// no @DatastoreIdentity nor @Version, since inherited from supertype
@javax.jdo.annotations.Indices({
        @javax.jdo.annotations.Index(
                name = "EmailAddress_emailAddress_IDX", members = { "emailAddress" })
})
@DomainObject(editing = Editing.DISABLED)
public class EmailAddress extends CommunicationChannel {


    public String title() {
        return getEmailAddress();
    }

    @javax.jdo.annotations.Column(allowsNull = "true", length = EmailType.MAX_LEN)
    @Property(optionality = Optionality.MANDATORY)
    @Getter @Setter
    private String emailAddress;

    public EmailAddress changeEmailAddress(
            @Parameter(
                    maxLength = EmailType.MAX_LEN,
                    regexPattern = EmailType.REGEX,
                    regexPatternReplacement = EmailType.REGEX_DESC
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