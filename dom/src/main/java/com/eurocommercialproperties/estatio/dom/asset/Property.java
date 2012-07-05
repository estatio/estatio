package com.eurocommercialproperties.estatio.dom.asset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.value.Date;

import com.eurocommercialproperties.estatio.dom.contactmechanism.ContactMechanism;
import com.eurocommercialproperties.estatio.dom.contactmechanism.ContactMechanismType;
import com.eurocommercialproperties.estatio.dom.contactmechanism.ContactMechanismTypes;
import com.eurocommercialproperties.estatio.dom.party.Owner;

public class Property extends AbstractDomainObject {

	// {{ Code (property)
	private String reference;

	@Disabled
	@MemberOrder(sequence = "1")
	public String getReference() {
		return reference;
	}

	public void setReference(final String code) {
		this.reference = code;
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

	// {{ Type (property)
	private PropertyType type;

	@Disabled
	@MemberOrder(sequence = "3")
	public PropertyType getType() {
		return type;
	}

	public void setType(final PropertyType type) {
		this.type = type;
	}

	public List<PropertyType> choicesType() {
		return Arrays.asList(PropertyType.values());
	}

	// }}

	// {{ OpeningDate (property)
	private Date openingDate;

	@MemberOrder(sequence = "1")
	public Date getOpeningDate() {
		return openingDate;
	}

	public void setOpeningDate(final Date openingDate) {
		this.openingDate = openingDate;
	}

	// }}

	// {{ AcquireDate (property)
	private Date acquireDate;

	@MemberOrder(sequence = "4")
	public Date getAcquireDate() {
		return acquireDate;
	}

	public void setAcquireDate(final Date acquireDate) {
		this.acquireDate = acquireDate;
	}

	// }}

	// {{ Disposal Date (property)
	private Date disposalDate;

	@MemberOrder(sequence = "5")
	@Optional
	public Date getDisposalDate() {
		return disposalDate;
	}

	public void setDisposalDate(final Date disposalDate) {
		this.disposalDate = disposalDate;
	}

	// }}

	// {{ Area (property)
	private Double area;

	@MemberOrder(sequence = "6")
	public Double getArea() {
		return area;
	}

	public void setArea(final Double area) {
		this.area = area;
	}

	// }}

	// {{ AreaOfUnits (property)

	@MemberOrder(sequence = "7")
	public Double getAreaOfUnits() {
		double area = 0;
		for (Unit unit : getUnits()) {
			area += unit.getArea();
		}
		return area;
	}

	// }}

	// {{ ContactMechanisms (Collection)
	private List<ContactMechanism> contactMechanisms = new ArrayList<ContactMechanism>();

	@MemberOrder(sequence = "8")
	public List<ContactMechanism> getContactMechanisms() {
		return contactMechanisms;
	}

	public void setContactMechanisms(
			final List<ContactMechanism> contactMechanisms) {
		this.contactMechanisms = contactMechanisms;
	}

	public ContactMechanism newContactMechanism(final ContactMechanismType contactMechanismType){
		ContactMechanism contactMechanism = contactMechanismType.create();
		contactMechanisms.add(contactMechanism);
		return contactMechanism;
	}
	
	// }}
	

	// {{ Units (Collection)
	private List<Unit> units = new ArrayList<Unit>();

	@Disabled
	@MemberOrder(sequence = "10")
	public List<Unit> getUnits() {
		return units;
	}

	public void setUnits(final List<Unit> units) {
		this.units = units;
	}

	// }}

	// {{ Owners (Collection)
	private List<Owner> collectionName = new ArrayList<Owner>();

	@Disabled
	@MemberOrder(sequence = "1")
	public List<Owner> getOwners() {
		return collectionName;
	}

	public void setOwners(final List<Owner> collectionName) {
		this.collectionName = collectionName;
	}

	// }}

	// {{ newUnit (action)
	@MemberOrder(name = "Units", sequence = "1")
	public Unit newUnit(@Named("Code") final String code,
			@Named("Name") final String name) {
		Unit unit = unitsRepo.newUnit(code, name);
		getUnits().add(unit);
		unit.setProperty(this);
		return unit;
	}

	// }}

	// {{ addOwner (action)
	@MemberOrder(name = "Owners", sequence = "1")
	public void addOwner(final Owner owner) {
		getOwners().add(owner);
	}

	public String validateAddOwner(final Owner owner) {
		return getOwners().contains(owner) ? "Already an owner" : null;
	}

	// }}

	// {{ removeOwner (action)
	@MemberOrder(name = "Owners", sequence = "2")
	public void removeOwner(final Owner owner) {
		getOwners().remove(owner);
	}

	public String validateRemoveOwner(final Owner owner) {
		return getOwners().contains(owner) ? null : "Not an owner";
	}

	public List<Owner> choices0RemoveOwner() {
		return getOwners();
	}

	public Owner default0RemoveOwner() {
		return getOwners().size() >= 1 ? getOwners().get(0) : null;
	}

	// }}

	// {{ injected: Units
	private Units unitsRepo;

	public void setUnits(final Units unitsRepo) {
		this.unitsRepo = unitsRepo;
	}
	// }}
	
	
	
}
