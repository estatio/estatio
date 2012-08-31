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

import org.apache.isis.applib.annotation.Idempotent;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;

import java.math.BigDecimal;
import org.joda.time.LocalDate;

public interface Api {

    @Idempotent
    public void putCountry(@Named("code") String code, @Named("alpha2Code") String alpha2Code, @Named("name") String name);

    @Idempotent
    public void putState(@Named("code") String state, @Named("name") String name, @Named("countryCode") String countryCode);

    @Idempotent
    public void putPerson(@Named("reference") String reference, @Named("initials") @Optional String initials, @Named("firstName") String name, @Named("lastName") String lastName);

    @Idempotent
    public void putOrganisation(@Named("reference") String reference, @Named("name") String name);

    @Idempotent
    public void putProperty(@Named("reference") String reference, @Named("name") String name, @Named("type") String type, @Named("acquireDate") @Optional LocalDate acquireDate, @Named("disposalDate") @Optional LocalDate disposalDate, @Named("openingDate") @Optional LocalDate openingDate,
            @Named("ownerReference") @Optional String ownerReference);

    @Idempotent
    public void putPropertyPostalAddress(@Named("propertyReference") String propertyReference, @Named("address1") @Optional String address1, @Named("address2") @Optional String address2, @Named("city") String city, @Named("postalCode") @Optional String postalCode,
            @Named("stateCode") @Optional String stateCode, @Named("countryCode") String countryCode);

    @Idempotent
    public void putPropertyOwner(@Named("Reference") String reference, @Named("Reference") String ownerReference);

    @Idempotent
    public void putPropertyActor(@Named("propertyReference") String propertyReference, @Named("partyReference") String partyReference, @Named("type") String type, @Named("from") @Optional LocalDate from, @Named("thru") @Optional LocalDate thru);

    @Idempotent
    public void putUnit(@Named("reference") String reference, @Named("propertyReference") String propertyReference, @Named("ownerReference") String ownerReference, @Named("name") String name, @Named("type") String type, @Named("from") @Optional LocalDate from, @Named("thru") @Optional LocalDate thru,
            @Named("area") @Optional BigDecimal area, @Named("salesArea") @Optional BigDecimal salesArea, @Named("storageArea") @Optional BigDecimal storageArea, @Named("mezzanineArea") @Optional BigDecimal mezzanineArea, @Named("terraceArea") @Optional BigDecimal terraceArea,
            @Named("address1") @Optional String address1, @Named("city") @Optional String city, @Named("postalCode") @Optional String postalCode, @Named("stateCode") @Optional String stateCode, @Named("countryCode") @Optional String countryCode);

}
