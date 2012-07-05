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

import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;

/**
 * 
 * 
 * @version $Rev$ $Date$
 */
public class ContactMechanismType extends AbstractDomainObject {

	// {{ FullyQualifiedClassName (property)
	private String fullyQualifiedClassName;

	@MemberOrder(sequence = "1")
	public String getFullyQualifiedClassName() {
		return fullyQualifiedClassName;
	}

	public void setFullyQualifiedClassName(final String fullyQualifiedClassName) {
		this.fullyQualifiedClassName = fullyQualifiedClassName;
	}
	// }}

	@Hidden
	public ContactMechanism create() {
		try {
			ContactMechanism contactMechanism = newTransientInstance(contactMechanismSubclass());
			contactMechanism.setType(this);
			return contactMechanism;
		} catch (Exception ex) {
			throw new ApplicationException(ex);
		}
	}

	@SuppressWarnings("unchecked")
	private Class<? extends ContactMechanism> contactMechanismSubclass() {
		try {
			return (Class<? extends ContactMechanism>) Class
					.forName(getFullyQualifiedClassName());
		} catch (ClassNotFoundException e) {
			throw new ApplicationException("No such contact mechanism");
		}
	}

}
