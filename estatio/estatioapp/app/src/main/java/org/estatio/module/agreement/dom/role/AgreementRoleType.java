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
package org.estatio.module.agreement.dom.role;

import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Unique;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.types.TitleType;
import org.incode.module.base.dom.utils.StringUtils;
import org.incode.module.base.dom.utils.TitleBuilder;
import org.incode.module.base.dom.with.WithTitleComparable;

import org.estatio.module.base.dom.UdoDomainObject2;
import org.estatio.module.agreement.dom.type.AgreementType;
import org.estatio.module.base.dom.apptenancy.ApplicationTenancyConstants;
import org.estatio.module.base.dom.apptenancy.WithApplicationTenancyGlobal;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "dbo"    // Isis' ObjectSpecId inferred from @DomainObject#objectType
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@Unique(name = "AgreemenRoleType_appliesTo_title_UNQ", members = { "appliesTo", "title" })
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByTitle", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.agreement.dom.role.AgreementRoleType "
                        + "WHERE title == :title "),
        @javax.jdo.annotations.Query(
                name = "findByAgreementType", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.agreement.dom.role.AgreementRoleType "
                        + "WHERE appliesTo == :agreementType "),
        @javax.jdo.annotations.Query(
                name = "findByAgreementTypeAndTitle", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.agreement.dom.role.AgreementRoleType "
                        + "WHERE appliesTo == :agreementType && title == :title")
})
@DomainObject(
        editing = Editing.DISABLED,
        bounded = true,
        objectType = "org.estatio.dom.agreement.AgreementRoleType" // backward compatibility
)
public class AgreementRoleType
        extends UdoDomainObject2<AgreementRoleType>
        implements WithTitleComparable<AgreementRoleType>, WithApplicationTenancyGlobal, IAgreementRoleType {

    public AgreementRoleType() {
        super("title");
    }


    @Override
    @Programmatic
    public String getKey() {
        return StringUtils.enumDeTitle(getTitle());
    }

    public String title() {
        return TitleBuilder.start()
                .withName(getTitle())
                .toString();
    }

    @Property(hidden = Where.EVERYWHERE)
    public ApplicationTenancy getApplicationTenancy() {
        return securityApplicationTenancyRepository.findByPathCached(
                ApplicationTenancyConstants.GLOBAL_PATH);
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "false", length = TitleType.Meta.MAX_LEN)
    @Getter @Setter
    private String title;




    @javax.jdo.annotations.Column(name = "appliesToAgreementTypeId", allowsNull = "false")
    @Getter
    private AgreementType appliesTo;

    public void setAppliesTo(final AgreementType appliesTo) {
        this.appliesTo = appliesTo;
    }
    // //////////////////////////////////////

    public static List<AgreementRoleType> applicableTo(final AgreementType at) {
        return at.getRoleTypesApplicableTo();
    }

}
