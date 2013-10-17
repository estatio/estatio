/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
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

import org.apache.isis.applib.annotation.Bounded;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.NotPersisted;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Title;

import org.estatio.dom.EstatioRefDataObject;
import org.estatio.dom.WithTitleComparable;
import org.estatio.dom.WithTitleUnique;

@javax.jdo.annotations.PersistenceCapable(identityType=IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy=IdGeneratorStrategy.NATIVE, 
        column="id")
@javax.jdo.annotations.Uniques({
    @javax.jdo.annotations.Unique(
            name = "AgreementType_title_UNQ", members="title")
})
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByTitle", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.agreement.AgreementType "
                        + "WHERE title == :title")
})
@Immutable
@Bounded
public class AgreementType extends EstatioRefDataObject<AgreementType> 
        implements WithTitleComparable<AgreementType>, WithTitleUnique {

    public AgreementType() {
        super("title");
    }

    // //////////////////////////////////////

    private String title;

    @javax.jdo.annotations.Column(allowsNull="false")
    @Title
    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }


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
        return agreementRoleTypes.findApplicableTo(this);
    }

    @Programmatic
    @NotPersisted
    public List<AgreementRoleCommunicationChannelType> getRoleChannelTypesApplicableTo() {
        return agreementRoleCommunicationChannelTypes.findApplicableTo(this);
    }
    

    // //////////////////////////////////////

    private AgreementRoleTypes agreementRoleTypes;

    public final void injectAgreementRoleTypes(final AgreementRoleTypes agreementRoleTypes) {
        this.agreementRoleTypes = agreementRoleTypes;
    }

    private AgreementRoleCommunicationChannelTypes agreementRoleCommunicationChannelTypes;
    
    public final void injectAgreementRoleCommunicationChannelTypes(
            final AgreementRoleCommunicationChannelTypes agreementRoleCommunicationChannelTypes) {
        this.agreementRoleCommunicationChannelTypes = agreementRoleCommunicationChannelTypes;
    }
    


}
