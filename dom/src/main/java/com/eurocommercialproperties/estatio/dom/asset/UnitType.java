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
package com.eurocommercialproperties.estatio.dom.asset;

/**
 * 
 * 
 * @version $Rev$ $Date$
 */
public enum UnitType {

    BOUTIQUE("Boutique"), MEDIUM("Medium"), HYPERMARKET("Hypermarket"), EXTERNAL("External"), OFFICE("Office"), PARKING("Parking");

    private final String title;

    UnitType(String title) {
        this.title = title;
    }

    public String title() {
        return title;
    }

}
