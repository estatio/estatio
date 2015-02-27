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
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.apache.isis.applib.annotation.Bounded;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Title;
import org.estatio.dom.EstatioDomainObject;
import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.apptenancy.ApplicationTenancyInvariantsService;
import org.estatio.dom.apptenancy.WithApplicationTenancyGlobal;

@javax.jdo.annotations.PersistenceCapable(identityType=IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy=IdGeneratorStrategy.NATIVE, 
        column="id")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByTitle", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.agreement.AgreementRoleCommunicationChannelType "
                        + "WHERE title == :title "),
        @javax.jdo.annotations.Query(
                name = "findByAgreementType", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.agreement.AgreementRoleCommunicationChannelType "
                        + "WHERE appliesTo == :agreementType "),
        @javax.jdo.annotations.Query(
                name = "findByAgreementTypeAndTitle", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.agreement.AgreementRoleCommunicationChannelType "
                        + "WHERE appliesTo == :agreementType && title == :title")
})
@Immutable
@Bounded
public class AgreementRoleCommunicationChannelType 
        extends EstatioDomainObject<AgreementRoleCommunicationChannelType>
        implements WithApplicationTenancyGlobal {

    public AgreementRoleCommunicationChannelType() {
        super("title");
    }

    // //////////////////////////////////////

    @Hidden
    public ApplicationTenancy getApplicationTenancy() {
        return applicationTenancies.findTenancyByPath(ApplicationTenancyInvariantsService.GLOBAL_APPLICATION_TENANCY_PATH);
    }

    // //////////////////////////////////////

    private String title;

    @javax.jdo.annotations.Column(allowsNull="false", length=JdoColumnLength.TITLE)
    @Title
    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    // //////////////////////////////////////

    private AgreementType appliesTo;

    @javax.jdo.annotations.Column(name="appliesToAgreementTypeId", allowsNull="false")
    public AgreementType getAppliesTo() {
        return appliesTo;
    }

    public void setAppliesTo(final AgreementType agreementType) {
        this.appliesTo = agreementType;
    }
    
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

    // //////////////////////////////////////

    public static List<AgreementRoleCommunicationChannelType> applicableTo(final AgreementType at) {
        return at.getRoleChannelTypesApplicableTo();
    }


}
