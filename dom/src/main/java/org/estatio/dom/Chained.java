/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
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
package org.estatio.dom;

import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Where;

public interface Chained<T extends Chained<T>> {

    
    /**
     * The object (usually an {@link WithInterval}, but not necessarily) that precedes this one, if any (not 
     * necessarily contiguously)..
     * 
     * <p>
     * Implementations where successive intervals are contiguous should instead implement 
     * {@link WithIntervalContiguous}.
     */
    @Hidden(where=Where.ALL_TABLES)
    @Disabled
    @Optional
    public T getPrevious();

    /**
     * The object (usually an {@link WithInterval}, but not necessarily) that succeeds this one, if any (not 
     * necessarily contiguously).
     * 
     * <p>
     * Implementations where successive intervals are contiguous should instead implement 
     * {@link WithIntervalContiguous}.
     */
    @Hidden(where=Where.ALL_TABLES)
    @Disabled
    @Optional
    public T getNext();
    
    
}
