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
package org.estatio.dom.guarantee;

import org.estatio.dom.agreement.AgreementRoleTypeData;
import org.estatio.dom.agreement.AgreementTypeData;

import lombok.AllArgsConstructor;
import lombok.Getter;

public final class GuaranteeConstants {

    private GuaranteeConstants() {
    }

    @AllArgsConstructor
    public enum AgreementType implements AgreementTypeData {
        GUARANTEE("Guarantee");

        @Getter
        private String title;
    }

    @AllArgsConstructor
    public enum AgreementRoleType implements AgreementRoleTypeData {
        GUARANTEE("Guarantee"), // One to whom a guaranty is made
        GUARANTOR("Guarantor"), //One who makes a guaranty
        BANK("Bank");

        @Getter
        private String title;

        public static class Meta {
            public final static int MAX_LEN = 30;
            private Meta() {}
        }
    }

}
