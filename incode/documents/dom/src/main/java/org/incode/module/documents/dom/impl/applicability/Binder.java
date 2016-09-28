
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
    }

    /**
     *
     * @param documentTemplate - to which this binder implementation applies, as per {@link DocumentTemplate#getAppliesTo()} and {@link Applicability#getBinderClassName()}
     * @param domainObject - acting as the context for the binding, from which both the input {@link Binding#getDataModel() data model } and the objects {@link Binding#getAttachTo() to attach to} are inferred
     * @param additionalTextIfAny - optional text (eg for an email cover note) that may also be available to create the input data model
     */
    @Programmatic
    Binding newBinding(
            final DocumentTemplate documentTemplate,
            final Object domainObject,
            final String additionalTextIfAny);

}
