/*
 * Copyright 2015 Yodo Int. Projects and Consultancy
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.estatio.module.budgeting.dom.contributed;

import java.math.BigDecimal;

import org.junit.Test;

import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;
import org.estatio.dom.asset.Unit;
import org.estatio.module.budgeting.dom.keytable.KeyTable;
import org.estatio.module.budgeting.dom.keyitem.KeyItem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class KeyItem_Test {

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            final KeyItem pojo = new KeyItem();
            newPojoTester()
                    .withFixture(pojos(KeyTable.class, KeyTable.class))
                    .withFixture(pojos(Unit.class, Unit.class))
                    .exercise(pojo);
        }

    }

    @Test
    public void testChangeValue(){

        // given
        KeyTable table = new KeyTable();
        table.setPrecision(3);
        KeyItem item = new KeyItem();
        item.setValue(new BigDecimal(2));
        item.setKeyTable(table);
        assertTrue(item.getValue().equals(new BigDecimal(2)));

        //when
        item.changeValue(BigDecimal.valueOf(2.3335));

        //then
        assertEquals(item.getValue(),BigDecimal.valueOf(2.334).setScale(3, BigDecimal.ROUND_HALF_UP));

    }

    @Test
    public void testValidateChangeValue(){

        // given
        KeyTable table = new KeyTable();
        table.setPrecision(3);
        KeyItem item = new KeyItem();
        item.setValue(new BigDecimal(2));
        item.setKeyTable(table);
        assertTrue(item.getValue().equals(new BigDecimal(2)));

        //when, then
        assertEquals(item.validateChangeValue(BigDecimal.valueOf(-0.001)),"Value cannot be less than zero");
    }

    @Test
    public void testChangeAuditedValue(){

        // given
        KeyTable table = new KeyTable();
        table.setPrecision(3);
        KeyItem item = new KeyItem();
        item.setAuditedValue(new BigDecimal(2));
        item.setKeyTable(table);
        assertTrue(item.getAuditedValue().equals(new BigDecimal(2)));

        //when
        item.changeAuditedValue(BigDecimal.valueOf(2.3335));

        //then
        assertEquals(item.getAuditedValue(),BigDecimal.valueOf(2.334).setScale(3, BigDecimal.ROUND_HALF_UP));

    }

    @Test
    public void testValidateChangeAuditedValue(){

        // given
        KeyTable table = new KeyTable();
        table.setPrecision(3);
        KeyItem item = new KeyItem();
        item.setAuditedValue(new BigDecimal(2));
        item.setKeyTable(table);
        assertTrue(item.getAuditedValue().equals(new BigDecimal(2)));

        //when, then
        assertEquals(item.validateChangeAuditedValue(BigDecimal.valueOf(-0.001)),"Value cannot be less than zero");
    }

    @Test
    public void testChangeSourceValue(){

        // given
        KeyTable table = new KeyTable();
        table.setPrecision(3);
        KeyItem item = new KeyItem();
        item.setSourceValue(new BigDecimal(2));
        item.setKeyTable(table);
        assertTrue(item.getSourceValue().equals(new BigDecimal(2)));

        //when
        item.changeSourceValue(BigDecimal.valueOf(2.335));

        //then
        assertEquals(item.getSourceValue(),BigDecimal.valueOf(2.34).setScale(2, BigDecimal.ROUND_HALF_UP));

    }

    @Test
    public void testValidateChangeSourceValue(){

        // given
        KeyTable table = new KeyTable();
        table.setPrecision(3);
        KeyItem item = new KeyItem();
        item.setSourceValue(new BigDecimal(2));
        item.setKeyTable(table);
        assertTrue(item.getSourceValue().equals(new BigDecimal(2)));

        //when, then
        assertEquals(item.validateChangeSourceValue(BigDecimal.valueOf(-0.001)),"Source Value must be positive");
        assertEquals(item.validateChangeSourceValue(BigDecimal.ZERO),null);
    }

}
