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
import org.apache.isis.applib.annotation.Bounded;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Title;

import org.estatio.dom.EstatioDomainObject;
import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.PowerType;
import org.estatio.dom.WithTitleComparable;
import org.estatio.dom.WithTitleUnique;
import org.estatio.dom.utils.ClassUtils;

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
@Immutable
@Bounded
public class FixedAssetRegistrationType 
        extends EstatioDomainObject<FixedAssetRegistrationType> 
        implements WithTitleComparable<FixedAssetRegistrationType>, 
                   WithTitleUnique, PowerType<FixedAssetRegistration> {

    public FixedAssetRegistrationType() {
        super("title");
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


    private String fullyQualifiedClassName;

    @javax.jdo.annotations.Column(allowsNull="false", length=JdoColumnLength.FQCN)
    public String getFullyQualifiedClassName() {
        return fullyQualifiedClassName;
    }

    public void setFullyQualifiedClassName(final String fullyQualifiedClassName) {
        this.fullyQualifiedClassName = fullyQualifiedClassName;
    }

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
