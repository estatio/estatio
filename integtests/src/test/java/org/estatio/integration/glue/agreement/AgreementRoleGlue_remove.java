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
package org.estatio.integration.glue.agreement;

import cucumber.api.PendingException;
import cucumber.api.Transform;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;

import org.joda.time.LocalDate;

import org.apache.isis.core.specsupport.specs.CukeGlueAbstract;
import org.apache.isis.core.specsupport.specs.V;

import org.estatio.dom.agreement.AgreementRole;
import org.estatio.integration.glue.ActionNoArgs;

public class AgreementRoleGlue_remove extends CukeGlueAbstract {
    
    class RemoveAction implements ActionNoArgs {
        
        private final AgreementRole agreementRole;
        RemoveAction(AgreementRole agreementRole) {
            this.agreementRole = agreementRole;
        }
        
        @Override
        public void invoke() {
            throw new PendingException();
        }
        
    }
    
    @Given("^.*want to remove.* indicated agreement role$")
    public void I_want_to_update_the_dates_on_the_indicated_agreement_role() throws Throwable {
        final AgreementRole agreementRole = getVar("agreementRole", "indicated", AgreementRole.class);
        putVar("isis-action", "updateDates",  new RemoveAction(agreementRole));
    }

}
