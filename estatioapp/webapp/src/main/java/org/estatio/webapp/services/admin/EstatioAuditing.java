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
package org.estatio.webapp.services.admin;

import java.util.List;
import org.isisaddons.module.audit.dom.AuditEntry;
import org.isisaddons.module.audit.dom.AuditingServiceRepository;
import org.joda.time.LocalDate;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.services.clock.ClockService;
import org.estatio.dom.EstatioService;

@DomainService
@DomainServiceLayout(
        named = "Changes",
        menuBar = DomainServiceLayout.MenuBar.SECONDARY,
        menuOrder = "20.2"
)
public class EstatioAuditing extends EstatioService<EstatioAuditing> {

    public EstatioAuditing() {
        super(EstatioAuditing.class);
    }
    
    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence="1")
    public List<AuditEntry> findAuditEntries(
            final @Optional @Named("From") LocalDate from,
            final @Optional @Named("To") LocalDate to) {
        return auditingServiceRepository.findByFromAndTo(from, to);
    }
    public boolean hideFindAuditEntries() {
        return auditingServiceRepository == null;
    }
    public LocalDate default0FindAuditEntries() {
        return clockService.now().minusDays(7);
    }
    public LocalDate default1FindAuditEntries() {
        return clockService.now();
    }

    // //////////////////////////////////////

    @javax.inject.Inject
    private AuditingServiceRepository auditingServiceRepository;
    
    @javax.inject.Inject
    private ClockService clockService;

}

