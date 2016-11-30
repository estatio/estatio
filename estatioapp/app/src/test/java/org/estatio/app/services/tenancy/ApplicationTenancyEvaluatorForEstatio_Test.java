/*
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
package org.estatio.app.services.tenancy;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ApplicationTenancyEvaluatorForEstatio_Test {

    final String globalObject = "/";
    final String italianObject = "/ITA";
    final String italianCaraselloObject = "/ITA/CAR";

    final String globalUser = "/";
    final String italianUser = "/ITA";
    final String italianCaraselloUser = "/ITA/CAR";
    final String italianXCaraselloUser = "/ITA/X-CAR";
    final String italianIgigliUser = "/ITA/GIG";

    final String frenchUser = "/FRA";

    ApplicationTenancyEvaluatorForEstatio evaluator;

    @Before
    public void setUp() throws Exception {
        evaluator = new ApplicationTenancyEvaluatorForEstatio();
    }

    @Test
    public void testObjectVisibleToUser() throws Exception {

        assertThat(evaluator.objectVisibleToUser(globalObject, globalUser)).isTrue();
        assertThat(evaluator.objectVisibleToUser(globalObject, italianUser)).isTrue();
        assertThat(evaluator.objectVisibleToUser(globalObject, frenchUser)).isTrue();
        assertThat(evaluator.objectVisibleToUser(globalObject, italianCaraselloUser)).isTrue();
        assertThat(evaluator.objectVisibleToUser(globalObject, italianXCaraselloUser)).isTrue();
        assertThat(evaluator.objectVisibleToUser(globalObject, italianIgigliUser)).isTrue();

        assertThat(evaluator.objectVisibleToUser(italianObject, globalUser)).isTrue();
        assertThat(evaluator.objectVisibleToUser(italianObject, italianUser)).isTrue();
        assertThat(evaluator.objectVisibleToUser(italianObject, frenchUser)).isFalse();
        assertThat(evaluator.objectVisibleToUser(italianObject, italianCaraselloUser)).isTrue();
        assertThat(evaluator.objectVisibleToUser(italianObject, italianXCaraselloUser)).isTrue();
        assertThat(evaluator.objectVisibleToUser(italianObject, italianIgigliUser)).isTrue();

        assertThat(evaluator.objectVisibleToUser(italianCaraselloObject, globalUser)).isTrue();
        assertThat(evaluator.objectVisibleToUser(italianCaraselloObject, italianUser)).isTrue();
        assertThat(evaluator.objectVisibleToUser(italianCaraselloObject, frenchUser)).isFalse();
        assertThat(evaluator.objectVisibleToUser(italianCaraselloObject, italianCaraselloUser)).isTrue();
        assertThat(evaluator.objectVisibleToUser(italianCaraselloObject, italianXCaraselloUser)).isTrue();  // would be false normally...
        assertThat(evaluator.objectVisibleToUser(italianCaraselloObject, italianIgigliUser)).isFalse();

    }

    @Test
    public void testObjectEnabledForUser() throws Exception {

        assertThat(evaluator.objectEnabledForUser(globalObject, globalUser)).isTrue();
        assertThat(evaluator.objectEnabledForUser(globalObject, italianUser)).isFalse();
        assertThat(evaluator.objectEnabledForUser(globalObject, frenchUser)).isFalse();
        assertThat(evaluator.objectEnabledForUser(globalObject, italianCaraselloUser)).isFalse();
        assertThat(evaluator.objectEnabledForUser(globalObject, italianXCaraselloUser)).isFalse();
        assertThat(evaluator.objectEnabledForUser(globalObject, italianIgigliUser)).isFalse();

        assertThat(evaluator.objectEnabledForUser(italianObject, globalUser)).isTrue();
        assertThat(evaluator.objectEnabledForUser(italianObject, italianUser)).isTrue();
        assertThat(evaluator.objectEnabledForUser(italianObject, frenchUser)).isFalse();
        assertThat(evaluator.objectEnabledForUser(italianObject, italianCaraselloUser)).isFalse();
        assertThat(evaluator.objectEnabledForUser(italianObject, italianXCaraselloUser)).isFalse();
        assertThat(evaluator.objectEnabledForUser(italianObject, italianIgigliUser)).isFalse();

        assertThat(evaluator.objectEnabledForUser(italianCaraselloObject, globalUser)).isTrue();
        assertThat(evaluator.objectEnabledForUser(italianCaraselloObject, italianUser)).isTrue();
        assertThat(evaluator.objectEnabledForUser(italianCaraselloObject, frenchUser)).isFalse();
        assertThat(evaluator.objectEnabledForUser(italianCaraselloObject, italianCaraselloUser)).isTrue();
        assertThat(evaluator.objectEnabledForUser(italianCaraselloObject, italianXCaraselloUser)).isTrue(); // would be false normally...
        assertThat(evaluator.objectEnabledForUser(italianCaraselloObject, italianIgigliUser)).isFalse();

    }

}