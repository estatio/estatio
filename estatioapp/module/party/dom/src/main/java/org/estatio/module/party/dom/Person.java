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
package org.estatio.module.party.dom;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.jdo.annotations.InheritanceStrategy;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.util.TitleBuffer;
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntityAdapter;

import org.isisaddons.module.security.dom.user.ApplicationUser;
import org.isisaddons.module.security.dom.user.ApplicationUserRepository;

import org.incode.module.base.dom.types.ProperNameType;

import org.estatio.dom.apptenancy.WithApplicationTenancyCountry;
import org.estatio.dom.apptenancy.WithApplicationTenancyPathPersisted;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        schema = "dbo" // Isis' ObjectSpecId inferred from @Discriminator
)
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByUsername", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.party.dom.Person "
                        + "WHERE username == :username"),
        @javax.jdo.annotations.Query(
                name = "findWithUsername", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.party.dom.Person "
                        + "WHERE username != null ")
})
@javax.jdo.annotations.Indices({
        @javax.jdo.annotations.Index(
                name = "Person_username_IDX", members = { "username" })
})
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Discriminator("org.estatio.dom.party.Person")
@DomainObject(
        editing = Editing.DISABLED,
        autoCompleteRepository = PersonRepository.class
)
@NoArgsConstructor
@XmlJavaTypeAdapter(PersistentEntityAdapter.class)
public class Person extends Party
        implements WithApplicationTenancyCountry, WithApplicationTenancyPathPersisted {

    @Builder
    public Person(
            String reference, String applicationTenancyPath, String name,
            @Nullable String initials,
            @Nullable String firstName,
            String lastName,
            @Nullable String username,
            PersonGenderType gender
    ) {
        setReference(reference);
        setApplicationTenancyPath(applicationTenancyPath);
        setName(name);
        setInitials(initials);
        setFirstName(firstName);
        setLastName(lastName);
        setUsername(username);
        setGender(gender);
    }

    @javax.jdo.annotations.Column(length = InitialsType.Meta.MAX_LEN)
    @MemberOrder(sequence = "2")
    @Getter @Setter
    private String initials;


    @Override
    public String disableName() {
        return "Cannot be updated directly; derived from first and last names";
    }


    @javax.jdo.annotations.Column(allowsNull = "true", length = ProperNameType.Meta.MAX_LEN)
    @Getter @Setter
    private String firstName;


    @javax.jdo.annotations.Column(allowsNull = "false", length = ProperNameType.Meta.MAX_LEN)
    @Getter @Setter
    private String lastName;


    @javax.jdo.annotations.Column(allowsNull = "true", length = ApplicationUser.MAX_LENGTH_USERNAME)
    @Getter @Setter
    private String username;

    /**
     * TODO: inline this mixin
     */
    @Mixin(method="act")
    public static class updateUsername {
        private final Person person;
        public updateUsername(final Person person) {
            this.person = person;
        }
        @Action(semantics = SemanticsOf.IDEMPOTENT)
        @ActionLayout(contributed= Contributed.AS_ACTION)
        public Person act(
                @Nullable final String username) {
            person.setUsername(username);
            return person;
        }
        public List<String> choices0Act() {

            // REVIEW: really naive code, ought to move this logic into a repository?
            final List<String> usernames =
                    Lists.newArrayList(
                            applicationUserRepository.allUsers().stream()
                                    .map(ApplicationUser::getUsername)
                                    .collect(Collectors.toList())
                    );

            final List<String> userNamesInUse =
                    personRepository.allPersons().stream()
                    .map(Person::getUsername)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            usernames.removeAll(userNamesInUse);
            return usernames;
        }

        public String default0Act() {
            return person.getUsername();
        }

        @Inject
        ApplicationUserRepository applicationUserRepository;
        @Inject
        PersonRepository personRepository;

    }


    @javax.jdo.annotations.Column(allowsNull = "false", length = PersonGenderType.Type.MAX_LEN)
    @MemberOrder(sequence = "1")
    @Getter @Setter
    private PersonGenderType gender;

    public PersonGenderType defaultGender() {
        return PersonGenderType.UNKNOWN;
    }


    public Person change(
            final PersonGenderType gender,
            final @Parameter(optionality = Optionality.OPTIONAL, regexPattern = InitialsType.Meta.REGEX, regexPatternReplacement = InitialsType.Meta.REGEX_DESCRIPTION) String initials,
            final String firstName,
            final String lastName) {
        setGender(gender);
        setInitials(initials);
        setFirstName(firstName);
        setLastName(lastName);
        TitleBuffer tb = new TitleBuffer();
        setName(tb.append(getLastName()).append(",", getFirstName()).toString());
        return this;
    }

    public PersonGenderType default0Change() {
        return getGender();
    }

    public String default1Change() {
        return getInitials();
    }

    public String default2Change() {
        return getFirstName();
    }

    public String default3Change() {
        return getLastName();
    }

    public String validateChange(
            final PersonGenderType gender,
            final String initials,
            final String firstName,
            final String lastName) {
        return Strings.isNullOrEmpty(firstName) || Strings.isNullOrEmpty(initials)
                ? "At least the first name or initials have to be filled in"
                : null;
    }


    // //////////////////////////////////////

    public static class InitialsType {

        private InitialsType() {}

        public static class Meta {

            public static final int MAX_LEN = 3;

            public static final String REGEX = "[A-Z]+";
            public static final String REGEX_DESCRIPTION = "Only letters are allowed";

            private Meta() {}

        }

    }

    public static class ReferenceType {

        private ReferenceType() {}

        public static class Meta {

            public static final String REGEX = "[A-Z,0-9,_,-,/]+";
            public static final String REGEX_DESCRIPTION = "Only letters, numbers and 3 symbols being: \"_\" , \"-\" and \"/\" are allowed";

            private Meta() {}

        }

    }
}