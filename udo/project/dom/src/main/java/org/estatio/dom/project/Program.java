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
package org.estatio.dom.project;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.i18n.TranslatableString;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancies;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.RegexValidation;
import org.estatio.dom.UdoDomainObject;
import org.estatio.dom.WithReferenceUnique;
import org.estatio.dom.apptenancy.WithApplicationTenancyGlobalAndCountry;
import org.estatio.dom.apptenancy.WithApplicationTenancyPathPersisted;

import lombok.Getter;
import lombok.Setter;

//import org.apache.isis.applib.annotation.Property;

@PersistenceCapable(identityType = IdentityType.DATASTORE)
@DatastoreIdentity(strategy = IdGeneratorStrategy.NATIVE, column = "id")
@Version(strategy = VersionStrategy.VERSION_NUMBER, column = "version")
@Unique(members={"reference"})
@Queries({
        @Query(
                name = "findByReference", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.project.Program " +
                        "WHERE reference == :reference "),
        @Query(
                name = "matchByReferenceOrName", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.project.Program " +
                        "WHERE reference.matches(:matcher) || name.matches(:matcher) "),
        @Query(
                name = "findByProperty", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.project.Program " +
                        "WHERE property == :property ")
})
@DomainObject(editing=Editing.DISABLED)
public class Program 
			extends UdoDomainObject<Program>
			implements WithReferenceUnique, WithApplicationTenancyPathPersisted, WithApplicationTenancyGlobalAndCountry {

	public Program() {
		super("reference, name, programGoal");
	}

    //region > identificatiom
    public TranslatableString title() {
        return TranslatableString.tr("{name}", "name", "[" + getReference() + "] " + getName());
    }
    //endregion
    // //////////////////////////////////////

    @Column(allowsNull = "false")
    @org.apache.isis.applib.annotation.Property(regexPattern = RegexValidation.REFERENCE)
    @PropertyLayout(describedAs = "Unique reference code for this program")
    @MemberOrder(sequence="1")
    @Getter @Setter
    private String reference;

    // //////////////////////////////////////

    @Column(allowsNull = "false")
    @MemberOrder(sequence="2")
    @Getter @Setter
    private String name;

    // //////////////////////////////////////

    @Column(allowsNull = "false")
    @PropertyLayout(multiLine = 5)
    @MemberOrder(sequence="3")
    @Getter @Setter
    private String programGoal;

    // //////////////////////////////////////

    @Column(allowsNull = "true")
    @Persistent
    @MemberOrder(sequence="4")
    @Getter @Setter
    private String relatedObject;

    // //////////////////////////////////////
        
    //TODO: decouple sorted set [momentarily needed by code in  ProgramRole getPredecessor() etc.

    @javax.jdo.annotations.Persistent(mappedBy = "program")
    private SortedSet<ProgramRole> roles = new TreeSet<>();

    @CollectionLayout(render=RenderType.EAGERLY, hidden=Where.EVERYWHERE)
    public SortedSet<ProgramRole> getRoles() {
        return roles;
    }

//    public void setRoles(final SortedSet<ProgramRole> roles) {
//        this.roles = roles;
//    }
    
    // //////////////////////////////////////
    
    public Program changeProgram(
    		final String programName,
    		final @ParameterLayout(multiLine = 5) String programGoal)
    {
    	this.setName(programName);
    	this.setProgramGoal(programGoal);
    	
    	return this;
    }
    
    public String default0ChangeProgram() {
    	return this.getName();
    }
    
    public String default1ChangeProgram() {
    	return this.getProgramGoal();
    }
    
    // //////////////////////////////////////

    @javax.jdo.annotations.Column(
            length = ApplicationTenancy.MAX_LENGTH_PATH,
            allowsNull = "false",
            name = "atPath"
    )
    @PropertyLayout(hidden = Where.EVERYWHERE)
    @Getter @Setter
    private String applicationTenancyPath;

    @PropertyLayout(
            named = "Application Level",
            describedAs = "Determines those users for whom this object is available to view and/or modify."
    )
    public ApplicationTenancy getApplicationTenancy() {
        return applicationTenancies.findTenancyByPath(getApplicationTenancyPath());
    }

    // //////////////////////////////////////


    @Inject
    public ProgramRoleRepository programRoleRepository;

    @Inject
    protected ApplicationTenancies applicationTenancies;

}
