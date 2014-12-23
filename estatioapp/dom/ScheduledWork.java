/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
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

package org.estatio.services.scheduler.work;


import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.collect.Iterables;

import org.estatio.dom.EstatioMutableObject;
import org.estatio.services.scheduler.work.ScheduledWorkItem.ItemState;
import org.joda.time.LocalDateTime;

import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.MultiLine;
import org.apache.isis.applib.annotation.TypicalLength;
import org.apache.isis.applib.util.TitleBuffer;

@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
public class ScheduledWork extends EstatioMutableObject<ScheduledWork> {

    public static enum State {
        QUEUED, IN_PROGRESS, SUCCEEDED, FAILURES
    }

    // //////////////////////////////////////

    public ScheduledWork() {
        super("submittedOn");
    }
    
    // //////////////////////////////////////
    
    public String title() {
        final TitleBuffer buf = new TitleBuffer();
        return buf.toString();
    }


    // //////////////////////////////////////

    private String description;

    @javax.jdo.annotations.Column(allowsNull="true", length=80)
    @TypicalLength(30)
    @Disabled
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }
    
    
    // //////////////////////////////////////
    
    private String commandText;
    
    @javax.jdo.annotations.Column(allowsNull="false", length=255)
    @MultiLine(numberOfLines=8)
    @Disabled
    public String getCommandText() {
        return commandText;
    }
    
    public void setCommandText(final String commandText) {
        this.commandText = commandText;
    }


    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent
    private LocalDateTime submittedOn;

    @javax.jdo.annotations.Column(allowsNull="false")
    @Disabled
    public LocalDateTime getSubmittedOn() {
        return submittedOn;
    }

    public void setSubmittedOn(final LocalDateTime submittedOn) {
        this.submittedOn = submittedOn;
    }


    // //////////////////////////////////////

    private String submittedBy;

    @javax.jdo.annotations.Column(allowsNull="false", length=50)
    @Disabled
    public String getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(final String submittedBy) {
        this.submittedBy = submittedBy;
    }


    
    // //////////////////////////////////////


    @javax.jdo.annotations.Column(allowsNull="false", length=20)
    public State getState() {
        final int size = getWorkItems().size();
        final int numFailed = numItemsInState(getWorkItems(), ItemState.FAILED);
        final int numSucceeded = numItemsInState(getWorkItems(), ItemState.SUCCEEDED);
        final int numQueued = numItemsInState(getWorkItems(), ItemState.QUEUED);
        if(numFailed > 0) {
            return State.FAILURES;
        }
        if(numSucceeded == size) {
            return State.SUCCEEDED;
        }
        if(numQueued == size) {
            return State.QUEUED;
        }
        return State.IN_PROGRESS;
    }

    private static int numItemsInState(Iterable<ScheduledWorkItem> workItems, ItemState state) {
        return Iterables.size(Iterables.filter(workItems, ScheduledWorkItem.Predicates.inState(state)));
    }

    
    // //////////////////////////////////////
    // workItems (collection)
    // //////////////////////////////////////

    @Persistent(mappedBy = "batch", dependentElement = "true")
    private SortedSet<ScheduledWorkItem> workItems = new TreeSet<ScheduledWorkItem>();

    @MemberOrder(sequence = "1")
    public SortedSet<ScheduledWorkItem> getWorkItems() {
        return workItems;
    }

    public void setWorkItems(final SortedSet<ScheduledWorkItem> workItems) {
        this.workItems = workItems;
    }

    
    // //////////////////////////////////////

}
