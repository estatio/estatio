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
package org.estatio.integspecs.glue;

import com.google.common.base.Objects;

import cucumber.api.Transform;
import cucumber.api.java.en.Then;

import org.joda.time.LocalDate;
import org.junit.Assert;

import org.apache.isis.core.specsupport.specs.CukeGlueAbstract;
import org.apache.isis.core.specsupport.specs.V;

public interface ActionWithDateParameter {

    public abstract LocalDate defaultDateParameter(String paramName);
    
    public static class Glue extends CukeGlueAbstract {
        
        @Then("^.*default for.* \"([^\"]*)\" date parameter.* \"([^\"]*)\"$")
        public void the_default_for_date_parameter_is(
                String paramName, 
                @Transform(V.LyyyyMMdd.class) final LocalDate expectedDate) throws Throwable {

            nextTransaction();

            ActionWithDateParameter action = getVar("isis-action", null, ActionWithDateParameter.class);
            LocalDate actualDate = action.defaultDateParameter(paramName);
            Assert.assertTrue(Objects.equal(expectedDate, actualDate));
        }
    }
}