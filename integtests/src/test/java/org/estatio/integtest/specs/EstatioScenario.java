/*
 *  Copyright 2012-2013 Eurocommercial Properties NV
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
package org.estatio.integtest.specs;

import java.util.Map;

import com.google.common.collect.Maps;

import org.apache.isis.applib.util.ObjectContracts;


public class EstatioScenario  {

    private final EstatioApp app;

    // //////////////////////////////////////
    
    private static ThreadLocal<EstatioScenario> current = new ThreadLocal<EstatioScenario>();
    
    public static EstatioScenario current() {
        final EstatioScenario scenario = current.get();
        if(scenario == null) {
            throw new IllegalStateException("Scenario has not yet been instantiated by Cukes");
        } 
        return scenario;
    }

    /**
     * For instantiation by Cucumber-JVM only.
     * 
     * <p>
     * Annotated as deprecated to discourage accidental instantiation.
     */
    @Deprecated
    public EstatioScenario(EstatioApp app) {
        current.set(this);
        this.app = app;
    }
    
    
    public EstatioApp getApp() {
        return app;
    }
    
    // //////////////////////////////////////

    public static class VariableId {
        private final String type;
        private final String id;
        public VariableId(String type, String id) {
            this.type = type;
            this.id = id;
        }

        /**
         * eg 'lease'
         */
        public String getType() {
            return type;
        }
        /**
         * eg 'OXF-TOPMODEL-001'
         */
        public String getId() {
            return id;
        }
        @Override
        public int hashCode() {
            return ObjectContracts.hashCode(this, "type,id");
        }
        @Override
        public boolean equals(Object obj) {
            return ObjectContracts.equals(this, obj, "type,id");
        }
        @Override
        public String toString() {
            return ObjectContracts.toString(this, "type,id");
        }
    }

    private final Map<VariableId, Object> objectByVariableId = Maps.newLinkedHashMap();
    private final Map<String, Object> objectsById = Maps.newLinkedHashMap();
    
    private final Map<String, Object> mostRecent = Maps.newHashMap();
    

    
    public void put(String type, String id, Object value) {
        objectByVariableId.put(new VariableId(type, id), value);
        mostRecent.put(type, value);
    }

    /**
     * Retrieve an object previously used in the scenario.
     * 
     * <p>
     * Must specify type and/or id:
     * <ul>
     * <li>The type and id together constitute an unambiguous reference, eg 'agreement' 'lease-1'.
     * <li>The type by itself means to return the last reference to an object of that type, eg 'agreement'
     * <li>The id by itself means to return the last object (without the 'noise' of specifying its type), eg 'lease-1'.
     * </ul>
     * 
     * <p>
     * Because of this last rule, the id should be unique in and of itself.
     */
    public Object get(String type, String id) {
        if(type != null && id != null) {
            final VariableId variableId = new VariableId(type,id);
            final Object value = objectByVariableId.get(variableId);
            if(value != null) {
                mostRecent.put(type, value);
                return value;
            } 
            throw new IllegalStateException("No such " + variableId);
        }
        if(type != null && id == null) {
            return mostRecent.get(type);
        }
        if(type == null && id != null) {
            final Object value = objectsById.get(id);
            if(value != null) {
                mostRecent.put(type, value);
            } 
            return value;
        }
        throw new IllegalArgumentException("Must specify type and/or id");
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String type, String id, Class<T> cls) {
        return (T) get(type, id);
    }

}