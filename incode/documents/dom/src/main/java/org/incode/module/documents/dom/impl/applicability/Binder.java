
/*
 *  Copyright 2016 Dan Haywood
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
package org.incode.module.documents.dom.impl.applicability;

import java.util.Arrays;
import java.util.List;

import org.apache.isis.applib.annotation.Programmatic;

import org.incode.module.documents.dom.impl.docs.DocumentTemplate;

import lombok.Getter;

/**
 * Implementation is responsible for creating the appropriate dataModel to feed into the supplied
 * {@link DocumentTemplate}, obtaining information from the supplied domainObject.
 *
 * <p>
 *     (Class name is) referenced by {@link Applicability#getBinderClassName()}.
 * </p>
 */
public interface Binder {

    public static class Binding {
        @Getter
        private final Object dataModel;
        @Getter
        private final List<Object> attachTo;

        public Binding(final Object dataModel, final List<Object> attachTo) {
            this.dataModel = dataModel;
            this.attachTo = attachTo;
        }

        public Binding(final Object dataModel, final Object... attachTo) {
            this.dataModel = dataModel;
            this.attachTo = Arrays.asList(attachTo);
        }
    }

    @Programmatic
    Binding newBinding(
            final DocumentTemplate documentTemplate,
            final Object domainObject);

}
