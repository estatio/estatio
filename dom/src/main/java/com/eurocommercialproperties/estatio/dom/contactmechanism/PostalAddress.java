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
package com.eurocommercialproperties.estatio.dom.contactmechanism;

import org.apache.isis.applib.annotation.MemberOrder;

import com.eurocommercialproperties.estatio.dom.geography.Country;
import com.eurocommercialproperties.estatio.dom.geography.State;

/**
 * 
 * 
 * @version $Rev$ $Date$
 */
public class PostalAddress extends ContactMechanism {

	// {{ Address1 (property)
	private String address1;

	@MemberOrder(sequence = "1")
	public String getAddress1() {
		return address1;
	}

	public void setAddress1(final String propertyName) {
		this.address1 = propertyName;
	}

	// }}

	// {{ Address2 (property)
	private String address2;

	@MemberOrder(sequence = "2")
	public String getAddress2() {
		return address2;
	}

	public void setAddress2(final String address2) {
		this.address2 = address2;
	}

	// }}

	// {{ City (property)
	private String city;

	@MemberOrder(sequence = "3")
	public String getCity() {
		return city;
	}

	public void setCity(final String city) {
		this.city = city;
	}

	// }}

	// {{ Country (property)
	private Country country;

	@MemberOrder(sequence = "4")
	public Country getCountry() {
		return country;
	}

	public void setCountry(final Country country) {
		this.country = country;
	}

	// }}

	// {{ Region (property)
	private State state;

	@MemberOrder(sequence = "5")
	public State getRegion() {
		return state;
	}

	public void setRegion(final State region) {
		this.state = region;
	}

	// }}


}
