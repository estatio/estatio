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
package org.estatio.integtest;

import java.util.List;

import org.estatio.integtest.specs.EstatioApp;
import org.estatio.integtest.specs.EstatioScenario;

import cucumber.api.DataTable;
import cucumber.api.java.After;
import cucumber.api.java.Before;


public class AbstractEstatioCukeStepDefs {

    protected final EstatioScenario scenario;
    /**
     * Provided as a convenience; same as {@link EstatioScenario#getApp()}.
     */
    protected final EstatioApp app;
    
    public AbstractEstatioCukeStepDefs(EstatioScenario scenario) {
        this.scenario = scenario;
        this.app = scenario.getApp();
    }

    // //////////////////////////////////////

    /**
     * Convenience method to start transaction.
     * 
     * <p>
     * Cukes does not allow this to be annotated with {@link Before Cucumber's Before}
     * annotation.  Subclasses should therefore override, annotate, and delegate back up:
     * 
     * <pre>
     *  &#64;cucumber.api.java.Before
     *  &#64;Override
     *  public void beginTran() {
     *     super.beginTran();
     *  }
     * </pre>
     */
    public void beginTran() {
        app.beginTran();
    }

    /**
     * Convenience method to start transaction.
     * 
     * <p>
     * Cukes does not allow this to be annotated with {@link After Cucumber's After}
     * annotation.  Subclasses should therefore override, annotate, and delegate back up:
     * 
     * <pre>
     *  &#64;cucumber.api.java.After
     *  &#64;Override
     *  public void endTran(cucumber.api.Scenario sc) {
     *     super.endTran(sc);
     *  }
     * </pre>
     */
    public void endTran(cucumber.api.Scenario sc) {
        app.endTran(!sc.isFailed());
    }


}
