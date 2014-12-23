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

import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;

import org.apache.isis.core.specsupport.specs.CukeGlueAbstract;

import org.estatio.dom.agreement.AgreementRole;

public class AgreementRoleGlue_replace extends CukeGlueAbstract {
    
    public static class ReplaceAction extends CukeGlueAbstract {
        
        private final AgreementRole agreementRole;
        ReplaceAction(AgreementRole agreementRole) {
            this.agreementRole = agreementRole;
        }
                
    }
    
    @Given("^.*want to replace.* indicated agreement role.*$")
    public void I_want_to_replace_indicated_agreement_role() throws Throwable {
        final AgreementRole agreementRole = getVar("agreementRole", "indicated", AgreementRole.class);
        putVar("isis-action", "replace",  new ReplaceAction(agreementRole));
    }

    @When("^.*invoke the action.* direction.* \"([^\"]*)\"$")
    public void I_invoke_the_action_direction(
            String direction) throws Throwable {
        
        nextTransaction();
        
        ReplaceAction action = 
                getVar("isis-action", null, ReplaceAction.class);
        
    }    
}
