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

import java.util.Objects;
import javax.jdo.annotations.InheritanceStrategy;
import com.google.common.base.Predicate;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Title;
import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.RegexValidation;

@javax.jdo.annotations.PersistenceCapable
// identityType=IdentityType.DATASTORE inherited from superclass
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
// no @DatastoreIdentity nor @Version, since inherited from supertype
@javax.jdo.annotations.Indices({
        @javax.jdo.annotations.Index(
                name = "EmailAddress_emailAddress_IDX", members = { "emailAddress" })
})
@DomainObject(editing = Editing.DISABLED)
public class EmailAddress extends CommunicationChannel {

    // //////////////////////////////////////

    private String emailAddress;

    @javax.jdo.annotations.Column(allowsNull = "true", length = JdoColumnLength.EMAIL_ADDRESS)
    @Title
    @Property(optionality = Optionality.MANDATORY)
    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(final String address) {
        this.emailAddress = address;
    }

    public EmailAddress changeEmailAddress(
            final @ParameterLayout(named = "Email Address") @Parameter(regexPattern = RegexValidation.CommunicationChannel.EMAIL) String address) {
        setEmailAddress(address);

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