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
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;

import org.joda.time.LocalDate;

import org.apache.isis.core.specsupport.specs.CukeGlueAbstract;
import org.apache.isis.core.specsupport.specs.V;

import org.estatio.dom.agreement.AgreementRole;
import org.estatio.integspecs.glue.ActionWithDateParameter;

public class AgreementRoleGlue_updateDates extends CukeGlueAbstract {
    
    public static class ChangeDatesAction extends CukeGlueAbstract implements ActionWithDateParameter {
        
        private final AgreementRole agreementRole;
        ChangeDatesAction(AgreementRole agreementRole) {
            this.agreementRole = agreementRole;
        }
        
        @Override
        public LocalDate defaultDateParameter(String paramName) {
            if("start date".equals(paramName)) {
                return agreementRole.default0ChangeDates();
            }
            if("end date".equals(paramName)) {
                return agreementRole.default1ChangeDates();
            }
            throw new IllegalArgumentException("Unknown parameter name '" + paramName + "'");
        }
        public void invoke(LocalDate startDate, LocalDate endDate) {
            try {
                wrap(agreementRole).changeDates(startDate, endDate);
            } catch(Exception ex) {
                putVar("exception", "exception", ex);
            }
        }
    }

    @Given("^.*want to update.* dates.*indicated agreement role$")
    public void I_want_to_update_the_dates_on_the_indicated_agreement_role() throws Throwable {
        final AgreementRole agreementRole = getVar("agreementRole", "indicated", AgreementRole.class);
        putVar("isis-action", "updateDates",  new ChangeDatesAction(agreementRole));
    }

    @When("^.*invoke the action, start date.* \"([^\"]*)\", end date.* \"([^\"]*)\"$")
    public void I_invoke_the_action_start_date_end_date(
            @Transform(V.LyyyyMMdd.class) LocalDate startDate, 
            @Transform(V.LyyyyMMdd.class) LocalDate endDate) throws Throwable {
        
        nextTransaction();
        
        ChangeDatesAction action = 
                getVar("isis-action", null, ChangeDatesAction.class);
        action.invoke(startDate, endDate);
    }    

}
