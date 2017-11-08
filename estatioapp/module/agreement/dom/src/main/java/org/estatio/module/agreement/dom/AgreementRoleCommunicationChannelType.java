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
package org.estatio.module.agreement.dom;

import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.types.TitleType;
import org.incode.module.base.dom.utils.TitleBuilder;

import org.estatio.dom.UdoDomainObject2;
import org.estatio.module.agreement.dom.commchantype.IAgreementRoleCommunicationChannelType;
import org.estatio.module.agreement.dom.type.AgreementType;
import org.estatio.dom.apptenancy.ApplicationTenancyConstants;
import org.estatio.dom.apptenancy.WithApplicationTenancyGlobal;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType=IdentityType.DATASTORE
        ,schema = "dbo"    // Isis' ObjectSpecId inferred from @DomainObject#objectType
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy=IdGeneratorStrategy.NATIVE, 
        column="id")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByTitle", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.agreement.dom.AgreementRoleCommunicationChannelType "
                        + "WHERE title == :title "),
        @javax.jdo.annotations.Query(
                name = "findByAgreementType", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.agreement.dom.AgreementRoleCommunicationChannelType "
                        + "WHERE appliesTo == :agreementType "),
        @javax.jdo.annotations.Query(
                name = "findByAgreementTypeAndTitle", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.agreement.dom.AgreementRoleCommunicationChannelType "
                        + "WHERE appliesTo == :agreementType && title == :title")
})
@DomainObject(
        bounded = true,
        editing = Editing.DISABLED,
        objectType = "org.estatio.dom.agreement.AgreementRoleCommunicationChannelType"
)
public class AgreementRoleCommunicationChannelType
        extends UdoDomainObject2<AgreementRoleCommunicationChannelType>
        implements WithApplicationTenancyGlobal, IAgreementRoleCommunicationChannelType {

    public AgreementRoleCommunicationChannelType() {
        super("title");
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

    @javax.jdo.annotations.Column(allowsNull="false", length= TitleType.Meta.MAX_LEN)
    @Getter @Setter
    private String title;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name="appliesToAgreementTypeId", allowsNull="false")
    @Getter @Setter
    private AgreementType appliesTo;

    // //////////////////////////////////////

    @Programmatic
    public Predicate<? super AgreementRoleCommunicationChannel> matchingCommunicationChannel() {
        return new Predicate<AgreementRoleCommunicationChannel>() {
            @Override
            public boolean apply(final AgreementRoleCommunicationChannel arcc) {
                return arcc != null && Objects.equal(arcc.getType(), AgreementRoleCommunicationChannelType.this) ? true : false;
            }
        };
    }

    public static List<AgreementRoleCommunicationChannelType> applicableTo(final AgreementType at) {
        return at.getRoleChannelTypesApplicableTo();
    }

}
