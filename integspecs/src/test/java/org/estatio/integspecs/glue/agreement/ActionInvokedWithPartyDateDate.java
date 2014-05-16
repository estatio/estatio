/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.estatio.integspecs.glue.agreement;

import cucumber.api.Transform;
import cucumber.api.java.en.When;

import org.joda.time.LocalDate;

import org.apache.isis.core.specsupport.specs.CukeGlueAbstract;
import org.apache.isis.core.specsupport.specs.V;

import org.estatio.dom.party.Party;
import org.estatio.integspecs.spectransformers.EMO;

interface ActionInvokedWithPartyDateDate {
    public void invoke(Party party, LocalDate startDate, LocalDate endDate);
    
    public static class Glue extends CukeGlueAbstract {
        @When("^.*invoke.* action,.* start date.* \"([^\"]*)\",.* end date.* \"([^\"]*)\",.* party.* \"([^\"]*)\"$")
        public void I_invoke_the_action_with_start_date_end_date_party(
                @Transform(V.LyyyyMMdd.class) LocalDate startDate, 
                @Transform(V.LyyyyMMdd.class) LocalDate endDate, 
                @Transform(EMO.Party.class) Party party) throws Throwable {
            
            nextTransaction();
            
            ActionInvokedWithPartyDateDate action = 
                    getVar("isis-action", null, ActionInvokedWithPartyDateDate.class);
            action.invoke(party, startDate, endDate);
        }
    }
}