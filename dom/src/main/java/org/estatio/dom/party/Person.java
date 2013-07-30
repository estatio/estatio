/**
 * or more
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

import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.util.TitleBuffer;

@javax.jdo.annotations.PersistenceCapable
public class Person extends Party {

    
    private String initials;

    @Optional
    public String getInitials() {
        return initials;
    }

    public void setInitials(final String initials) {
        this.initials = initials;
    }


    // //////////////////////////////////////

    @Override
    public String disableName() {
        return "Cannot be updated directly; derived from first and last names";
    }

    // //////////////////////////////////////

    
    private String firstName;

    @Optional
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    // //////////////////////////////////////

    private String lastName;

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }


    // //////////////////////////////////////

    private PersonGenderType gender;

    public PersonGenderType getGender() {
        return gender;
    }

    public void setGender(final PersonGenderType gender) {
        this.gender = gender;
    }

    public PersonGenderType defaultGender() {
        return PersonGenderType.UNKNOWN;
    }

    // //////////////////////////////////////

    public String validate() {
        return getFirstName().isEmpty() || getInitials().isEmpty() ? "At least the first name or initials have to be filled in" : null;
    }

    public void updating() {
        TitleBuffer tb = new TitleBuffer();
        setName(tb.append(getLastName()).append(",", getFirstName()).toString());
    }


}
