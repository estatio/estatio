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

package org.estatio.dom.party;

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

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Where;

import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.utils.TitleBuilder;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(identityType = IdentityType.DATASTORE)
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
public class OrganisationPreviousName implements Comparable<OrganisationPreviousName> {

    public String title() {
        return TitleBuilder.start()
                .withName(getName())
                .withTupleElement(getEndDate())
                .toString();
    }

    @Column(allowsNull = "false", name = "organisationId")
    @Property(hidden = Where.REFERENCES_PARENT, editing = Editing.DISABLED)
    @Getter @Setter
    private Organisation organisation;

    // //////////////////////////////////////

    @Column(allowsNull = "false", length = JdoColumnLength.NAME)
    @Getter @Setter
    private String name;

    // //////////////////////////////////////

    @Column(allowsNull = "false")
    @Persistent
    @Getter @Setter
    private LocalDate endDate;

    @Override
    public int compareTo(final OrganisationPreviousName o) {
        final int compare = this.getEndDate().compareTo(o.getEndDate());

        if (compare == 0) {
            final int nameCompare = this.getName().compareTo(o.getName());
            return nameCompare;
        } else {
            return compare;
        }
    }
}
