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

package org.estatio.domlink;

import javax.jdo.annotations.IdentityType;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.MemberGroupLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.types.FqcnType;
import org.incode.module.base.dom.types.NameType;
import org.incode.module.base.dom.types.UrlTemplateType;
import org.incode.module.base.dom.utils.TitleBuilder;

import org.estatio.dom.UdoDomainObject2;
import org.estatio.dom.apptenancy.WithApplicationTenancyPathPersisted;
import org.estatio.dom.apptenancy.WithApplicationTenancyProperty;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "EstatioLink" // Isis' ObjectSpecId inferred from @DomainObject#objectType
)
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByClassName", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.domlink.Link "
                        + "WHERE className == :className")
})
@javax.jdo.annotations.Unique(members = { "className", "name" })
@DomainObject(
        objectType = "org.estatio.domlink.Link"     // TODO: externalize mapping
)
@MemberGroupLayout(columnSpans = { 12, 0, 0, 12 })
public class Link
        extends UdoDomainObject2<Link>
        implements WithApplicationTenancyProperty, WithApplicationTenancyPathPersisted {

    public Link() {
        super("name");
    }

    public String title() {
        return TitleBuilder.start()
                .withName(getName())
                .toString();
    }

    @javax.jdo.annotations.Column(
            length = ApplicationTenancy.MAX_LENGTH_PATH,
            allowsNull = "false",
            name = "atPath"
    )
    @Property(hidden = Where.EVERYWHERE)
    @Getter @Setter
    private String applicationTenancyPath;

    @PropertyLayout(
            named = "Application Level",
            describedAs = "Determines those users for whom this object is available to view and/or modify."
    )
    public ApplicationTenancy getApplicationTenancy() {
        return securityApplicationTenancyRepository.findByPathCached(getApplicationTenancyPath());
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "false", length = FqcnType.Meta.MAX_LEN)
    @MemberOrder(sequence = "1")
    @Getter @Setter
    private String className;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "false", length = NameType.Meta.MAX_LEN)
    @MemberOrder(sequence = "2")
    @Getter @Setter
    private String name;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "false", length = UrlTemplateType.Meta.MAX_LEN)
    @MemberOrder(sequence = "3")
    @Getter @Setter
    private String urlTemplate;

}
