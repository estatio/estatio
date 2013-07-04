/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
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

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import org.apache.isis.applib.annotation.Hidden;
import org.quartz.JobDetail;
import org.quartz.Trigger;

import com.danhaywood.ddd.domainservices.scheduler.SchedulerService;

@Hidden
public class SchedulerServiceForEstatio extends SchedulerService {

    public SchedulerServiceForEstatio() {
        super("scheduler_user", "scheduler_role", "admin_role");
    }

    public void initializeJobs() {
        JobDetail job = newJob(CountPropertiesJob.class)
                .withIdentity("job1", "group1")
                .build();
        
        Trigger trigger = newTrigger()
            .withIdentity("trigger1", "group1")
            .startNow()
            .withSchedule(simpleSchedule()
                    .withIntervalInSeconds(20)
                    .repeatForever())            
            .build();

        scheduleJob(job, trigger);
    }

}
