/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
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
package org.estatio.module.lease.dom;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.types.DescriptionType;
import org.incode.module.base.dom.types.ReferenceType;
import org.incode.module.base.dom.types.TitleType;
import org.incode.module.base.dom.utils.TitleBuilder;

import org.estatio.module.base.dom.UdoDomainObject2;
import org.incode.module.base.dom.with.WithNameUnique;
import org.incode.module.base.dom.with.WithReferenceComparable;
import org.estatio.module.base.dom.apptenancy.WithApplicationTenancyGlobal;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "dbo"     // Isis' ObjectSpecId inferred from @DomainObject#objectType
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Uniques({
        @javax.jdo.annotations.Unique(
                name = "LeaseType_reference_UNQ", members = "reference"),
        @javax.jdo.annotations.Unique(
                name = "LeaseType_name_UNQ", members = "name")
})
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByReference", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.lease.dom.LeaseType "
                        + "WHERE reference == :reference")
})
@DomainObject(
        editing = Editing.DISABLED,
        bounded = true,
        objectType = "org.estatio.dom.lease.LeaseType"
)
public class LeaseType
        extends UdoDomainObject2<LeaseType>
        implements WithReferenceComparable<LeaseType>, WithNameUnique, WithApplicationTenancyGlobal {

    public LeaseType() {
        super("reference");
    }

    public String title() {
        return TitleBuilder.start()
                .withName(getName())
                .toString();
    }

    @Column(
            length = ApplicationTenancy.MAX_LENGTH_PATH,
            allowsNull = "false",
            name = "atPath"
    )
    @Property(hidden = Where.ALL_EXCEPT_STANDALONE_TABLES)
    @Getter @Setter
    private String applicationTenancyPath;

    @Property(hidden = Where.EVERYWHERE)
    public ApplicationTenancy getApplicationTenancy() {
        return securityApplicationTenancyRepository.findByPathCached(getApplicationTenancyPath());
    }

    // //////////////////////////////////////

    @Column(allowsNull = "false", length = ReferenceType.Meta.MAX_LEN)
    @MemberOrder(sequence = "1")
    @Getter @Setter
    private String reference;

    // //////////////////////////////////////

    @Column(allowsNull = "false", length = TitleType.Meta.MAX_LEN)
    @MemberOrder(sequence = "2")
    @Getter @Setter
    private String name;

    // //////////////////////////////////////

    @Column(allowsNull = "true", length = DescriptionType.Meta.MAX_LEN)
    @Property(optionality = Optionality.OPTIONAL)
    @PropertyLayout(multiLine = 3)
    @MemberOrder(sequence = "3")
    @Getter @Setter
    private String description;

    @MemberOrder(sequence = "3", name = "description")
    public LeaseType change(
            final String name,
            final String description) {
        setName(name);
        setDescription(description);
        return this;
    }

    public String default0Change() {
        return getName();
    }

    public String default1Change() {
        return getDescription();
    }

}
