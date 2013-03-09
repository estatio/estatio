/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.estatio.dom.party;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Title;

@javax.jdo.annotations.PersistenceCapable
public class Person extends Party {

    // {{ Initials (attribute, title)
    private String initials;

    @Title(prepend=" - ", sequence="3")
    @MemberOrder(sequence = "3")
    @Optional
    public String getInitials() {
        return initials;
    }

    public void setInitials(final String initials) {
        this.initials = initials;
    }

    // }}

    // {{ FirstName (attribute, title)
    private String firstName;

    @Title(prepend=", ", sequence="2")
    @MemberOrder(sequence = "1")
    @Optional
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }
    // }}

    // {{ LastName (attribute, title)
    private String lastName;

    @Title(sequence="1")
    @MemberOrder(sequence = "2")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }
    // }}

    // {{ Gender (attribute)
    private PersonGenderType gender;

    @MemberOrder(sequence = "4")
    //@Optional
    public PersonGenderType getGender() {
        return gender;
    }

    public void setGender(final PersonGenderType gender) {
        this.gender = gender;
    }
    
//    public PersonGenderType defaultGender() {
//        return PersonGenderType.UNKNOWN;
//    }
    // }}

    // {{ validate
    public String validate() {
        return getFirstName().isEmpty() || getLastName().isEmpty() ? "At least the first name or initials have to be filled in" : null;
    }
    // }}

}
