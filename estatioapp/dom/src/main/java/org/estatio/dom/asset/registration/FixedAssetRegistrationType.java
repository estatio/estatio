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
package org.estatio.dom.asset.registration;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;

import org.apache.isis.applib.NonRecoverableException;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.factory.FactoryService;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.types.FqcnType;
import org.incode.module.base.dom.types.TitleType;
import org.incode.module.base.dom.utils.ClassUtils;
import org.incode.module.base.dom.utils.TitleBuilder;

import org.incode.module.base.dom.PowerType;
import org.estatio.dom.UdoDomainObject2;
import org.incode.module.base.dom.with.WithTitleComparable;
import org.incode.module.base.dom.with.WithTitleUnique;
import org.estatio.dom.apptenancy.ApplicationTenancyConstants;
import org.estatio.dom.apptenancy.WithApplicationTenancyProperty;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType=IdentityType.DATASTORE
        ,schema = "dbo" // Isis' ObjectSpecId inferred from @DomainObject#objectType
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy=IdGeneratorStrategy.NATIVE, 
        column="id")
@javax.jdo.annotations.Uniques({
    @javax.jdo.annotations.Unique(
            name = "FixedAssetRegistrationType_title_UNQ", members="title")
})
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByTitle", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.asset.registration.FixedAssetRegistrationType "
                        + "WHERE title == :title")
})
@DomainObject(
        editing = Editing.DISABLED,
        bounded = true,
        objectType = "org.estatio.dom.asset.registration.FixedAssetRegistrationType"
)
public class FixedAssetRegistrationType 
        extends UdoDomainObject2<FixedAssetRegistrationType>
        implements WithTitleComparable<FixedAssetRegistrationType>, 
                   WithTitleUnique, PowerType<FixedAssetRegistration>,
                   WithApplicationTenancyProperty {

    public FixedAssetRegistrationType() {
        super("title");
    }

    // //////////////////////////////////////

    @Property(hidden = Where.EVERYWHERE)
    public ApplicationTenancy getApplicationTenancy() {
        return securityApplicationTenancyRepository.findByPathCached(
                ApplicationTenancyConstants.GLOBAL_PATH);
    }

    // //////////////////////////////////////

    public String title() {
        return TitleBuilder.start().withName(getTitle()).toString();
    }

    @javax.jdo.annotations.Column(allowsNull="false", length= TitleType.Meta.MAX_LEN)
    @Getter @Setter
    private String title;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull="false", length= FqcnType.Meta.MAX_LEN)
    @Getter @Setter
    private String fullyQualifiedClassName;

    // //////////////////////////////////////

    @Programmatic
    public FixedAssetRegistration create(final FactoryService factoryService){
        try {
            final Class<? extends FixedAssetRegistration> cls = 
                    ClassUtils.load(getFullyQualifiedClassName(), FixedAssetRegistration.class);
            FixedAssetRegistration registration = factoryService.instantiate(cls);
            registration.setType(this);
            return registration;
        } catch (Exception ex) {
            throw new NonRecoverableException(ex);
        }
    }

}
