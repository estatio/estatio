package com.eurocommercialproperties.estatio.dom.party;

import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Title;

public abstract class Party extends AbstractDomainObject {

	// {{ Name (property)
	private String name;

	@Disabled
	@Title
	@MemberOrder(sequence = "2")
	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	// }}



}
