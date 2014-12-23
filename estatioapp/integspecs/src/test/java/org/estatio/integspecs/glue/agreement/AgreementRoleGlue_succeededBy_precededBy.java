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

import cucumber.api.java.en.When;

import org.joda.time.LocalDate;

import org.apache.isis.core.specsupport.specs.CukeGlueAbstract;

import org.estatio.dom.agreement.AgreementRole;
import org.estatio.dom.party.Party;
import org.estatio.integspecs.glue.ActionWithDateParameter;

public class AgreementRoleGlue_succeededBy_precededBy extends CukeGlueAbstract {
    
    public static class SucceededByAction extends CukeGlueAbstract 
                                        implements ActionWithDateParameter, ActionInvokedWithPartyDateDate {
        private final AgreementRole agreementRole;
        SucceededByAction(AgreementRole agreementRole) {
            this.agreementRole = agreementRole;
        }
        
        @Override
        public LocalDate defaultDateParameter(String paramName) {
            if("start date".equals(paramName)) {
                return agreementRole.default1SucceededBy();
            }
            throw new IllegalArgumentException("Unknown parameter name '" + paramName + "'");
        }
        public void invoke(Party party, LocalDate startDate, LocalDate endDate) {
            try {
                wrap(agreementRole).succeededBy(party, startDate, endDate);
            } catch(Exception ex) {
                putVar("exception", "exception", ex);
            }
        }
    }
    
    public static class PrecededByAction extends CukeGlueAbstract
                                        implements ActionWithDateParameter,ActionInvokedWithPartyDateDate {
        private final AgreementRole agreementRole;
        PrecededByAction(AgreementRole agreementRole) {
            this.agreementRole = agreementRole;
        }
        public LocalDate defaultDateParameter(String paramName) {
            if("end date".equals(paramName)) {
                return agreementRole.default2PrecededBy();
            }
            throw new IllegalArgumentException("Unknown parameter name '" + paramName + "'");
        }
        public void invoke(Party party, LocalDate startDate, LocalDate endDate) {
            try {
                wrap(agreementRole).precededBy(party, startDate, endDate);
            } catch(Exception ex) {
                putVar("exception", "exception", ex);
            }
        }
    }

    @When("^.*want to.*predecessor.*indicated agreement role$")
    public void I_want_to_add_a_predecessor_to_the_indicated_agreement_role() throws Throwable {
        final AgreementRole agreementRole = getVar("agreementRole", "indicated", AgreementRole.class);
        putVar("isis-action", "precededBy",  new PrecededByAction(agreementRole));
    }
    
    @When("^.*want to.*successor.*indicated agreement role$")
    public void I_want_to_add_a_successor_to_the_indicated_agreement_role() throws Throwable {
        final AgreementRole agreementRole = getVar("agreementRole", "indicated", AgreementRole.class);
        putVar("isis-action", "succeededBy",  new SucceededByAction(agreementRole));
    }

}
