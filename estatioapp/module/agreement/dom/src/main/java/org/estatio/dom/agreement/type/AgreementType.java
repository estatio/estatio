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
package org.estatio.dom.agreement.type;

import java.util.List;

import javax.inject.Inject;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;

import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.types.TitleType;
import org.incode.module.base.dom.utils.TitleBuilder;
import org.incode.module.base.dom.with.WithTitleComparable;
import org.incode.module.base.dom.with.WithTitleUnique;

import org.estatio.dom.UdoDomainObject2;
import org.estatio.dom.agreement.AgreementRoleCommunicationChannelType;
import org.estatio.dom.agreement.AgreementRoleCommunicationChannelTypeRepository;
import org.estatio.dom.agreement.role.AgreementRoleType;
import org.estatio.dom.agreement.role.AgreementRoleTypeRepository;
import org.estatio.dom.apptenancy.ApplicationTenancyConstants;
import org.estatio.dom.apptenancy.WithApplicationTenancyGlobal;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "dbo"    // Isis' ObjectSpecId inferred from @DomainObject#objectType
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Uniques({
        @javax.jdo.annotations.Unique(
                name = "AgreementType_title_UNQ", members = "title")
})
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByTitle", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.agreement.type.AgreementType "
                        + "WHERE title == :title")
})
@DomainObject(
        editing = Editing.DISABLED,
        bounded = true,
        objectType = "org.estatio.dom.agreement.AgreementType" // backward compatibility
)
public class AgreementType
        extends UdoDomainObject2<AgreementType>
        implements WithTitleComparable<AgreementType>, WithTitleUnique, WithApplicationTenancyGlobal, IAgreementType {

    public AgreementType() {
        super("title");
    }

    /**
     * For testing purposes only.
     */
    public AgreementType(AgreementRoleTypeRepository agreementRoleTypeRepository) {
        this();
        this.agreementRoleTypeRepository = agreementRoleTypeRepository;
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

    // //////////////////////////////////////

    @Collection(notPersisted = true)
    // else Isis tries to persist graph when setting up fixture data.
    public List<AgreementRoleType> getRoles() {
        return AgreementRoleType.applicableTo(this);
    }

    // //////////////////////////////////////

    @Programmatic
    @Collection(notPersisted = true)
    public List<AgreementRoleType> getRoleTypesApplicableTo() {
        return agreementRoleTypeRepository.findApplicableTo(this);
    }

    @Programmatic
    @Collection(notPersisted = true)
    public List<AgreementRoleCommunicationChannelType> getRoleChannelTypesApplicableTo() {
        return agreementRoleCommunicationChannelTypeRepository.findApplicableTo(this);
    }

    // //////////////////////////////////////

    @Inject
    protected AgreementRoleTypeRepository agreementRoleTypeRepository;

    @Inject
    private AgreementRoleCommunicationChannelTypeRepository agreementRoleCommunicationChannelTypeRepository;

}
