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
package com.eurocommercialproperties.estatio.dom.asset;

import java.util.Arrays;
import java.util.List;

import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Title;

/**
 * 
 * 
 * @version $Rev$ $Date$
 */
public class Unit {

	
	// {{ Property (property)
	private Property property;

	@Disabled
	@MemberOrder(sequence = "1")
	public Property getProperty() {
		return property;
	}

	public void setProperty(final Property property) {
		this.property = property;
	}
	// }}


	// {{ Code (property)
	private String code;

	@Title(sequence="1", append=",")
	@MemberOrder(sequence = "1")
	public String getCode() {
		return code;
	}

	public void setCode(final String code) {
		this.code = code;
	}

	// }}

	// {{ Name (property)
	private String name;

	@Disabled
	@Title(sequence="2")
	@MemberOrder(sequence = "1")
	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	// }}
	
	// {{ Type (property)
	private UnitType type;

	@Disabled
	@MemberOrder(sequence = "1")
	public UnitType getType() {
		return type;
	}

	public void setType(final UnitType type) {
		this.type = type;
	}
	public List<UnitType> choicesType() {
		return Arrays.asList(UnitType.values());
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

}
