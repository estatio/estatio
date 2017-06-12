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

import org.estatio.dom.agreement.IAgreementRoleCommunicationChannelType;

import lombok.AllArgsConstructor;
import lombok.Getter;

public final class LeaseConstants {

    private LeaseConstants() {}

    @AllArgsConstructor
    public enum AgreementRoleCommunicationChannelType implements IAgreementRoleCommunicationChannelType {
        INVOICE_ADDRESS("Invoice Address"),
        ADMINISTRATION_ADDRESS("Administration Address");

        @Getter
        private String title;

        public boolean equalTo(final IAgreementRoleCommunicationChannelType agreementRoleCommunicationChannelType){
            return getTitle().equals(agreementRoleCommunicationChannelType.getTitle());
        }

    }

}
