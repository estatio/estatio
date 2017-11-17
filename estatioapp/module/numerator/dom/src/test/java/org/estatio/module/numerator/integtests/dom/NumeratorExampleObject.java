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
package org.estatio.module.numerator.integtests.dom;

import javax.jdo.annotations.IdentityType;

import org.apache.isis.applib.annotation.Auditing;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.util.ObjectContracts;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

// TODO: need to move this back to src/test/java
@javax.jdo.annotations.PersistenceCapable(
        identityType=IdentityType.DATASTORE,
        schema = "simple",
        table = "NumeratorExampleObject"
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy=javax.jdo.annotations.IdGeneratorStrategy.IDENTITY,
         column="id")
@javax.jdo.annotations.Queries({
})
@javax.jdo.annotations.Unique(name="NumeratorExampleObject_name_UNQ", members = {"name"})
@DomainObject(
        objectType = "simple.NumeratorExampleObject",
        auditing = Auditing.ENABLED,
        publishing = Publishing.ENABLED
)
@EqualsAndHashCode(of = "name")
@Builder
public class NumeratorExampleObject implements Comparable<NumeratorExampleObject> {


    @javax.jdo.annotations.Column(allowsNull = "false", length = 40)
    @Getter @Setter
    @Title
    private String name;

    @Override
    public String toString() {
        return ObjectContracts.toString(this, "name");
    }

    @Override
    public int compareTo(final NumeratorExampleObject other) {
        return ObjectContracts.compare(this, other, "name");
    }

}