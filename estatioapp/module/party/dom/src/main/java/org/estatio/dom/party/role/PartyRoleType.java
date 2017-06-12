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
package org.estatio.dom.party.role;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Property;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.types.ReferenceType;
import org.incode.module.base.dom.types.TitleType;
import org.incode.module.base.dom.utils.TitleBuilder;

import org.estatio.dom.UdoDomainObject2;
import org.estatio.dom.apptenancy.ApplicationTenancyConstants;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "dbo"
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Uniques({
        @javax.jdo.annotations.Unique(
                name = "PartyRoleType_key_UNQ", members = "key")
})
@javax.jdo.annotations.Indices({
        @javax.jdo.annotations.Index(
                name = "PartyRoleType_key_title_IDX", members = { "key", "title" })
})
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "matchByKeyOrTitle", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.party.role.PartyRoleType "
                        + "WHERE key.matches(:keyOrTitle) "
                        + "   || title.matches(:keyOrTitle)"),
        @javax.jdo.annotations.Query(
                name = "findByKey", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.party.role.PartyRoleType "
                        + "WHERE key == :key") })
@DomainObject(bounded = true)
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_ROOT)
public class PartyRoleType
        extends UdoDomainObject2<PartyRoleType>
        implements IPartyRoleType {

    public PartyRoleType() {
        super("title");
    }

    public String title() {
        return TitleBuilder.start()
                .withName(getTitle())
                .withReference(getKey())
                .toString();

    }

    @Column(allowsNull = "false", length = ReferenceType.Meta.MAX_LEN)
    @Property(regexPattern = ReferenceType.Meta.REGEX)
    @Getter @Setter
    private String key;

    @Column(allowsNull = "false", length = TitleType.Meta.MAX_LEN)
    @Getter @Setter
    private String title;

    @Override public ApplicationTenancy getApplicationTenancy() {
        return securityApplicationTenancyRepository.findByPathCached(
                ApplicationTenancyConstants.GLOBAL_PATH);
    }

}
