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
package com.eurocommercialproperties.estatio.dom.communicationchannel;

import java.util.List;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Title;

import com.eurocommercialproperties.estatio.dom.geography.Country;
import com.eurocommercialproperties.estatio.dom.geography.State;
import com.eurocommercialproperties.estatio.dom.geography.States;

/**
 * 
 * 
 * @version $Rev$ $Date$
 */
public class PostalAddress extends CommunicationChannel {

    // {{ Address1 (property)
    private String address1;

    @Title(sequence = "1")
    @MemberOrder(sequence = "1")
    public String getAddress1() {
        return address1;
    }

    public void setAddress1(final String address1) {
        this.address1 = address1;
    }

    // }}

    // {{ Address2 (property)
    private String address2;

    @Optional
    @MemberOrder(sequence = "2")
    public String getAddress2() {
        return address2;
    }

    public void setAddress2(final String address2) {
        this.address2 = address2;
    }

    // }}

    // {{ PostalCode (property)
    private String postalCode;

    @MemberOrder(sequence = "3")
    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(final String postalCode) {
        this.postalCode = postalCode;
    }

    // }}

    // {{ City (property)
    private String city;

    @Title(sequence = "2", prepend = ", ")
    @MemberOrder(sequence = "4")
    public String getCity() {
        return city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    // }}

    // {{ Country (property)
    private Country country;

    @Optional
    @MemberOrder(sequence = "5")
    public Country getCountry() {
        return country;
    }

    public void setCountry(final Country country) {
        this.country = country;
        if (getState() != null && getState().getCountry() != country) {
            setState(null);
        }
    }

    // }}

    // {{ State (property)
    private State state;

    @Optional
    @MemberOrder(sequence = "6")
    public State getState() {
        return state;
    }

    public void setState(final State state) {
        this.state = state;
    }

    public List<State> choicesState() {
        return states.findByCountry(country);
    }

    // }}
    

    private States states;

    public void setStateRepository(final States states) {
        this.states = states;
    }

}
