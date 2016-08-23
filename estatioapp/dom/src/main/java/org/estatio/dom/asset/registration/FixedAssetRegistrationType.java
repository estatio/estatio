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

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.NonRecoverableException;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.UdoDomainObject2;
import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.PowerType;
import org.estatio.dom.WithTitleComparable;
import org.estatio.dom.WithTitleUnique;
import org.estatio.dom.apptenancy.ApplicationTenancyInvariantsService;
import org.estatio.dom.apptenancy.WithApplicationTenancyProperty;
import org.estatio.dom.utils.ClassUtils;
import org.estatio.dom.utils.TitleBuilder;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(identityType=IdentityType.DATASTORE)
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
@DomainObject(editing = Editing.DISABLED, bounded = true)
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
        return securityApplicationTenancyRepository.findByPathCached(ApplicationTenancyInvariantsService.GLOBAL_APPLICATION_TENANCY_PATH);
    }

    // //////////////////////////////////////

    public String title() {
        return TitleBuilder.start().withName(getTitle()).toString();
    }

    @javax.jdo.annotations.Column(allowsNull="false", length=JdoColumnLength.TITLE)
    @Getter @Setter
    private String title;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull="false", length=JdoColumnLength.FQCN)
    @Getter @Setter
    private String fullyQualifiedClassName;

    // //////////////////////////////////////

    @Programmatic
    public FixedAssetRegistration create(final DomainObjectContainer container){ 
        try {
            final Class<? extends FixedAssetRegistration> cls = 
                    ClassUtils.load(getFullyQualifiedClassName(), FixedAssetRegistration.class);
            FixedAssetRegistration registration = 
                container.newTransientInstance(cls);
            registration.setType(this);
            return registration;
        } catch (Exception ex) {
            throw new NonRecoverableException(ex);
        }
    }

}
