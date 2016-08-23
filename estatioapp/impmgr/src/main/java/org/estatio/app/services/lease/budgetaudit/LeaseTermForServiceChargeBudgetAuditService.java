/*
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
package org.estatio.app.services.lease.budgetaudit;

import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.isisaddons.module.excel.dom.ExcelService;
import org.joda.time.LocalDate;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.annotation.NotContributed.As;
import org.apache.isis.applib.services.bookmark.BookmarkService;
import org.estatio.dom.UdoDomainService;
import org.estatio.dom.asset.Property;
import org.estatio.dom.lease.LeaseTermRepository;

@DomainService(menuOrder = "00")
@Immutable
public class LeaseTermForServiceChargeBudgetAuditService extends UdoDomainService<LeaseTermForServiceChargeBudgetAuditService> {

    public LeaseTermForServiceChargeBudgetAuditService() {
        super(LeaseTermForServiceChargeBudgetAuditService.class);
    }

    // //////////////////////////////////////

    @PostConstruct
    public void init(final Map<String, String> properties) {
        super.init(properties);
        if (bookmarkService == null) {
            throw new IllegalStateException("Require BookmarkService to be configured");
        }
        if (excelService == null) {
            throw new IllegalStateException("Require ExcelService to be configured");
        }
    }

    // //////////////////////////////////////

    @NotContributed(As.ASSOCIATION)
    // ie *is* contributed as action
    @NotInServiceMenu
    public LeaseTermForServiceChargeBudgetAuditManager maintainServiceCharges(
            final Property property,
            @Named("Start date") final LocalDate startDate) {
        return new LeaseTermForServiceChargeBudgetAuditManager(property, startDate);
    }

    public List<LocalDate> choices1MaintainServiceCharges(final Property property) {
        return leaseTermRepository.findServiceChargeDatesByProperty(property);
    }

    // //////////////////////////////////////

    @javax.inject.Inject
    private ExcelService excelService;

    @javax.inject.Inject
    private BookmarkService bookmarkService;

    @javax.inject.Inject
    private LeaseTermRepository leaseTermRepository;

}
