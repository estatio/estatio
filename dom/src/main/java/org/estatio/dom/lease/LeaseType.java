/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
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
package org.estatio.dom.lease;



// TODO: this needs to be made into an entity, so can make into a multi-tenanted entity:
//@javax.jdo.annotations.PersistenceCapable
public enum LeaseType {

    AA("Apparecchiature Automatic"),
    AD("Affitto d'Azienda"),
    CG("Comodato Gratuito"),
    CO("Comodato"),
    DH("Dehors"),
    LO("Locazione"),
    OA("Occup. Abusiva Affito"),
    OL("Occup. Abusiva Locazione"),
    PA("Progroga Affitto"),
    PL("Progroga Locazione"),
    PP("Pannelli Pubblicitari"),
    PR("Precaria"),
    SA("Scritt. Privata Affitto"),
    SL("Scritt. Privata Locazione");

    private final String title;

    private LeaseType(String title) {
        this.title = title;
    }

    public String title() {
        return title;
    }
    
    //TODO: Handle localised titles. 
    // 2013-04-13: still not need
    

}
