package com.eurocommercialproperties.estatio.viewer.wicket.app.scheduler;

import org.quartz.JobExecutionContext;

import com.danhaywood.ddd.domainservices.scheduler.AbstractIsisJob;
import com.eurocommercialproperties.estatio.dom.asset.Properties;

public class CountPropertiesJob extends AbstractIsisJob {

    protected void doExecute(JobExecutionContext context) {
        Properties properties = getService(Properties.class);
        int numProperties = properties.allProperties().size();
        
        System.out.println("number of properties is: " + numProperties);
    }

}
