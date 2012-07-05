package com.eurocommercialproperties.estatio.dom.contactmechanism;

import java.util.List;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.QueryOnly;

public interface ContactMechanismTypes {

	@QueryOnly
	@MemberOrder(sequence = "1")
	public ContactMechanismType newContactMechanismType(
			String fullyQualifiedClassName);

	List<ContactMechanismType> allInstances();

}
