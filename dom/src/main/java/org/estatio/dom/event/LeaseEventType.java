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
package org.estatio.dom.event;

import org.estatio.dom.Titled;

// TODO: is this in scope?
//  EST-130: convert to entity, since will vary by location
public enum LeaseEventType implements Titled {

    LEASE_BRK_OPT_LNDLRD("Break Option - Landlord"), 
    LEASE_BRK_OPT_MTL("Break Option - Mutual"), 
    LEASE_BRK_OPT_TNT("Break Option - Tenant"), 
    LEASE_EVENT("Event"), 
    LEASE_MEETING("Meeting"), 
    LEASE_PROLONGATION("Prolongation"), 
    LEASE_TASK("Task");

    private String title;

    private LeaseEventType(String title) {
        this.title = title;
    }

    public String title() {
        return title;
    }
}
