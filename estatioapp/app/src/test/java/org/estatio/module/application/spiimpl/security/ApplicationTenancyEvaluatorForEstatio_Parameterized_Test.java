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
package org.estatio.module.application.spiimpl.security;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class ApplicationTenancyEvaluatorForEstatio_Parameterized_Test {

    private final String objectAtPath;
    private final String userAtPath;
    private final boolean visible;
    private final boolean enabled;

    @Parameterized.Parameters
    public static Collection<Object[]> values() {
        return Arrays.asList(
                new Object[][]{
                        {"/", "/", true, true},
                        {"/", "/ITA", true, false},
                        {"/", "/FRA", true, false},
                        {"/", "/ITA/CAR", true, false},
                        {"/", "/ITA/X-CAR", true, false},
                        {"/", "/ITA/GIG", true, false},

                        {"/ITA", "/", true, true},
                        {"/ITA", "/ITA", true, true},
                        {"/ITA", "/FRA", false, false},
                        {"/ITA", "/ITA/CAR", true, false},
                        {"/ITA", "/ITA/X-CAR", true, false},
                        {"/ITA", "/ITA/GIG", true, false},

                        {"/ITA/CAR", "/", true, true},
                        {"/ITA/CAR", "/ITA", true, true},
                        {"/ITA/CAR", "/FRA", false, false},
                        {"/ITA/CAR", "/ITA/CAR", true, true},
                        {"/ITA/CAR", "/ITA/X-CAR", true, true}, // would be false normally
                        {"/ITA/CAR", "/ITA/GIG", false, false},

                        {"/FRA", "/FRA;/BEL", true, true},
                        {"/BEL", "/FRA;/BEL", true, true},
                        {"/ITA", "/FRA;/BEL", false, false},

                        {"/FRA", " /FRA ; /BEL ", true, true},
                        {"/BEL", " /FRA ; /BEL ", true, true},
                        {"/ITA", " /FRA; /BEL ", false, false},

                        {"/FRA;/BEL", "/FRA", true, true},
                        {"/FRA;/BEL", "/ITA", false, false},
                }
        );
    }

    public ApplicationTenancyEvaluatorForEstatio_Parameterized_Test(
            String objectAtPath, String userAtPath, boolean visible, boolean enabled) {
        this.objectAtPath = objectAtPath;
        this.userAtPath = userAtPath;
        this.visible = visible;
        this.enabled = enabled;
    }

    @Test
    public void execute() throws Exception {
        ApplicationTenancyEvaluatorForEstatio evaluator = new ApplicationTenancyEvaluatorForEstatio();

        assertThat(evaluator.objectVisibleToUser(objectAtPath, userAtPath), is(equalTo(visible)));
        assertThat(evaluator.objectEnabledForUser(objectAtPath, userAtPath), is(equalTo(enabled)));

    }

}