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
package org.estatio.webapp.services.scheduler;

import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseStatus;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.lease.status.LeaseStatusService;
import org.estatio.services.scheduler.AbstractIsisJob;

public class LeaseStatusJob extends AbstractIsisJob {

    final static Logger LOG = LoggerFactory.getLogger(LeaseStatusJob.class);

    protected void doExecute(JobExecutionContext context) {
        LeaseStatusService service = getService(LeaseStatusService.class);
        Leases leases = getService(Leases.class);
        int updatedCount = 0;
        LOG.info("Start Job");
        for (Lease lease : leases.allLeases()) {
            LeaseStatus oldStatus = lease.getStatus();
            service.refreshStatus(lease);
            if (lease.getStatus() != oldStatus) {
                updatedCount++;
            }
        }
        LOG.info(String.format("End Job, %d leases updated", updatedCount));
    }
}
