package com.eurocommercialproperties.estatio.dom.party;

import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Title;

public abstract class Party extends AbstractDomainObject {

	// {{ Reference (property)
	private String reference;

	@Disabled
	@Title
	@MemberOrder(sequence = "1")
	public String getReference() {
		return reference;
	}

	public void setReference(final String reference) {
		this.reference = reference;
	}
	// }}

	
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
	
	
	// {{ Roles (Collection)
	private Set<PartyRole> roles = new LinkedHashSet<PartyRole>();

	@MemberOrder(sequence = "1")
	public Set<PartyRole> getRoles() {
		return roles;
	}

	public void setRoles(final Set<PartyRole> roles) {
		this.roles = roles;
	}
	// }}





}
