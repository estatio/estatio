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
package org.estatio.module.agreement.dom;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AgreementRoleRepository_Test {

    public static class GetId {

        @Test
        public void getId() {
            assertThat(new AgreementRoleRepository().getId()).isEqualTo(AgreementRoleRepository.class.getName());
        }

    }

    public static class IconName {

        @Test
        public void iconName() {
            assertThat(new AgreementRoleRepository().iconName()).isEqualTo("AgreementRole");
        }

    }

}