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
package org.estatio.dom;

import org.estatio.dom.utils.StringUtils;

/**
 * Defines the lifecycle for all domain objects that do not define their own, 
 * more specialized lifecycles.
 * 
 * <p>
 * Irrespective of whether they use this {@link Status} type or not, the first
 * state should always be called <tt>NEW</tt>.
 */
public enum Status implements TitledEnum {

    /**
     * Object has just been created, and can be referenced by other NEW objects,
     * but may not be involved in 'critical' operations.
     * 
     * <p>
     * Much of the data on the referencing object may be mutable when in this state;
     * eg to allow for fixing up of typos and other minor errors.
     * 
     * <p>
     * Most entities allow objects in this state to be (physically) deleted.
     */
    NEW, 
    /**
     * The object's state has been checked, and should now be made (mostly) immutable.
     * It can no longer be deleted after this point.
     */
    CHECKED;

    public String title() {
        return StringUtils.enumTitle(this.name());
    }

    public boolean isNew() {
        return this == NEW;
    }
    public boolean isChecked() {
        return this == CHECKED;
    }

 }
