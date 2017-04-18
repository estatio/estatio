/*
 *
 *  Copyright 2012-2015 Eurocommercial Properties NV
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
package org.estatio.capex.dom.project;

import java.math.BigDecimal;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.types.MoneyType;
import org.incode.module.base.dom.utils.TitleBuilder;

import org.estatio.capex.dom.items.FinancialItem;
import org.estatio.capex.dom.items.FinancialItemType;
import org.estatio.dom.UdoDomainObject;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.asset.Property;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.tax.Tax;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
		identityType = IdentityType.DATASTORE
		,schema = "dbo"
)
@DatastoreIdentity(strategy = IdGeneratorStrategy.NATIVE, column = "id")
@Version(strategy = VersionStrategy.VERSION_NUMBER, column = "version")
@Queries({
		@Query(name = "findByProjectAndCharge", language = "JDOQL", value = "SELECT "
				+ "FROM org.estatio.capex.dom.project.ProjectItem "
				+ "WHERE project == :project "
				+ " && charge == :charge")
})
@Unique(name = "ProjectItem_project_charge_UNQ", members = { "project", "charge" })
@DomainObject(
		editing = Editing.DISABLED,
		objectType = "org.estatio.capex.dom.project.ProjectItem"
)
public class ProjectItem extends UdoDomainObject<ProjectItem> implements FinancialItem {

	public String title(){
		return TitleBuilder.start().withParent(getProject()).withName(getCharge()).toString();
	}

	public ProjectItem() {
		super("project, charge");
	}

	@Column(allowsNull = "false", name = "projectId")
	@PropertyLayout(hidden = Where.REFERENCES_PARENT)
	@Getter @Setter
	private Project project;

	@Column(allowsNull = "false", name = "chargeId")
	@Getter @Setter
	private Charge charge;

	@Column(allowsNull = "false")
	@Getter @Setter
	private String description;

	@Column(allowsNull = "true", scale = MoneyType.Meta.SCALE)
	@Getter @Setter
	private BigDecimal budgetedAmount;

	@Getter @Setter
	@Column(allowsNull = "true")
	private LocalDate startDate;

	@Getter @Setter
	@Column(allowsNull = "true")
	private LocalDate endDate;

	@Column(allowsNull = "true", name = "propertyId")
	@Getter @Setter
	private Property property;

	@Column(allowsNull = "true", name = "taxId")
	@Getter @Setter
	private Tax tax;

	@PropertyLayout(
			named = "Application Level",
			describedAs = "Determines those users for whom this object is available to view and/or modify."
	)
	@Override
	public ApplicationTenancy getApplicationTenancy() {
		return getProject().getApplicationTenancy();
	}

	@Override
	@Programmatic
	public BigDecimal value() {
		return getBudgetedAmount();
	}

	@Override
	public FinancialItemType getType() {
		return FinancialItemType.BUDGETED;
	}

	@Override
	public FixedAsset<?> getFixedAsset() {
		return getProperty();
	}
}
