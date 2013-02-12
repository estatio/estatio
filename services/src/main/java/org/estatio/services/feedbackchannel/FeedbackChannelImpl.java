package org.estatio.services.feedbackchannel;

import org.apache.log4j.Logger;

import org.apache.isis.applib.AbstractService;

public class FeedbackChannelImpl extends AbstractService implements FeedbackChannel {
    public final static Logger LOG = Logger.getLogger(FeedbackChannelImpl.class);
    public void info(String message) {
        getContainer().informUser(message);  // for users' benefit
        LOG.info(message);  // for administrators' benefit
    }
    
    public void error(String message) {
        getContainer().warnUser(message);//??? something else more visible to the users
        LOG.error(message);  // for administrators' benefit
    }
}