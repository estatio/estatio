/*
 *
 *  Copyright 2012-2015 Eurocommercial Properties NV
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

package org.estatio.dom.asset.ownership;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Where;

import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.asset.OwnershipType;
import org.estatio.dom.party.Party;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "dbo"  // Isis' ObjectSpecId inferred from @DomainObject#objectType
)
@Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version"
)
@DomainObject(
        objectType = "org.estatio.dom.asset.ownership.FixedAssetOwnership"
)
public class FixedAssetOwnership implements Comparable<FixedAssetOwnership> {

    @Column(allowsNull = "false")
    @Getter @Setter
    private Party owner;

    @PropertyLayout(hidden = Where.ALL_TABLES)
    @Column(allowsNull = "false")
    @Getter @Setter
    private FixedAsset fixedAsset;

    @Column(allowsNull = "false")
    @Getter @Setter
    private OwnershipType ownershipType;

    public FixedAssetOwnership changeOwnershipType(final OwnershipType newOwnershipType) {
        setOwnershipType(newOwnershipType);
        return this;
    }

    public OwnershipType default0ChangeOwnershipType() {
        return getOwnershipType();
    }

    @Override
    public int compareTo(final FixedAssetOwnership o) {
        return this.getOwner().compareTo(o.getOwner());
    }
}
