/**
 * or more
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
public enum Status implements TitledEnum, Lockable {

    /**
     * Object is in an unlocked status, and so (most of the data of this object) may be edited
     * eg to allow for fixing up of typos and other minor errors.
     * 
     * <p>
     * Some domain classes will allow objects in this state to be (physically) deleted.  RDBMS referential
     * integrity constraints are used to ensure that objects referenced elsewhere cannot be removed. 
     */
    UNLOCKED, 
    /**
     * The object's state is locked, and is mostly or completely immutable.
     * It can no longer be deleted when in this state.
     */
    LOCKED;

    @Override
    public String title() {
        return StringUtils.enumTitle(this.name());
    }

    @Override
    public boolean isUnlocked() {
        return this == UNLOCKED;
    }
    @Override
    public boolean isLocked() {
        return this == LOCKED;
    }

 }
