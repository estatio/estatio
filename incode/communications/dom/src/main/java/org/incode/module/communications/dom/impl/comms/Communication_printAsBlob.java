/*
 *  Copyright 2016 Dan Haywood
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
package org.incode.module.communications.dom.impl.comms;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.value.Blob;

import org.incode.module.communications.dom.mixins.DocumentConstants;
import org.incode.module.documents.dom.impl.docs.Document;
import org.incode.module.documents.dom.impl.docs.Document_downloadExternalUrlAsBlob;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelType;

@Mixin
public class Communication_printAsBlob  {

    private final Communication communication;

    public Communication_printAsBlob(final Communication communication) {
        this.communication = communication;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(named = "Print")
    public Blob $$() {
        communication.sent();
        final Document enclosed = communication.findDocument(DocumentConstants.PAPERCLIP_ROLE_ENCLOSED);
        switch (enclosed.getSort()) {
            case BLOB:
                return enclosed.getBlob();
            case EXTERNAL_BLOB:
                return factoryService.mixin(Document_downloadExternalUrlAsBlob.class, enclosed).$$();
        }
        // not expected
        return null;
    }

    public boolean hide$$() {
        if(communication.getType() != CommunicationChannelType.POSTAL_ADDRESS) {
            return true;
        }
        final Document enclosed = communication.findDocument(DocumentConstants.PAPERCLIP_ROLE_ENCLOSED);
        if(enclosed == null) {
            return false; // picked up by disabled$$
        }
        switch (enclosed.getSort()) {
            case BLOB:
            case EXTERNAL_BLOB:
                return false;
            default:
                return true;
        }
    }

    public String disable$$() {
        final Document enclosed = communication.findDocument(DocumentConstants.PAPERCLIP_ROLE_ENCLOSED);
        if(enclosed == null) {
            return "Cannot locate the 'enclosed' Document";
        }
        return null;
    }


    @Inject
    FactoryService factoryService;



}
