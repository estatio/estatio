/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
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

import org.junit.Assert;
import org.junit.Test;

public class TurnoverRentRuleHelperTest_isValid {

    @Test
    public void whenIsNot() {
        Assert.assertFalse(new TurnoverRentRuleHelper("50000;5").isValid());
        Assert.assertFalse(new TurnoverRentRuleHelper(null).isValid());
        Assert.assertFalse(new TurnoverRentRuleHelper("").isValid());
    }

    @Test
    public void whenIs() {
        Assert.assertTrue(new TurnoverRentRuleHelper("5").isValid());
        Assert.assertTrue(new TurnoverRentRuleHelper("5.00").isValid());
        Assert.assertTrue(new TurnoverRentRuleHelper("50000;5;7").isValid());
    }


}
