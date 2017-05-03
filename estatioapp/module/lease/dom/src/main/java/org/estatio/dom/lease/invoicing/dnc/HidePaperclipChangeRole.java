/*
 *
 *  Copyright 2012-2015 Eurocommercial Properties NV
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

package org.estatio.dom.lease.invoicing.dnc;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.incode.module.document.dom.impl.paperclips.Paperclip_changeRole;

@DomainService(nature = NatureOfService.DOMAIN)
public class HidePaperclipChangeRole extends AbstractSubscriber {

    @com.google.common.eventbus.Subscribe
    public void on(Paperclip_changeRole.ActionDomainEvent ev) {
        switch (ev.getEventPhase()) {
        case HIDE:
            ev.hide();
        }
    }
}
