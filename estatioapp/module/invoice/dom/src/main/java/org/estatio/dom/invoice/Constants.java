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
package org.estatio.dom.invoice;

import org.estatio.dom.party.role.IPartyRoleType;

public final class Constants {
    
    private Constants(){}

    // TODO: convert this into NumeratorData, similar to DocTypeData
    public static class NumeratorName {
        private NumeratorName(){}

        public static final String INVOICE_NUMBER = "Invoice number";
        public static final String COLLECTION_NUMBER = "Collection number";
    }

    public enum InvoiceRoleTypeEnum implements IPartyRoleType {

        BUYER,
        SELLER;

        @Override public String getKey() {
            return this.name();
        }
    }

}
