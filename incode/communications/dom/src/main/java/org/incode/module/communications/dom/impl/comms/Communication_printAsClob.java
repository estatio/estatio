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
import org.apache.isis.applib.value.Clob;

import org.incode.module.communications.dom.mixins.DocumentConstants;
import org.incode.module.documents.dom.impl.docs.Document;
import org.incode.module.documents.dom.impl.docs.DocumentAbstract_downloadTextAsClob;
import org.incode.module.documents.dom.impl.docs.Document_downloadExternalUrlAsClob;

import org.estatio.dom.communicationchannel.CommunicationChannelType;

@Mixin
public class Communication_printAsClob {

    private final Communication communication;

    public Communication_printAsClob(final Communication communication) {
        this.communication = communication;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(named = "Print")
    public Clob $$() {
        communication.sent();
        final Document enclosed = communication.findDocument(DocumentConstants.PAPERCLIP_ROLE_ENCLOSED);
        switch (enclosed.getSort()) {
            case CLOB:
                return enclosed.getClob();
            case TEXT:
                return factoryService.mixin(DocumentAbstract_downloadTextAsClob.class, enclosed).$$();
            case EXTERNAL_CLOB:
                    return factoryService.mixin(Document_downloadExternalUrlAsClob.class, enclosed).$$();
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
            case CLOB:
            case EXTERNAL_CLOB:
            case TEXT:
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
