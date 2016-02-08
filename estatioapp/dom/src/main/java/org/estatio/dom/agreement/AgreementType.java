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
package org.estatio.dom.agreement;

import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.NotPersisted;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Title;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.EstatioDomainObject;
import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.WithTitleComparable;
import org.estatio.dom.WithTitleUnique;
import org.estatio.dom.apptenancy.ApplicationTenancyInvariantsService;
import org.estatio.dom.apptenancy.WithApplicationTenancyGlobal;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
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
                        + "FROM org.estatio.dom.agreement.AgreementType "
                        + "WHERE title == :title")
})
@DomainObject(editing = Editing.DISABLED, bounded = true)
public class AgreementType
        extends EstatioDomainObject<AgreementType>
        implements WithTitleComparable<AgreementType>, WithTitleUnique, WithApplicationTenancyGlobal {

    public AgreementType() {
        super("title");
    }

    // //////////////////////////////////////

    @Hidden
    public ApplicationTenancy getApplicationTenancy() {
        return securityApplicationTenancyRepository.findByPathCached(ApplicationTenancyInvariantsService.GLOBAL_APPLICATION_TENANCY_PATH);
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "false", length = JdoColumnLength.TITLE)
    @Title
    @Getter @Setter
    private String title;

    // //////////////////////////////////////

    @NotPersisted
    // else Isis tries to persist graph when setting up fixture data.
    public List<AgreementRoleType> getRoles() {
        return AgreementRoleType.applicableTo(this);
    }

    // //////////////////////////////////////

    @Programmatic
    @NotPersisted
    public List<AgreementRoleType> getRoleTypesApplicableTo() {
        return agreementRoleTypeRepository.findApplicableTo(this);
    }

    @Programmatic
    @NotPersisted
    public List<AgreementRoleCommunicationChannelType> getRoleChannelTypesApplicableTo() {
        return agreementRoleCommunicationChannelTypeRepository.findApplicableTo(this);
    }

    // //////////////////////////////////////

    private AgreementRoleTypeRepository agreementRoleTypeRepository;

    public final void injectAgreementRoleTypes(final AgreementRoleTypeRepository agreementRoleTypeRepository) {
        this.agreementRoleTypeRepository = agreementRoleTypeRepository;
    }

    private AgreementRoleCommunicationChannelTypeRepository agreementRoleCommunicationChannelTypeRepository;

    public final void injectAgreementRoleCommunicationChannelTypes(
            final AgreementRoleCommunicationChannelTypeRepository agreementRoleCommunicationChannelTypeRepository) {
        this.agreementRoleCommunicationChannelTypeRepository = agreementRoleCommunicationChannelTypeRepository;
    }

}
