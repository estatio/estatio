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
package org.estatio.dom.communicationchannel;

import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;

import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.Status;
import org.estatio.dom.WithNameGetter;
import org.estatio.dom.WithReferenceGetter;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
@javax.jdo.annotations.DatastoreIdentity(strategy = IdGeneratorStrategy.IDENTITY, column = "COMMUNICATIONCHANNEL_ID")
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByReferenceAndType", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.communicationchannel.CommunicationChannel "
                        + "WHERE reference == :reference "
                        + "&& type == :type"),
        @javax.jdo.annotations.Query(
                name = "findByOwner", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.communicationchannel.CommunicationChannel "
                        + "WHERE owner == :owner ")
})
@Bookmarkable(BookmarkPolicy.AS_CHILD)
public abstract class CommunicationChannel extends EstatioTransactionalObject<CommunicationChannel, Status> implements WithNameGetter, WithReferenceGetter {

    public CommunicationChannel() {
        // TODO: description is annotated as optional,
        // so it doesn't really make sense for it to be part of the natural sort
        // order
        super("type, description", Status.UNLOCKED, Status.LOCKED);
    }

    @Override
    public Status getLockable() {
        return getStatus();
    }

    @Override
    public void setLockable(Status lockable) {
        setStatus(lockable);
    }

    // //////////////////////////////////////

    private Status status;

    @javax.jdo.annotations.Column(allowsNull="false")
    @MemberOrder(sequence="5")
    @Disabled
    public Status getStatus() {
        return status;
    }

    public void setStatus(final Status status) {
        this.status = status;
    }

    // //////////////////////////////////////
    
    @MemberOrder(sequence="2")
    @Hidden(where = Where.OBJECT_FORMS)
    public String getName() {
        return getContainer().titleOf(this);
    }
    
    // //////////////////////////////////////

    private CommunicationChannelOwner owner;

    @javax.jdo.annotations.Persistent(
            extensions = {
                    @Extension(vendorName = "datanucleus",
                        key = "mapping-strategy",
                        value = "per-implementation"),
                    @Extension(vendorName = "datanucleus",
                        key = "implementation-classes",
                        value = "org.estatio.dom.party.Organisation,org.estatio.dom.party.Person,org.estatio.dom.asset.Property,org.estatio.dom.asset.Unit")
            })
    @javax.jdo.annotations.Columns({
        @javax.jdo.annotations.Column(name="OWNER_ORGANISATION_ID"),
        @javax.jdo.annotations.Column(name="OWNER_PERSON_ID"),
        @javax.jdo.annotations.Column(name="OWNER_PROPERTY_ID"),
        @javax.jdo.annotations.Column(name="OWNER_UNIT_ID")
    })
    @Optional // not really, but cannot set @Column(allowNulls="false") for a polymorphic relationship
    @Hidden(where = Where.PARENTED_TABLES)
    @Disabled
    public CommunicationChannelOwner getOwner() {
        return owner;
    }

    public void setOwner(final CommunicationChannelOwner owner) {
        this.owner = owner;
    }

    // //////////////////////////////////////

    private CommunicationChannelType type;

    @MemberOrder(sequence="1")
    @javax.jdo.annotations.Column(allowsNull="false")
    @Hidden(where=Where.OBJECT_FORMS)
    public CommunicationChannelType getType() {
        return type;
    }

    public void setType(final CommunicationChannelType type) {
        this.type = type;
    }

    // //////////////////////////////////////

    private String reference;

    /**
     * For import purposes only
     */
    @javax.jdo.annotations.Column(allowsNull="true")
    @Hidden
    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }


    // //////////////////////////////////////

    private String description;

    @Hidden(where=Where.ALL_TABLES)
    @Optional
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    // //////////////////////////////////////

    private boolean legal;

    @MemberOrder(sequence="3")
    public boolean isLegal() {
        return legal;
    }

    public void setLegal(final boolean Legal) {
        this.legal = Legal;
    }

}
