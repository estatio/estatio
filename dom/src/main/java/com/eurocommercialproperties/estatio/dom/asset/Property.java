package com.eurocommercialproperties.estatio.dom.asset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.value.DateTime;

public class Property extends AbstractDomainObject {

	// {{ Title
	public String title() {
		return getDescription();
	}

	// }}

	// {{ Code (property)
	private String code;

	@MemberOrder(sequence = "1")
	public String getCode() {
		return code;
	}

	public void setCode(final String code) {
		this.code = code;
	}

	// }}

	// {{ Description
	private String description;

	@MemberOrder(sequence = "2")
	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	// }}

	// {{ AcquireDate (property)
	private DateTime acquireDate;

	@MemberOrder(sequence = "1")
	public DateTime getAcquireDate() {
		return acquireDate;
	}

	public void setAcquireDate(final DateTime acquireDate) {
		this.acquireDate = acquireDate;
	}

	// }}

	// {{ Disposal Date (property)
	private DateTime disposalDate;

	@MemberOrder(sequence = "1")
	public DateTime getDisposalDate() {
		return disposalDate;
	}

	public void setDisposalDate(final DateTime disposalDate) {
		this.disposalDate = disposalDate;
	}

	// }}

	// {{ Type (property)
	private PropertyType type;

	@MemberOrder(sequence = "1")
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

	// {{ Area (property)
	private Double area;

	@MemberOrder(sequence = "1")
	public Double getArea() {
		return area;
	}

	public void setArea(final Double area) {
		this.area = area;
	}
	// }}
	
	// {{ Units (Collection)
	private List<Unit> units = new ArrayList<Unit>();

	@MemberOrder(sequence = "1")
	public List<Unit> getUnits() {
		return units;
	}

	public void setUnits(final List<Unit> units) {
		this.units = units;
	}
	// }}

public void addToUnits(final Unit unit) {
	// check for no-op
	if (unit == null || getUnits().contains(unit)) {
		return;
	}
	// associate new
	getUnits().add(unit);
	// additional business logic
	onAddToUnits(unit);
}

public void removeFromUnits(final Unit unit) {
	// check for no-op
	if (unit == null || !getUnits().contains(unit)) {
		return;
	}
	// dissociate existing
	getUnits().remove(unit);
	// additional business logic
	onRemoveFromUnits(unit);
}

protected void onAddToUnits(final Unit unit) {
}

protected void onRemoveFromUnits(final Unit unit) {
}
	

}
