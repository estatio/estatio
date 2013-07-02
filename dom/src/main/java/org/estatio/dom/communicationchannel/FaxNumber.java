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
package org.estatio.dom.communicationchannel;

import javax.jdo.annotations.InheritanceStrategy;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Title;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
public class FaxNumber extends CommunicationChannel {

    @Override
    @Title
    public String getName() {
        return "Fax ".concat(getFaxNumber());
    }

    // //////////////////////////////////////

    private String faxNumber;

    @MemberOrder(sequence = "1")
    public String getFaxNumber() {
        return faxNumber;
    }

    public void setFaxNumber(final String number) {
        this.faxNumber = number;
    }

    public String disableFaxNumber() {
        return getStatus().isLocked() ? "Cannot modify when locked" : null;
    }

}
