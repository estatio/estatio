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
package org.estatio.dom.asset;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.InheritanceStrategy;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.bookmark.BookmarkService;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelOwner;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelOwnerLink;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        schema = "dbo" // Isis' ObjectSpecId inferred @DomainObject#objectType
)
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.NEW_TABLE)
@DomainObject(
        objectType = "fixedasset.CommunicationChannelOwnerLinkForFixedAsset" // TODO: reconcile with schema
)
public class CommunicationChannelOwnerLinkForFixedAsset extends CommunicationChannelOwnerLink {

    @DomainService(nature = NatureOfService.DOMAIN)
    public static class InstantiationSubscriber extends AbstractSubscriber {

        @Programmatic
        @com.google.common.eventbus.Subscribe
        public void on(final CommunicationChannelOwnerLink.InstantiateEvent ev) {
            if(ev.getPolymorphicReference() instanceof FixedAsset) {
                ev.setSubtype(CommunicationChannelOwnerLinkForFixedAsset.class);
            }
        }
    }

    @Override
    public void setPolymorphicReference(final CommunicationChannelOwner polymorphicReference) {
        super.setPolymorphicReference(polymorphicReference);
        setFixedAsset((FixedAsset) polymorphicReference);
    }

    //region > fixedAsset (property)
    @Column(
            allowsNull = "false",
            name = "fixedAssetId"
    )
    @MemberOrder(sequence = "1")
    @Getter @Setter
    private FixedAsset fixedAsset;
    //endregion


    //region > injected services
    @javax.inject.Inject
    private BookmarkService bookmarkService;
    //endregion

}
