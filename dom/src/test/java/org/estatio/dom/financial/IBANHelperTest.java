/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
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
package org.estatio.dom.financial;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.estatio.dom.financial.utils.IBANHelper;

public class IBANHelperTest {

    @Before
    public void setup() {

    }
    
    @Test
    public void testDutchAccount() {
        IBANHelper ibanHelper = new IBANHelper("NL31ABNA0580744434");
        BankAccount ba = new BankAccount();
        ibanHelper.update(ba);
        Assert.assertThat(ba.getNationalBankCode(), Is.is("ABNA"));
        Assert.assertThat(ba.getAccountNumber(), Is.is("0580744434"));
    }

    @Test
    public void testItalianAccount() {
        IBANHelper ibanHelper = new IBANHelper("IT69N0347501601000051986922");
        BankAccount ba = new BankAccount();
        ibanHelper.update(ba);
        Assert.assertThat(ba.getNationalBankCode(), Is.is("03475"));
        Assert.assertThat(ba.getBranchCode(), Is.is("01601"));
        Assert.assertThat(ba.getAccountNumber(), Is.is("000051986922"));
    }

    
    @Test
    public void testEmptyAccount() {
        IBANHelper ibanHelper = new IBANHelper(null);
        BankAccount ba = new BankAccount();
        ibanHelper.update(ba);
        Assert.assertNull(ba.getNationalBankCode());
        Assert.assertNull(ba.getBranchCode());
        Assert.assertNull(ba.getAccountNumber());
    }

    
    @Test
    public void testFalseAccount() {
        IBANHelper ibanHelper = new IBANHelper("IT1231231");
        BankAccount ba = new BankAccount();
        ibanHelper.update(ba);
        Assert.assertNull(ba.getNationalBankCode());
        Assert.assertNull(ba.getBranchCode());
        Assert.assertNull(ba.getAccountNumber());
    }

}
