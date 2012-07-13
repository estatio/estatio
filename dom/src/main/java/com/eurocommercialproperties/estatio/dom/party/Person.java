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
package com.eurocommercialproperties.estatio.dom.party;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.util.TitleBuffer;

/**
 * 
 * 
 * @version $Rev$ $Date$
 */
public class Person extends Party {

    public String title() {
        TitleBuffer tb = new TitleBuffer(getLastName());
        tb.append(", ", getFirstName()).append(" - ", getInitials());
        return tb.toString();
    }

    // {{ initials (property)
    private String initials;

    @MemberOrder(sequence = "1")
    @Optional
    public String getInitials() {
        return initials;
    }

    public void setInitials(final String initials) {
        this.initials = initials;
    }

    // }}

    // {{ FirstName (property)
    private String firstName;

    @MemberOrder(sequence = "1")
    @Optional
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    // }}

    // {{ LastName (property)
    private String lastName;

    @MemberOrder(sequence = "1")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    // }}

    // {{ Gender (property)
    private PersonGenderType gender;

    @MemberOrder(sequence = "1")
    public PersonGenderType getGender() {
        return gender;
    }

    public void setGender(final PersonGenderType gender) {
        this.gender = gender;
    }

    // }}

    public String validate() {

        if (getFirstName().isEmpty() || getLastName().isEmpty()) {
            return "At least the first name or initials have to be filled in";
        } else {
            return null;
        }

    }

}
