/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.eurocommercialproperties.estatio.objstore.dflt;

import javax.jdo.annotations.PersistenceCapable;

import org.apache.isis.applib.annotation.Hidden;

import com.eurocommercialproperties.estatio.dom.asset.Property;
import com.eurocommercialproperties.estatio.dom.asset.Unit;
import com.eurocommercialproperties.estatio.dom.communicationchannel.CommunicationChannel;
import com.eurocommercialproperties.estatio.dom.communicationchannel.EmailAddress;
import com.eurocommercialproperties.estatio.dom.communicationchannel.FaxNumber;
import com.eurocommercialproperties.estatio.dom.communicationchannel.PhoneNumber;
import com.eurocommercialproperties.estatio.dom.communicationchannel.PostalAddress;
import com.eurocommercialproperties.estatio.dom.geography.Country;
import com.eurocommercialproperties.estatio.dom.geography.State;
import com.eurocommercialproperties.estatio.dom.index.Index;
import com.eurocommercialproperties.estatio.dom.index.IndexValue;
import com.eurocommercialproperties.estatio.dom.lease.Lease;
import com.eurocommercialproperties.estatio.dom.lease.LeaseItem;
import com.eurocommercialproperties.estatio.dom.party.Organisation;
import com.eurocommercialproperties.estatio.dom.party.Party;
import com.eurocommercialproperties.estatio.dom.party.Person;

/**
 * This is a temporary measure just to ensure that all the
 * {@link PersistenceCapable} entities are registered.
 * 
 * <p>
 * Once we have all the repos that reference these types, we can get rid of this
 * service.
 */
@Hidden
public class JdoRegistration {

    @Hidden
    public void registerToJdo(Property obj) {
    }

    @Hidden
    public void registerToJdo(Unit obj) {
    }

    @Hidden
    public void registerToJdo(CommunicationChannel obj) {
    }

    @Hidden
    public void registerToJdo(EmailAddress obj) {
    }

    @Hidden
    public void registerToJdo(FaxNumber obj) {
    }

    @Hidden
    public void registerToJdo(PostalAddress obj) {
    }

    @Hidden
    public void registerToJdo(PhoneNumber obj) {
    }

    @Hidden
    public void registerToJdo(State obj) {
    }

    @Hidden
    public void registerToJdo(Country obj) {
    }

    @Hidden
    public void registerToJdo(Organisation obj) {
    }

    @Hidden
    public void registerToJdo(Person obj) {
    }

    @Hidden
    public void registerToJdo(Party obj) {
    }

    @Hidden
    public void registerToJdo(Lease obj) {
    }

    @Hidden
    public void registerToJdo(LeaseItem obj) {
    }
    @Hidden
    public void registerToJdo(Index obj) {
    }
    @Hidden
    public void registerToJdo(IndexValue obj) {
    }



}
