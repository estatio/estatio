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
package org.estatio.dom.asset.registration;

import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Title;

import org.estatio.dom.EstatioMutableObject;
import org.estatio.dom.asset.FixedAsset;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Discriminator(
        strategy = DiscriminatorStrategy.CLASS_NAME,
        column = "discriminator")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findBySubject", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.asset.registration.FixedAssetRegistration "
                        + "WHERE subject == :subject"),
})
public abstract class FixedAssetRegistration 
    extends EstatioMutableObject<FixedAssetRegistration> {

    public FixedAssetRegistration() {
        super("subject,type");
    }

    // //////////////////////////////////////

    private FixedAsset subject;

    @javax.jdo.annotations.Column(name = "subjectId", allowsNull = "false")
    @Disabled
    @MemberOrder(sequence = "1")
    @Title(sequence="1")
    public FixedAsset getSubject() {
        return subject;
    }

    public void setSubject(final FixedAsset subject) {
        this.subject = subject;
    }

    // //////////////////////////////////////

    private FixedAssetRegistrationType type;

    @Title(sequence="1", append=": ")
    @javax.jdo.annotations.Column(name = "registrationTypeId", allowsNull="false")
    public FixedAssetRegistrationType getType() {
        return type;
    }
    
    public void setType(final FixedAssetRegistrationType type) {
        this.type = type;
    }

}