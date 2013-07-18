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
package org.estatio.integration.glue;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import cucumber.api.java.en.Then;

import org.junit.Assert;

import org.apache.isis.applib.services.wrapper.DisabledException;
import org.apache.isis.applib.services.wrapper.InvalidException;
import org.apache.isis.core.specsupport.specs.CukeGlueAbstract;

public class ExpectedExceptionsGlue extends CukeGlueAbstract {

    @Then("^.*disabled$")
    public void then_disabled() throws Throwable {
        
        nextTransaction();
        
        final Exception var = getVar("exception", "exception", Exception.class);
        if(var instanceof DisabledException) {
            // ok
        } else {
            Assert.fail("Expected DisabledException to have been thrown; was instead: " + var);
        }
    }

    @Then("^.*disabled with message \"([^\"]*)\"$")
    public void then_disabled_with_message(String message) throws Throwable {
        
        nextTransaction();

        final Exception var = getVar("exception", "exception", Exception.class);
        if(var instanceof DisabledException) {
            final DisabledException ex = (DisabledException) var;
            assertThat(ex.getMessage(), is(message));
        } else {
            Assert.fail("Expected DisabledException to have been thrown; was instead: " + var);
        }
    }

    @Then("^.*invalid$")
    public void then_invalid() throws Throwable {
        
        nextTransaction();

        final Exception var = getVar("exception", "exception", Exception.class);
        if(var instanceof InvalidException) {
            // ok
        } else {
            Assert.fail("Expected DisabledException to have been thrown; was instead: " + var);
        }
    }

    @Then("^.*invalid with message \"([^\"]*)\"$")
    public void then_invalid_with_message(String message) throws Throwable {

        nextTransaction();

        final Exception var = getVar("exception", "exception", Exception.class);
        if(var instanceof InvalidException) {
            final InvalidException ex = (InvalidException) var;
            assertThat(ex.getMessage(), is(message));
        } else {
            Assert.fail("Expected InvalidException to have been thrown; was instead: " + var);
        }
    }
    
}
