/*
 *  Copyright 2012-2013 Eurocommercial Properties NV
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
package org.estatio.integration;

import cucumber.api.junit.Cucumber;

import org.junit.runner.RunWith;

/**
 * Base class for all Cucumber specs run at integration-scope; 
 * runs the spec as a JUnit test.
 * 
 * <p>
 * The similarity between this class' responsibilities and {@link EstatioIntegrationTest} is
 * no accident. 
 */
@RunWith(Cucumber.class)
@Cucumber.Options(
        format = {
                "html:target/cucumber-html-report"
                // addHook causes an exception to be thrown if this reporter is registered...
                // ,"json-pretty:target/cucumber-json-report.json"
        },
        strict = true,
        tags = { "~@backlog" })
public abstract class EstatioIntegrationCukeSpecs {


}
