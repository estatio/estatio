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
package org.estatio.dom.tag;

import java.util.List;

import org.apache.isis.core.unittestsupport.comparable.ComparableContractTest_compareTo;


public class TagTest_compareTo extends ComparableContractTest_compareTo<Tag> {

    @SuppressWarnings("unchecked")
    @Override
    protected List<List<Tag>> orderedTuples() {
        return listOf(
                listOf(
                        newLeaseItem(null, null),
                        newLeaseItem("com.mycompany.Boo", null),
                        newLeaseItem("com.mycompany.Boo", null),
                        newLeaseItem("com.mycompany.Foo", null)
                        ),
                listOf(
                        newLeaseItem("com.mycompany.Boo", null),
                        newLeaseItem("com.mycompany.Boo", "Abc"),
                        newLeaseItem("com.mycompany.Boo", "Abc"),
                        newLeaseItem("com.mycompany.Foo", "Def")
                        )
                );
    }

    private Tag newLeaseItem(
            String appliesToClassName, 
            String name) {
        final Tag tag = new Tag();
        tag.setObjectType(appliesToClassName);
        tag.setName(name);
        return tag;
    }

}
