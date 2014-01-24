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
package org.estatio.dom.lease;

import org.estatio.dom.Titled;

// TODO: is this in scope?
// EST-130: convert to entity, since will vary by location
//
// my idea is that these will become the values of Event#subjectEventType 
// (scoped by the subject to which the event refers)
//
// see BreakOption for a usage of this...
//
public enum LeaseEventType implements Titled {

    LEASE_OTHER("Other"), 
    LEASE_MEETING("Meeting"), 
    LEASE_PROLONGATION("Prolongation"), 
    LEASE_TASK("Task");

    private String title;

    private LeaseEventType(final String title) {
        this.title = title;
    }

    public String title() {
        return title;
    }
}
