package com.eurocommercialproperties.estatio.viewer.wicket.app.scheduler;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import org.quartz.JobDetail;
import org.quartz.Trigger;

import com.danhaywood.ddd.domainservices.scheduler.SchedulerService;

public class EstatioSchedulerService extends SchedulerService {

    public EstatioSchedulerService() {
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
