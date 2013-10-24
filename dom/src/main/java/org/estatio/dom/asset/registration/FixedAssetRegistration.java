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
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;

import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.Status;
import org.estatio.dom.asset.FixedAsset;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Discriminator(
        strategy = DiscriminatorStrategy.CLASS_NAME,
        column = "discriminator")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findBySubject", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.asset.registration.FixedAssetRegistration "
                        + "WHERE subject == :subject"),
})
public class FixedAssetRegistration extends EstatioTransactionalObject<FixedAssetRegistration, Status> {

    public FixedAssetRegistration() {
        super("type", Status.UNLOCKED, Status.LOCKED);
    }

    @Override
    public Status getLockable() {
        return getStatus();
    }

    @Override
    public void setLockable(final Status lockable) {
        setStatus(lockable);
    }

    // //////////////////////////////////////

    private Status status;

    @javax.jdo.annotations.Column(allowsNull = "false")
    @Hidden
    public Status getStatus() {
        return status;
    }

    public void setStatus(final Status status) {
        this.status = status;
    }

    // //////////////////////////////////////

    private FixedAsset subject;

    @MemberOrder(sequence = "1")
    @javax.jdo.annotations.Column(name = "subjectId", allowsNull = "false")
    public FixedAsset getSubject() {
        return subject;
    }

    public void setSubject(final FixedAsset subject) {
        this.subject = subject;
    }

    // //////////////////////////////////////

    private FixedAssetRegistrationType type;

    public FixedAssetRegistrationType getType() {
        return type;
    }
    
    public void setType(final FixedAssetRegistrationType type) {
        this.type = type;
    }

    // //////////////////////////////////////

    String title() {
        // TODO Auto-generated method stub
        return null;
    }

}