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
package com.eurocommercialproperties.estatio.api;

import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.value.Date;

public interface Api {

    public void country(@Named("code") String code, @Named("alpha2Code") String alpha2Code, @Named("name") String name);

    public void state(@Named("code") String state, @Named("name") String name, @Named("countryCode") String countryCode);

    public void owner(@Named("reference") String reference, @Named("name") String name);

    public void property(@Named("reference") String reference, @Named("name") String name, @Named("type") String type,
                    @Named("acquireDate") @Optional Date acquireDate,
                    @Named("disposalDate") @Optional Date disposalDate,
                    @Named("openingDate") @Optional Date openingDate,
                    @Named("ownerReference") @Optional String ownerReference);

    public void propertyPostalAddress(@Named("propertyReference") String propertyReference,
                    @Named("address1") String address1, @Named("address2") String address2,
                    @Named("postalCode") String postalCode, @Named("stateCode") String stateCode,
                    @Named("countryCode") String countryCode);

    public void propertyOwner(@Named("Reference") String reference, @Named("Reference") String ownerReference);

}
