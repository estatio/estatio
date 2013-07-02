/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
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
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.VersionStrategy;

import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.Status;
import org.estatio.dom.WithNameGetter;
import org.estatio.dom.WithReferenceGetter;
import org.estatio.dom.WithStatus;
import org.estatio.dom.agreement.AgreementRole;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.Bulk;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.annotation.ActionSemantics.Of;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
@javax.jdo.annotations.DatastoreIdentity(strategy = IdGeneratorStrategy.IDENTITY, column = "COMMUNICATIONCHANNEL_ID")
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
@javax.jdo.annotations.Query(name = "findByReferenceAndType", language = "JDOQL", value = "SELECT FROM org.estatio.dom.communicationchannel.CommunicationChannel WHERE (reference == :reference && type == :type)")
public abstract class CommunicationChannel extends EstatioTransactionalObject<CommunicationChannel, Status> implements WithNameGetter, WithReferenceGetter  {

    public CommunicationChannel() {
        // TODO: description is annotated as optional,
        // so it doesn't really make sense for it to be part of the natural sort
        // order
        super("type, description", Status.LOCKED, Status.UNLOCKED);
    }

    // //////////////////////////////////////

    private Status status;

    @MemberOrder(sequence = "4.5")
    @Disabled
    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public void setStatus(final Status status) {
        this.status = status;
    }


    // //////////////////////////////////////

    private CommunicationChannelType type;

    @Hidden
    public CommunicationChannelType getType() {
        return type;
    }

    public void setType(final CommunicationChannelType type) {
        this.type = type;
    }

    // //////////////////////////////////////

    /**
     * For import purposes only
     */
    private String reference;

    /**
     * For import purposes only
     */
    @Hidden
    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }
    

    // //////////////////////////////////////

    @Title
    @MemberOrder(sequence = "1")
    @Hidden(where = Where.OBJECT_FORMS)
    public abstract String getName();

    // //////////////////////////////////////

    private String description;

    @Optional
    @MemberOrder(sequence = "10")
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String disableDescription() {
        return getStatus().isLocked() ? "Cannot modify when locked" : null;
    }

    // //////////////////////////////////////

    private boolean legal;

    @MemberOrder(sequence = "10")
    public boolean isLegal() {
        return legal;
    }

    public void setLegal(final boolean Legal) {
        this.legal = Legal;
    }

    public String disableLegal() {
        return getStatus().isLocked() ? "Cannot modify when locked" : null;
    }

}
