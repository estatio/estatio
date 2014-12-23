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
package org.estatio.dom.bankmandate;


public final class BankMandateConstants {

    private BankMandateConstants() {}
    
    public final static String AT_MANDATE = "Mandate";
    
    public final static String ART_CREDITOR = "Creditor"; 
    public final static String ART_DEBTOR = "Debtor";
    public final static String ART_OWNER = "Owner";

    // TODO: what are the AgreementRoleCommunicationChannelTypes for BankMandates 
    // (or more generally, any subtype of Agreement defined by the financial module)?
    public final static String ARCCT_FOO_ADDRESS = "Foo Address";
    public final static String ARCCT_BAR_ADDRESS = "Bar Address";

}
