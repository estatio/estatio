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
package org.estatio.app.lease.turnoverrent;

import java.util.List;

import javax.annotation.PostConstruct;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotContributed;
import org.apache.isis.applib.annotation.NotContributed.As;
import org.apache.isis.applib.annotation.NotInServiceMenu;

import org.isisaddons.module.excel.dom.ExcelService;

import org.estatio.dom.EstatioService;
import org.estatio.dom.asset.Property;
import org.estatio.dom.lease.LeaseItemType;
import org.estatio.dom.lease.LeaseTerms;

@DomainService(menuOrder = "00")
@Immutable
public class LeaseTermForTurnoverRentService extends EstatioService<LeaseTermForTurnoverRentService> {

    public LeaseTermForTurnoverRentService() {
        super(LeaseTermForTurnoverRentService.class);
    }

    // //////////////////////////////////////

    @PostConstruct
    public void init() {
        if (excelService == null) {
            throw new IllegalStateException("Require ExcelService to be configured");
        }
    }

    // //////////////////////////////////////

    @NotContributed(As.ASSOCIATION)
    // ie *is* contributed as action
    @NotInServiceMenu
    public LeaseTermForTurnoverRentManager maintainTurnoverRent(
            final Property property,
            @Named("Start date") final LocalDate startDate) {
        LeaseTermForTurnoverRentManager template = new LeaseTermForTurnoverRentManager();
        template.setProperty(property);
        template.setStartDate(startDate);
        return template;
    }

    public List<LocalDate> choices1MaintainTurnoverRent(final Property property) {
        return leaseTerms.findStartDatesByPropertyAndType(property, LeaseItemType.TURNOVER_RENT);
    }

    // //////////////////////////////////////

    @javax.inject.Inject
    private ExcelService excelService;

    @javax.inject.Inject
    private LeaseTerms leaseTerms;

}
