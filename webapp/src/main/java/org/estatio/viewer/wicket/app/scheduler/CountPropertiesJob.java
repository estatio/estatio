package org.estatio.viewer.wicket.app.scheduler;

import org.estatio.dom.asset.Properties;
import org.quartz.JobExecutionContext;

import com.danhaywood.ddd.domainservices.scheduler.AbstractIsisJob;

public class CountPropertiesJob extends AbstractIsisJob {

    protected void doExecute(JobExecutionContext context) {
        Properties properties = getService(Properties.class);
        int numProperties = properties.allProperties().size();
        
        System.out.println("number of properties is: " + numProperties);
    }

}
