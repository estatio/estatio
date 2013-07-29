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
package org.estatio.integration.glue;

import cucumber.api.java.en.Then;

import org.apache.isis.core.specsupport.specs.CukeGlueAbstract;

public interface ActionNoArgs {

    public static class Glue extends CukeGlueAbstract {
        
        @Then("^.*invoke the action$")
        public void invoke_the_no_arg_action() throws Throwable {

            nextTransaction();

            ActionNoArgs action = getVar("isis-action", null, ActionNoArgs.class);
            action.invoke();
        }
    }

    void invoke();
}