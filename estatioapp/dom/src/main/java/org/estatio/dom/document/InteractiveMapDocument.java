/*
 *  Copyright 2013~2014 Dan Haywood
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
package org.estatio.dom.document;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.value.Blob;

import org.estatio.dom.asset.FixedAsset;

@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = javax.jdo.annotations.IdGeneratorStrategy.IDENTITY,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Query(
        name = "findByFixedAsset", language = "JDOQL",
        value = "SELECT "
                + "FROM org.estatio.dom.document.InteractiveMapDocument "
                + "WHERE fixedAsset == :fixedAsset")
@Bookmarkable
@Named("Document")
public class InteractiveMapDocument implements Comparable<InteractiveMapDocument> {

    private String name;

    @Title()
    @Column(allowsNull = "false")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // //////////////////////////////////////

    private FixedAsset fixedAsset;

    @Column(allowsNull = "false")
    public FixedAsset getFixedAsset() {
        return fixedAsset;
    }

    public void setFixedAsset(FixedAsset fixedAsset) {
        this.fixedAsset = fixedAsset;
    }

    // //////////////////////////////////////

    @Persistent(defaultFetchGroup = "false")
    private Blob file;

    @Hidden
    @Optional
    public Blob getFile() {
        return file;
    }

    @Persistent
    public void setFile(Blob file) {
        this.file = file;
    }

    // //////////////////////////////////////

    public Blob download() {
        return getFile();
    }

    // //////////////////////////////////////

    @javax.inject.Inject
    @SuppressWarnings("unused")
    private ClockService clockService;

    // //////////////////////////////////////

    @Override
    public int compareTo(InteractiveMapDocument o) {
        return this.getName().compareTo(o.getName());
    }

}
