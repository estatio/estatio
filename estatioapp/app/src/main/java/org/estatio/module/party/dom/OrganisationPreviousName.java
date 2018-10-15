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

package org.estatio.module.party.dom;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.collect.ComparisonChain;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.util.TitleBuffer;

import org.incode.module.base.dom.types.NameType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "dbo"    // Isis' ObjectSpecId inferred from @DomainObject#objectType
)
@Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version"
)
@Unique(
        name = "Organisation"
)
@DomainObject(
        objectType = "org.estatio.dom.party.OrganisationPreviousName"
)
@NoArgsConstructor
@RequiredArgsConstructor
@Getter @Setter
public class OrganisationPreviousName implements Comparable<OrganisationPreviousName> {


    public String title() {
        return new TitleBuffer()
                    .append(getName())
                    .append("until")
                    .append(getEndDate().toString("dd-MMM-yyyy"))
                    .toString();
    }

    @Column(allowsNull = "false", name = "organisationId")
    @lombok.NonNull
    @Property(hidden = Where.REFERENCES_PARENT, editing = Editing.DISABLED)
    private Organisation organisation;


    @Column(allowsNull = "false", length = Party.NameType.Meta.MAX_LEN)
    @lombok.NonNull
    private String name;


    @Column(allowsNull = "false")
    @Persistent
    @lombok.NonNull
    private LocalDate endDate;


    @Override
    public int compareTo(final OrganisationPreviousName o) {
        return ComparisonChain.start()
                .compare(getEndDate(), o.getEndDate())
                .compare(getName(), o.getName())
                .result();
    }

}
