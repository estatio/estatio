/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
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

import javax.jdo.annotations.InheritanceStrategy;

import org.apache.isis.applib.annotation.Mandatory;
import org.apache.isis.applib.annotation.Title;

@javax.jdo.annotations.PersistenceCapable // identityType=IdentityType.DATASTORE inherited from superclass
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
//no @DatastoreIdentity nor @Version, since inherited from supertype
@javax.jdo.annotations.Indices({
    @javax.jdo.annotations.Index(
            name="PhoneNumber_phoneNumber_IDX", members={"phoneNumber"})
})
@javax.jdo.annotations.Queries({
    @javax.jdo.annotations.Query(
            name = "findByPhoneNumber", language = "JDOQL", 
            value = "SELECT "
                    + "FROM org.estatio.dom.communicationchannel.PhoneOrFaxNumber " 
                    + "WHERE owner == :owner "
                    + "&& phoneNumber == :phoneNumber")
})
public class PhoneOrFaxNumber extends CommunicationChannel {


    private String phoneNumber;

    @javax.jdo.annotations.Column(allowsNull="true")
    @Title(prepend="Phone")
    @Mandatory
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(final String number) {
        this.phoneNumber = number;
    }

}
