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
package org.estatio.module.lease.imports;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.isisaddons.module.excel.dom.ExcelService;

import org.estatio.module.base.dom.UdoDomainService;
import org.estatio.module.lease.dom.LeaseTermRepository;

@DomainService(
        menuOrder = "00",
        objectType = "org.estatio.app.services.lease.turnoverrent.LeaseTermForTurnoverRentService",
        nature = NatureOfService.DOMAIN
)
public class LeaseTermForTurnoverRentService extends UdoDomainService<LeaseTermForTurnoverRentService> {

    public LeaseTermForTurnoverRentService() {
        super(LeaseTermForTurnoverRentService.class);
    }

    // //////////////////////////////////////

    @PostConstruct
    public void init(final Map<String, String> properties) {
        super.init(properties);
        if (excelService == null) {
            throw new IllegalStateException("Require ExcelService to be configured");
        }
    }


    @javax.inject.Inject
    private ExcelService excelService;

    @javax.inject.Inject
    private LeaseTermRepository leaseTermRepository;


}
