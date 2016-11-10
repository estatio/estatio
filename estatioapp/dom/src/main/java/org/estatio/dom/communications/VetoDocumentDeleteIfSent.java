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
package org.estatio.dom.communications;

import java.util.List;

import javax.inject.Inject;

import com.google.common.eventbus.Subscribe;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.i18n.TranslatableString;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannel;
import org.incode.module.communications.dom.impl.comms.Communication;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.Document_delete;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

import org.estatio.dom.UdoDomainRepositoryAndFactory;

@DomainService(nature = NatureOfService.DOMAIN)
public class VetoDocumentDeleteIfSent extends UdoDomainRepositoryAndFactory<CommunicationChannel> {

    public String getId() {
        return "estatio.VetoDocumentDeleteIfSent";
    }

    public VetoDocumentDeleteIfSent() {
        super(VetoDocumentDeleteIfSent.class, CommunicationChannel.class);
    }


    @Subscribe
    @Programmatic
    public void on(final Document_delete.ActionDomainEvent ev) {
        final Document document = (Document) ev.getMixedIn();
        switch (ev.getEventPhase()) {
        case DISABLE:
            final List<Paperclip> attachments = paperclipRepository.findByDocument(document);
            for (Paperclip attachment : attachments) {
                if(attachment.getAttachedTo() instanceof Communication) {
                    Communication communication = (Communication) attachment.getAttachedTo();
                    ev.veto(TranslatableString.tr("Document has already been sent as a communication"));
                }
            }
        }
    }


    @Inject
    PaperclipRepository paperclipRepository;

}
