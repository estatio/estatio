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


import java.math.BigDecimal;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.base.Predicate;

import org.estatio.dom.EstatioMutableObject;
import org.estatio.services.scheduler.work.ScheduledWorkItem.ItemState;
import org.joda.time.DurationFieldType;
import org.joda.time.LocalDateTime;
import org.joda.time.Seconds;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Bulk;
import org.apache.isis.applib.annotation.DescribedAs;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.MultiLine;
import org.apache.isis.applib.annotation.NotPersisted;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.publish.EventMetadata;
import org.apache.isis.applib.services.publish.EventType;

@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
public class ScheduledWorkItem extends EstatioMutableObject<ScheduledWorkItem> {

    public static enum ItemState {
        QUEUED, IN_PROGRESS, SUCCEEDED, FAILED
    }

    // //////////////////////////////////////

    public ScheduledWorkItem() {
        super("work,sequence");
    }
    
    // //////////////////////////////////////
    
    @javax.jdo.annotations.Persistent
    private ScheduledWork work;

    public ScheduledWork getWork() {
        return work;
    }

    public void setWork(final ScheduledWork batch) {
        this.work = batch;
    }


    // //////////////////////////////////////
    
    @javax.jdo.annotations.Persistent
    private LocalDateTime startedAt;

    /**
     * The date/time that the scheduled started running this work item.
     */
    @javax.jdo.annotations.Column(allowsNull="true")
    @Disabled
    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(final LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    // //////////////////////////////////////
    
    @javax.jdo.annotations.Persistent
    private LocalDateTime completedAt;
    
    /**
     * The date/time that the scheduler completed running this work item.
     */
    @javax.jdo.annotations.Column(allowsNull="true")
    @Disabled
    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
    
    public void setCompletedAt(final LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
    
    // //////////////////////////////////////

    
    /**
     * Number of seconds taken for this work item to complete.
     * @return
     */
    @javax.jdo.annotations.Column(allowsNull="true")
    public Integer getDuration() {
        if(getStartedAt() == null || getCompletedAt() == null) {
            return null;
        }
        Seconds seconds = Seconds.secondsBetween(getStartedAt(), getCompletedAt());
        return seconds.getSeconds();
    }

    
    // //////////////////////////////////////

    private int sequence;

    /**
     * Hidden (<tt>@Programmatic</tt>) because information also available in the {@link #getId() id}.
     * 
     * @see #getId()
     */
    @Programmatic
    public int getSequence() {
        return sequence;
    }

    public void setSequence(final int sequence) {
        this.sequence = sequence;
    }
    
    // //////////////////////////////////////

    private EventType eventType;

    @javax.jdo.annotations.Column(allowsNull="false")
    @MemberOrder(sequence = "3")
    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(final EventType eventType) {
        this.eventType = eventType;
    }
    
//    // //////////////////////////////////////
//
//    private String user;
//    
//    @javax.jdo.annotations.Column(allowsNull="false", length=50)
//    @MemberOrder(sequence = "4")
//    public String getUser() {
//        return user;
//    }
//    
//    public void setUser(final String user) {
//        this.user = user;
//    }
    
    // //////////////////////////////////////

    private ItemState state;

    @javax.jdo.annotations.Column(allowsNull="false", length=20)
    @MemberOrder(sequence = "5")
    public ItemState getState() {
        return state;
    }

    public void setState(final ItemState state) {
        this.state = state;
    }
    private ScheduledWorkItem setStateAndReturn(ItemState state) {
        setState(state);
        return this;
    }
    
    // //////////////////////////////////////

//    @javax.jdo.annotations.NotPersistent
//    @NotPersisted
//    @MultiLine(numberOfLines=20)
//    @Hidden(where=Where.ALL_TABLES)
//    @MemberOrder(sequence = "6")
//    public String getSerializedForm() {
//        return IoUtils.fromUtf8ZippedBytes("serializedForm", getSerializedFormZipped());
//    }
//
//    public void setSerializedForm(final String serializedForm) {
//        final byte[] zippedBytes = IoUtils.toUtf8ZippedBytes("serializedForm", serializedForm);
//        setSerializedFormZipped(zippedBytes);
//    }
//    
//    // //////////////////////////////////////
//
//    @javax.jdo.annotations.Column
//    private byte[] serializedFormZipped;
//
//    @Programmatic // ignored by Isis
//    public byte[] getSerializedFormZipped() {
//        return serializedFormZipped;
//    }
//
//    public void setSerializedFormZipped(final byte[] serializedFormZipped) {
//        this.serializedFormZipped = serializedFormZipped;
//    }
//    
//    // //////////////////////////////////////
//
// 
//    @Bulk
//    @ActionSemantics(Of.IDEMPOTENT)
//    @MemberOrder(sequence="10")
//    public ScheduledWorkItem processed() {
//        return setStateAndReturn(ItemState.IN_PROGRESS);
//    }
//
//
//    @Bulk
//    @ActionSemantics(Of.IDEMPOTENT)
//    @MemberOrder(sequence="11")
//    public ScheduledWorkItem reQueue() {
//        return setStateAndReturn(ItemState.QUEUED);
//    }
//
//    @Bulk
//    @MemberOrder(sequence="12")
//    public void delete() {
//        container.removeIfNotAlready(this);
//    }
//
    
    // //////////////////////////////////////

    public static class Predicates {
        private Predicates(){}
        public static Predicate<ScheduledWorkItem> inState(final ItemState state) {
            return new Predicate<ScheduledWorkItem>() {
                @Override
                public boolean apply(ScheduledWorkItem input) {
                    return input.getState() == state;
                }
            };
        }
    }


}
