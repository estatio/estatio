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
package org.estatio.dom.party;

import java.util.List;

import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.annotation.ActionSemantics.Of;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.RegexValidation;

@DomainService(menuOrder = "20", repositoryFor = Person.class)
public class Persons extends EstatioDomainService<Person> {

    public Persons() {
        super(Persons.class, Person.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(name = "Parties", sequence = "1")
    public Person newPerson(
            final @Named("reference") @Optional @RegEx(validation=RegexValidation.Person.REFERENCE) String reference,
            final @Named("initials") @Optional @RegEx(validation=RegexValidation.Person.INITIALS) String initials,
            final @Named("First name") @Optional String firstName,
            final @Named("Last name") String lastName,
            final @Named("Gender") PersonGenderType gender) {
        final Person person = newTransientInstance(Person.class);
        person.setReference(reference);
        person.change(gender, initials, firstName, lastName);
        persist(person);
        return person;
    }

    // //////////////////////////////////////

    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(name = "Parties", sequence = "99.2")
    public List<Person> allPersons() {
        return allInstances();
    }

}
